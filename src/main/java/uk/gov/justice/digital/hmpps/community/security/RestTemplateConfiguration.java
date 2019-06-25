package uk.gov.justice.digital.hmpps.community.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RootUriTemplateHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import uk.gov.justice.digital.hmpps.community.utils.JwtAuthInterceptor;
import uk.gov.justice.digital.hmpps.community.utils.W3cTracingInterceptor;

import java.util.List;

@Slf4j
@Configuration
public class RestTemplateConfiguration {

    private final OAuth2ClientContext oauth2ClientContext;
    private final ClientCredentialsResourceDetails communityApiDetails;

    @Value("${proxy.endpoint.url}")
    private String proxyApiRootUri;

    @Value("${delius.endpoint.url}")
    private String deliusApiRootUri;

    @Value("${delius.api.username}")
    private String deliusUsername;

    @Autowired
    public RestTemplateConfiguration(
            final OAuth2ClientContext oauth2ClientContext,
            final ClientCredentialsResourceDetails communityApiDetails) {
        this.oauth2ClientContext = oauth2ClientContext;
        this.communityApiDetails = communityApiDetails;
    }

    @Bean(name = "proxyApiOauthRestTemplate")
    public RestTemplate restTemplate(final RestTemplateBuilder restTemplateBuilder) {
        log.info("* * * Creating Proxy resource rest template with URL {}", proxyApiRootUri);
        return restTemplateBuilder
                .rootUri(proxyApiRootUri)
                .additionalInterceptors(getRequestInterceptors())
                .build();
    }

    @Bean(name = "deliusApiHealthRestTemplate")
    public RestTemplate deliusApiHealthRestTemplate(final RestTemplateBuilder restTemplateBuilder) {
        log.info("* * * Creating Delius health rest template with URL {}", deliusApiRootUri);
        return restTemplateBuilder
                .rootUri(deliusApiRootUri)
                .additionalInterceptors(getRequestInterceptors())
                .build();
    }

    @Bean(name = "deliusApiResourceRestTemplate")
    public RestTemplate deliusApiRestTemplate(final RestTemplateBuilder restTemplateBuilder) {
        log.info("* * * Creating Delius resource rest template with URL {}", deliusApiRootUri);
        return restTemplateBuilder
                .rootUri(deliusApiRootUri)
                .build();
    }

    @Bean(name = "deliusApiLogonEntity")
    public HttpEntity<String> deliusApiLogonEntity() {
        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        return new HttpEntity<>(deliusUsername, headers);
    }

    private List<ClientHttpRequestInterceptor> getRequestInterceptors() {
        return List.of(
                new W3cTracingInterceptor(),
                new JwtAuthInterceptor());
    }

    @Bean
    public OAuth2RestTemplate communitySystemRestTemplate(final GatewayAwareAccessTokenProvider accessTokenProvider) {

        final var communitySystemRestTemplate = new OAuth2RestTemplate(communityApiDetails, oauth2ClientContext);
        final var systemInterceptors = communitySystemRestTemplate.getInterceptors();
        systemInterceptors.add(new W3cTracingInterceptor());
        communitySystemRestTemplate.setAccessTokenProvider(accessTokenProvider);
        RootUriTemplateHandler.addTo(communitySystemRestTemplate, this.deliusApiRootUri);

        return communitySystemRestTemplate;
    }

    /**
     * This subclass is necessary to make OAuth2AccessTokenSupport.getRestTemplate() public
     */
    @Component("accessTokenProvider")
    public class GatewayAwareAccessTokenProvider extends ClientCredentialsAccessTokenProvider {

        @Override
        public RestOperations getRestTemplate() {
            return super.getRestTemplate();
        }
    }
}
