package ztp.labs.integrityvalidator.dto;

import lombok.Value;
import reactor.util.annotation.Nullable;

@Value
public class ChecksumCrawlingResult {

    Boolean wasChecksumResolved;

    @Nullable
    String checksum;

}

