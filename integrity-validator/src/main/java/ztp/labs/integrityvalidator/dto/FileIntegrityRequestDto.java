package ztp.labs.integrityvalidator.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.net.URL;
import java.util.UUID;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class FileIntegrityRequestDto {

    UUID fileId;
    URL fileDownloadUrl;
    String originalFileName;

}
