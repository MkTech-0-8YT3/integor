package ztp.labs.filemanager.dto;

import lombok.Builder;
import lombok.Data;
import lombok.With;

@Data
@Builder
@With
public class FileProcessingContext {

    private FileIntegrityRequestDto fileIntegrityRequestDto;
    private DownloadRequestDto downloadRequestDto;
    private ResolvedFileInfoDto resolvedFileInfoDto;
    private Boolean checksumsMatch;

    public static FileProcessingContext init(DownloadRequestDto downloadRequestDto) {
        return FileProcessingContext.builder()
                .downloadRequestDto(downloadRequestDto)
                .fileIntegrityRequestDto(null)
                .resolvedFileInfoDto(null)
                .build();

    }

}
