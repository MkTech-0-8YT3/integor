package ztp.labs.filemanager.connector;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ztp.labs.filemanager.dto.DownloadRequestDto;
import ztp.labs.filemanager.dto.ResolvedFileInfoDto;

@Component
public class FileResolverConnector {

    private final WebClient webClient;

    public FileResolverConnector(WebClient.Builder webClientBuilder,
                                 @Value("${services.file-resolver.address}") String baseUrl) {
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    public Mono<ResolvedFileInfoDto> downloadFile(DownloadRequestDto requestDto) {

        return webClient
                .post()
                .uri("/file/download")
                .body(BodyInserters.fromValue(requestDto))
                .retrieve()
                .bodyToMono(ResolvedFileInfoDto.class);
    }

}
