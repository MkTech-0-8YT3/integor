package ztp.labs.filemanager.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DownloadRequestDto {

    String url;
    Boolean allowRetry;
    int maxNumberOfRetries;

    public static DownloadRequestDto from(DownloadUrlDto urlDto) {
        return DownloadRequestDto.builder()
                .url(urlDto.getDownloadUrl())
                .allowRetry(true)
                .maxNumberOfRetries(3)
                .build();
    }

}
