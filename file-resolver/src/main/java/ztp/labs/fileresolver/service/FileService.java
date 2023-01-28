package ztp.labs.fileresolver.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ztp.labs.fileresolver.connector.InternalResolverService;
import ztp.labs.fileresolver.dto.DownloadDetails;
import ztp.labs.fileresolver.dto.FileDto;
import ztp.labs.fileresolver.dto.ResolvedFileInfoDto;
import ztp.labs.fileresolver.util.FilePathUtils;
import ztp.labs.fileresolver.util.UrlUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@Log4j2
public class FileService {

    private final InternalResolverService internalResolverService;

    @Value("${file.storage.directory}")
    private String storageDirectory;

    @Autowired
    public FileService(InternalResolverService internalResolverService) {
        this.internalResolverService = internalResolverService;
    }

    public Mono<ResolvedFileInfoDto> resolveFileExternal(DownloadDetails downloadDetails) {
        log.info("Handling request to resolve file from: {}", downloadDetails.getUrl());
        return resolveFile(downloadDetails)
                .map(ResolvedFileInfoDto::from);
    }

    public Mono<FileDto> resolveFile(DownloadDetails downloadDetails) {
        return saveBufferToFile(internalResolverService.downloadFileFromUrl(downloadDetails))
                .doOnNext(r -> log.info("File: {} downloaded & saved successfully as: {}",
                        UrlUtils.getFilenameFromUrl(downloadDetails.getUrl()), r.getFileName()))
                .map(path -> constructResult(path, downloadDetails));
    }

    private FileDto constructResult(Path path, DownloadDetails details) {
        File file = new File(path.toString());
        return FileDto.builder()
                .content(readFile(path))
                .fileId(UUID.fromString(file.getName()))
                .downloadDate(Date.from(Instant.now()))
                .downloadUrl(details.getUrl())
                .fileName(UrlUtils.getFilenameFromUrl(details.getUrl()))
                .size(file.length())
                .build();
    }

    private byte[] readFile(Path path) {
        try (var reader = new FileInputStream(path.toString())) {
            return reader.readAllBytes();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Mono<Path> saveBufferToFile(Flux<DataBuffer> downloadFileBuffer) {
        return Mono.fromCallable(() -> saveFileToDisk(downloadFileBuffer, FilePathUtils.generateFileLocation(storageDirectory)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Warning: Blocking call
     */
    private Path saveFileToDisk(Flux<DataBuffer> downloadFileBuffer, Path fileLocation) {
        log.info("Saving file byte buffer to disk in location: {}", fileLocation.toString());
        DataBufferUtils
                .write(downloadFileBuffer, fileLocation, StandardOpenOption.CREATE)
                .share()
                .block();
        log.info("Saved file: {}", fileLocation.getFileName());
        return fileLocation;
    }

}
