package ztp.labs.filemanager.dto;

import lombok.Value;

import java.util.UUID;

@Value
public class FileIntegrityRequestDto {

    UUID fileId;
    String fileDownloadUrl;
    String originalFileName;

    public static FileIntegrityRequestDto from(ResolvedFileInfoDto fileInfoDto) {
        return new FileIntegrityRequestDto(fileInfoDto.getFileId(), fileInfoDto.getDownloadUrl(), fileInfoDto.getFileName());
    }

}
