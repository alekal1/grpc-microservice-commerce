package ee.aleksale.client.config;

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
@ConfigurationProperties("credentials")
public class ClientConfig {
    private String managementSystemSecret;
    private String clientSystemSecret;

    @Bean
    public CommerceSecretCallInterceptor commerceSecretCallInterceptor() {
        return new CommerceSecretCallInterceptor(managementSystemSecret);
    }

    @Bean
    public CommerceSecretCallCredentials commerceSecretCallCredentials() {
        return new CommerceSecretCallCredentials(clientSystemSecret);
    }
}
