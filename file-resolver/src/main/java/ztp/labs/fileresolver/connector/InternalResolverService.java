package ztp.labs.fileresolver.connector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;
import ztp.labs.fileresolver.configuration.UserAgentSpoofProperties;
import ztp.labs.fileresolver.dto.DownloadDetails;
import ztp.labs.fileresolver.util.NumericUtils;

@Service
public class InternalResolverService {

    private static final String USER_AGENT = "User-Agent";

    private WebClient webClient;
    private UserAgentSpoofProperties spoofProperties;

    @Autowired
    public InternalResolverService resolverService(UserAgentSpoofProperties spoofProperties) {
        this.spoofProperties = spoofProperties;
        webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))
                .build();
        return this;
    }

    public Flux<DataBuffer> downloadFileFromUrl(DownloadDetails downloadDetails) {
        return webClient.get()
                .uri(downloadDetails.getUrl())
                .header(USER_AGENT, getUserAgent())
                .retrieve()
                .bodyToFlux(DataBuffer.class);
    }

    private String getUserAgent() {
        return spoofProperties.getAllowRandom()
                ? spoofProperties.getUserAgents().get(NumericUtils.getRandom(spoofProperties.getUserAgents().size() - 1))
                : spoofProperties.getUserAgents().get(0);
    }

}
