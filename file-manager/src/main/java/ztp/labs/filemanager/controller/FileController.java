package ztp.labs.filemanager.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ztp.labs.filemanager.dto.DownloadUrlDto;
import ztp.labs.filemanager.dto.ResolvedFileResponseDto;
import ztp.labs.filemanager.service.FileManagementService;

@RestController
public class FileController {

    private final FileManagementService fileManagementService;

    public FileController(FileManagementService fileManagementService) {
        this.fileManagementService = fileManagementService;
    }

    @PostMapping("/resolve")
    public Mono<ResolvedFileResponseDto> resolveFile(@RequestBody DownloadUrlDto downloadUrlDto) {
        return fileManagementService.downloadAndValidateFile(downloadUrlDto);
    }

}
