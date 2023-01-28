package ztp.labs.integrityvalidator.dto;

import lombok.Builder;
import lombok.Data;
import lombok.With;

import java.io.File;
import java.nio.file.Path;

@Data
@Builder
@With
public class LocalFileIntegrityData {

    Path storageFilePath;
    File file;
    String SHA256Checksum;
    String SHA512Checksum;

    public static LocalFileIntegrityData init(Path path) {
        return LocalFileIntegrityData.builder()
                .storageFilePath(path)
                .file(new File(path.toString()))
                .build();
    }

}
