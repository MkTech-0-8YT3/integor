package ztp.labs.filemanager.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;

@EnableCaching
@Configuration
public class CacheConfig {

    @Value("${config.cache.expiration}")
    private int cacheExpirationSeconds;

    @Bean
    @Primary
    public CaffeineCacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder().expireAfterWrite(Duration.ofSeconds(cacheExpirationSeconds)));
        return cacheManager;
    }

}
