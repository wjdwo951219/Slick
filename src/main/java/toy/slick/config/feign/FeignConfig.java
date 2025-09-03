package toy.slick.config.feign;

import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;

@Configuration
public class FeignConfig {

    @Primary
    @Bean
    public Retryer defaultRetryer() {
        return FeignRetryerGenerator.create(Duration.ofSeconds(1), 3);
    }

    @Configuration
    public static class Slow {
        @Bean
        public Retryer slowRetryer() {
            return FeignRetryerGenerator.create(Duration.ofSeconds(10), Duration.ofSeconds(30), 5);
        }
    }
}
