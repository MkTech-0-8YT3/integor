package ztp.labs.fileresolver.dto;

import lombok.Value;

import java.util.Date;
import java.util.UUID;

@Value
public class ResolvedFileInfoDto {

    UUID fileId;
    String downloadUrl;
    Date downloadDate;
    String fileName;
    long size;

    public static ResolvedFileInfoDto from(FileDto fileDto) {
        return new ResolvedFileInfoDto(fileDto.getFileId(), fileDto.getDownloadUrl(),
                fileDto.getDownloadDate(), fileDto.getFileName(), fileDto.getSize());
    }

}
