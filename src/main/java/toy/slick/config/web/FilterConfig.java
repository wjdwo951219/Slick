package toy.slick.config.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import toy.slick.filter.RequestLoggingFilter;

@Configuration
public class FilterConfig {
    // TODO: exception message -> property
    // TODO: Bucket4j

    @Bean
    public RequestLoggingFilter requestLoggingFilter() {
        RequestLoggingFilter filter = new RequestLoggingFilter();

        filter.setIncludeClientInfo(true);
        filter.setIncludeHeaders(true);
        filter.setIncludePayload(true);
        filter.setIncludeQueryString(true);

        return filter;
    }
}
