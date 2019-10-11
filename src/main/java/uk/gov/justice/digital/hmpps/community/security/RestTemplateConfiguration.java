package uk.gov.justice.digital.hmpps.community.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import uk.gov.justice.digital.hmpps.community.utils.W3cTracingInterceptor;

import java.time.Duration;

@Slf4j
@Configuration
public class RestTemplateConfiguration {
    @Value("${delius.endpoint.url}")
    private String deliusApiRootUri;

    @Value("${delius.api.username}")
    private String deliusUsername;

    @Value("${delius.api.ping-timeout:1s}")
    private Duration pingTimeout;

    @Value("${delius.api.timeout:30s}")
    private Duration apiTimeout;

    @Bean(name = "deliusApiHealthRestTemplate")
    public RestTemplate deliusApiHealthRestTemplate(final RestTemplateBuilder restTemplateBuilder) {
        log.info("* * * Creating Delius health rest template with URL {}", deliusApiRootUri);
        return restTemplateBuilder
                .rootUri(deliusApiRootUri)
                .additionalInterceptors(new W3cTracingInterceptor())
                .setConnectTimeout(pingTimeout)
                .setReadTimeout(pingTimeout)
                .build();
    }

    @Bean(name = "deliusApiResourceRestTemplate")
    public RestTemplate deliusApiRestTemplate(final RestTemplateBuilder restTemplateBuilder) {
        log.info("* * * Creating Delius resource rest template with URL {}", deliusApiRootUri);
        return restTemplateBuilder
                .rootUri(deliusApiRootUri)
                .additionalInterceptors(new W3cTracingInterceptor())
                .setConnectTimeout(apiTimeout)
                .setReadTimeout(apiTimeout)
                .build();
    }

    @Bean(name = "deliusApiLogonEntity")
    public HttpEntity<String> deliusApiLogonEntity() {
        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        return new HttpEntity<>(deliusUsername, headers);
    }
}
