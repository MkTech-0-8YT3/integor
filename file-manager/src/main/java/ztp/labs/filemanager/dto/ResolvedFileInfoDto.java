package ztp.labs.filemanager.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Date;
import java.util.UUID;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ResolvedFileInfoDto {

    UUID fileId;
    String downloadUrl;
    Date downloadDate;
    String fileName;
    long size;

}