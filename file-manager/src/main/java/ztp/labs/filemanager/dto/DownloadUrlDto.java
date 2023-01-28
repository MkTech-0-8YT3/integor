package ztp.labs.filemanager.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class DownloadUrlDto {

    String downloadUrl;

}
