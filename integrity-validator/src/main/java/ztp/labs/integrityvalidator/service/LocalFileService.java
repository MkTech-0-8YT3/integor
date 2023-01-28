package ztp.labs.integrityvalidator.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ztp.labs.integrityvalidator.dto.LocalFileIntegrityData;
import ztp.labs.integrityvalidator.util.FilePathUtils;
import ztp.labs.integrityvalidator.util.HashUtils;

import java.util.UUID;

@Service
@Log4j2
public class LocalFileService {

    @Value("${file.storage.directory}")
    private String storageDirectory;

    public Mono<LocalFileIntegrityData> getFileIntegrityData(UUID fileId) {

        return Mono.just(LocalFileIntegrityData.init(FilePathUtils.getFileLocation(storageDirectory, fileId)))
                .flatMap(this::extendFileIntegrityDataWithSHA256Checksum)
                .flatMap(this::extendFileIntegrityDataWithSHA512Checksum);
    }

    private Mono<LocalFileIntegrityData> extendFileIntegrityDataWithSHA256Checksum(LocalFileIntegrityData localFileIntegrityData) {
        return Mono.just(localFileIntegrityData)
                .zipWhen(integData -> getFileChecksum(integData, HashUtils.HashingAlgo.SHA256),
                        LocalFileIntegrityData::withSHA256Checksum);
    }

    private Mono<LocalFileIntegrityData> extendFileIntegrityDataWithSHA512Checksum(LocalFileIntegrityData localFileIntegrityData) {
        return Mono.just(localFileIntegrityData)
                .zipWhen(integData -> getFileChecksum(integData, HashUtils.HashingAlgo.SHA512),
                        LocalFileIntegrityData::withSHA512Checksum);
    }

    private Mono<String> getFileChecksum(LocalFileIntegrityData fileIntegrityData, HashUtils.HashingAlgo algorithm) {
        return Mono.defer(() ->Mono.just(HashUtils.getFileHash(fileIntegrityData.getFile(), algorithm)))
                .doOnNext(r -> log.info("Extending LocalFileIntegrityData of file: {} with {} checksum: {}",
                        fileIntegrityData.getFile().getName(), algorithm.name(), r));
    }

}
