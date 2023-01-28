package ztp.labs.filemanager.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ztp.labs.filemanager.connector.FileResolverConnector;
import ztp.labs.filemanager.connector.IntegrityValidatorConnector;
import ztp.labs.filemanager.dto.DownloadRequestDto;
import ztp.labs.filemanager.dto.DownloadUrlDto;
import ztp.labs.filemanager.dto.FileIntegrityRequestDto;
import ztp.labs.filemanager.dto.FileProcessingContext;
import ztp.labs.filemanager.dto.ResolvedFileResponseDto;

@Service
@AllArgsConstructor
@Log4j2
public class FileManagementService {

    private final FileResolverConnector fileResolverConnector;
    private final IntegrityValidatorConnector integrityValidatorConnector;

    public Mono<ResolvedFileResponseDto> downloadAndValidateFile(DownloadUrlDto downloadUrlDto) {
        return Mono.just(FileProcessingContext.init(DownloadRequestDto.from(downloadUrlDto)))
                .flatMap(this::downloadFile)
                .flatMap(this::extendContextWithFileIntegrityRequestDto)
                .flatMap(this::validateFileIntegrity)
                .flatMap(this::finalizeContextProcessing);
    }

    private Mono<FileProcessingContext> downloadFile(FileProcessingContext context) {
        return Mono.defer(() -> Mono.just(context))
                .doOnNext(ctx -> log.info("Requesting file download from: {}", ctx.getDownloadRequestDto().getUrl()))
                .flatMap(ctx -> fileResolverConnector.downloadFile(ctx.getDownloadRequestDto()))
                .map(context::withResolvedFileInfoDto);
    }

    private Mono<FileProcessingContext> extendContextWithFileIntegrityRequestDto(FileProcessingContext context) {
        return Mono.defer(() -> Mono.just(context))
                .map(ctx -> FileIntegrityRequestDto.from(ctx.getResolvedFileInfoDto()))
                .map(context::withFileIntegrityRequestDto);
    }

    private Mono<FileProcessingContext> validateFileIntegrity(FileProcessingContext context) {
        return Mono.defer(() -> Mono.just(context))
                .doOnNext(ctx -> log.info("Requesting integrity validation for file: {}, with original name: {}",
                        ctx.getFileIntegrityRequestDto().getOriginalFileName(), ctx.getFileIntegrityRequestDto().getFileId()))
                .flatMap(ctx -> integrityValidatorConnector.checkFileIntegrity(ctx.getFileIntegrityRequestDto()))
                .map(context::withChecksumsMatch);
    }

    private Mono<ResolvedFileResponseDto> finalizeContextProcessing(FileProcessingContext context) {
        return Mono.defer(() -> Mono.just(context))
                .map(ResolvedFileResponseDto::from);
    }

}
