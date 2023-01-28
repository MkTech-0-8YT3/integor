package ztp.labs.integrityvalidator.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import ztp.labs.integrityvalidator.connector.WebCrawler;
import ztp.labs.integrityvalidator.dto.CrawlingContext;
import ztp.labs.integrityvalidator.dto.FileIntegrityRequestDto;
import ztp.labs.integrityvalidator.dto.LocalFileIntegrityData;
import ztp.labs.integrityvalidator.util.HashUtils;
import ztp.labs.integrityvalidator.util.UrlUtils;

import java.net.URL;
import java.util.List;

@Service
@AllArgsConstructor
@Log4j2
public class FileIntegrityService {

    private WebCrawler webCrawler;
    private LocalFileService localFileService;

    public Mono<Boolean> isChecksumValid(FileIntegrityRequestDto fileIntegrityRequestDto) {
        return tryResolveChecksumForDownloadableFile(fileIntegrityRequestDto.getFileDownloadUrl())
                .zipWith(localFileService.getFileIntegrityData(fileIntegrityRequestDto.getFileId()))
                .doOnNext(t -> log.debug("Validating context {} against {}", t.getT1(), t.getT2()))
                .flatMap(this::validateRemoteChecksumAgainstLocal)
                .doOnNext(r -> logChecksumValidationResult(r, fileIntegrityRequestDto.getOriginalFileName()));
    }

    private Mono<CrawlingContext> tryResolveChecksumForDownloadableFile(URL url) {
        return Mono.just(CrawlingContext.init(url))
                .flatMap(this::extendContextWithPossibleChecksumFiles)
                .flatMap(this::tryResolveChecksum);
    }

    private Mono<CrawlingContext> tryResolveChecksum(CrawlingContext context) {
        return Flux.fromIterable(context.getUrlsToCrawl())
                .doOnNext(u -> log.info("Using webCrawler to resolve content from: {}", u))
                .flatMap(u -> webCrawler.resolveExternalContentToString(u))
                .collectList()
                .flatMap(l -> analyzeListOfDownloadedData(l, context));
    }

    private Mono<CrawlingContext> analyzeListOfDownloadedData(List<String> content, CrawlingContext context) {
        return Mono.defer(() -> Mono.just(context))
                .map(ctx -> {
                    content.forEach(c -> {
                        var lines = c.split("\n");
                        for (var line : lines) {
                            var processedLine = line.trim();
                            tryResolveFileChecksums(processedLine, ctx, processedLine.matches(".*\\s+.*"));
                        }
                    });
                    return ctx;
                });
    }

    private void tryResolveFileChecksums(String line, CrawlingContext context, Boolean lineHasWhiteSpaceSeparators) {
        tryResolveFileSHA256Checksum(line, context, lineHasWhiteSpaceSeparators);
        tryResolveFileSHA512Checksum(line, context, lineHasWhiteSpaceSeparators);
    }

    private void tryResolveFileSHA256Checksum(String line, CrawlingContext context, Boolean lineHasWhiteSpaceSeparators) {
        log.debug("Trying to resolve SHA256 checksum from line: {}", line);
        var sha256Candidate = HashUtils.tryResolveSHA256ChecksumFromLine(line, lineHasWhiteSpaceSeparators);

        if(sha256Candidate.isPresent()) {
            log.info("Resolved SHA256 checksum: {} from line: {}", sha256Candidate.get().toLowerCase(), line);
            context.setCanResolveSHA256Checksum(true);
            context.setResolvedSHA256Checksum(sha256Candidate.get().toLowerCase());
        }
    }

    private void tryResolveFileSHA512Checksum(String line, CrawlingContext context, Boolean lineHasWhiteSpaceSeparators) {
        log.debug("Trying to resolve SHA512 checksum from line: {}", line);
        var sha512Candidate = HashUtils.tryResolveSHA512ChecksumFromLine(line, lineHasWhiteSpaceSeparators);

        if(sha512Candidate.isPresent()) {
            log.info("Resolved SHA512 checksum: {} from line: {}", sha512Candidate.get().toLowerCase(), line);
            context.setCanResolveSHA512Checksum(true);
            context.setResolvedSHA512Checksum(sha512Candidate.get().toLowerCase());
        }
    }

    private Mono<CrawlingContext> extendContextWithPossibleChecksumFiles(CrawlingContext crawlingContext) {
        return Mono.just(crawlingContext)
                .zipWhen(ctx -> getListOfPossibleChecksumFiles(ctx.getBaseUrl()))
                .doOnNext(tup -> tup.getT1().addUrlsToCrawl(tup.getT2()))
                .map(Tuple2::getT1);
    }

    private Mono<List<URL>> getListOfPossibleChecksumFiles(URL url) {
        return Flux.fromIterable(UrlUtils.generatePotentialUrlsWithChecksumFile(url))
                .map(UrlUtils::getUrlFromString)
                .doOnNext(u -> log.debug("Generated checksum file url: {}", u))
                .collectList();
    }

    private Mono<Boolean> validateRemoteChecksumAgainstLocal(Tuple2<CrawlingContext, LocalFileIntegrityData> remoteLocalResults) {
        return Mono.just(remoteLocalResults)
                .filter(r -> r.getT1().getCanResolveSHA256Checksum() || r.getT1().getCanResolveSHA512Checksum())
                .map(r -> {
                    if(r.getT1().getCanResolveSHA256Checksum()) {
                        log.info("Could successfully resolve SHA256 checksum from remote");
                        return r.getT1().getResolvedSHA256Checksum().equals(r.getT2().getSHA256Checksum());
                    } else if (r.getT1().getCanResolveSHA512Checksum()) {
                        log.info("Could successfully resolve SHA512 checksum from remote");
                        return r.getT1().getResolvedSHA512Checksum().equals(r.getT2().getSHA512Checksum());
                    }
                    log.info("Could not resolve SHA256/SHA512 file from remote.");
                    return false;
                });
    }

    private void logChecksumValidationResult(Boolean isChecksumValid, String filename) {
        log.info(isChecksumValid
                ? "Checksum validation for file {} result: SUCCESS"
                : "Checksum validation for file {} status: FAILED",
                filename);
    }

}
