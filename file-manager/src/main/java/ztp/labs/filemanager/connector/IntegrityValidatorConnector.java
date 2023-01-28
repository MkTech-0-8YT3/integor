package ztp.labs.filemanager.connector;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ztp.labs.filemanager.dto.FileIntegrityRequestDto;

@Component
@Log4j2
public class IntegrityValidatorConnector {

    private final WebClient webClient;

    public IntegrityValidatorConnector(WebClient.Builder webClientBuilder,
                                       @Value("${services.integrity-validator.address}") String baseUrl) {
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    public Mono<Boolean> checkFileIntegrity(FileIntegrityRequestDto requestDto) {
        log.info(requestDto);
        return webClient
                .post()
                .uri("/validate")
                .body(BodyInserters.fromValue(requestDto))
                .retrieve()
                .bodyToMono(Boolean.class);
    }

}