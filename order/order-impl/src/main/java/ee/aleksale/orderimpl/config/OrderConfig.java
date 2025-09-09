package ee.aleksale.orderimpl.config;

import ee.aleksale.credentials.CommerceSecretCallCredentials;
import ee.aleksale.credentials.CommerceSecretCallInterceptor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "credentials")
public class OrderConfig {
    private String orderSystemSecret;
    private String clientSystemToken;

    @Bean
    public CommerceSecretCallCredentials secretCallCredentials() {
        return new CommerceSecretCallCredentials(orderSystemSecret);
    }

    @Bean
    public CommerceSecretCallInterceptor secretCallInterceptor() {
        return new CommerceSecretCallInterceptor(clientSystemToken);
    }
}
