package ztp.labs.fileresolver.dto;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class DownloadDetails {

    String url;
    Boolean allowRetry;
    int maxNumberOfRetries;

}
