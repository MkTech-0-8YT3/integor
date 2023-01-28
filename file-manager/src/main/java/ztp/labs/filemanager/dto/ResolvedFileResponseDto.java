package ztp.labs.filemanager.dto;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class ResolvedFileResponseDto {

    ResolvedFileInfoDto fileInformation;
    Boolean isFileValid;

    public static ResolvedFileResponseDto from(FileProcessingContext context) {
        return new ResolvedFileResponseDto(context.getResolvedFileInfoDto(), context.getChecksumsMatch());
    }

}
