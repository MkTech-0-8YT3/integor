package ztp.labs.integrityvalidator.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "spoofer")
@Getter
@Setter
public class UserAgentSpoofProperties {

    private List<String> userAgents;
    private Boolean allowRandom = true;

}

