package ee.aleksale.paymentimpl.config;

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
public class PaymentConfig {
    private String orderSystemSecret;

    @Bean
    public CommerceSecretCallInterceptor commerceSecretCallInterceptor() {
        return new CommerceSecretCallInterceptor(orderSystemSecret);
    }
}
