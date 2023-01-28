package ztp.labs.fileresolver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ztp.labs.fileresolver.dto.DownloadDetails;
import ztp.labs.fileresolver.dto.FileDto;
import ztp.labs.fileresolver.dto.ResolvedFileInfoDto;
import ztp.labs.fileresolver.service.FileService;

@RestController
public class FileResolverController {

    private final FileService fileService;

    public FileResolverController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/file/download")
    public Mono<ResolvedFileInfoDto> downloadFile(
            @RequestBody DownloadDetails downloadDetails) {
        return fileService.resolveFileExternal(downloadDetails);
    }

    @PostMapping("/file/download/get-file")
    public Mono<FileDto> downloadAndGetFileContent(
            @RequestBody DownloadDetails downloadDetails) {
        return fileService.resolveFile(downloadDetails);
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

}
