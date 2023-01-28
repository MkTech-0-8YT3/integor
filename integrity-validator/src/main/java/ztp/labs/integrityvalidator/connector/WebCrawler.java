package ztp.labs.integrityvalidator.connector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import ztp.labs.integrityvalidator.config.UserAgentSpoofProperties;
import ztp.labs.integrityvalidator.util.NumericUtils;

import java.net.URL;

@Component
public class WebCrawler {

    private static final String USER_AGENT = "User-Agent";

    private WebClient webClient;
    private UserAgentSpoofProperties spoofProperties;

    @Autowired
    public WebCrawler(WebClient.Builder webClientBuilder, UserAgentSpoofProperties spoofProperties) {
        this.spoofProperties = spoofProperties;
        webClient = webClientBuilder
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))
                .build();
    }

    public Mono<HttpStatusCode> headStatusCode(URL url) {
        return webClient.head()
                .uri(url.toString())
                .exchangeToMono(resp -> Mono.just(resp.statusCode()));
    }

    public Mono<String> resolveExternalContentToString(URL url) {
        return webClient.get()
                .uri(url.toString())
                .header(USER_AGENT, getUserAgent())
                .retrieve()
                .bodyToMono(String.class)
                .onErrorComplete();
    }

    private String getUserAgent() {
        return spoofProperties.getAllowRandom()
                ? spoofProperties.getUserAgents().get(NumericUtils.getRandom(spoofProperties.getUserAgents().size() - 1))
                : spoofProperties.getUserAgents().get(0);
    }

}
