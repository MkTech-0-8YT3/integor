package ztp.labs.fileresolver.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
public class FileDto {

    private UUID fileId;
    private String downloadUrl;
    private Date downloadDate;
    private String fileName;
    private long size;
    private byte[] content;

}
