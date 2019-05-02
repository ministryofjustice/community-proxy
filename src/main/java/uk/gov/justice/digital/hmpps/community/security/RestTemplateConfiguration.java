package uk.gov.justice.digital.hmpps.community.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RootUriTemplateHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import uk.gov.justice.digital.hmpps.community.utils.JwtAuthInterceptor;
import uk.gov.justice.digital.hmpps.community.utils.UserContextInterceptor;

import java.util.List;

@Slf4j
@Configuration
public class RestTemplateConfiguration {

    private final OAuth2ClientContext oauth2ClientContext;
    private final ClientCredentialsResourceDetails communityApiDetails;

    @Value("${proxy.endpoint.url}/communityapi/api")
    private String proxyApiRootUri;

    @Value("${delius.endpoint.url}/api")
    private String deliusApiRootUri;

    @Value("${delius.api.username}")
    private String deliusUsername;

    @Value("${delius.api.password}")
    private String deliusPassword;

    @Autowired
    public RestTemplateConfiguration(
            OAuth2ClientContext oauth2ClientContext,
            ClientCredentialsResourceDetails communityApiDetails) {
        this.oauth2ClientContext = oauth2ClientContext;
        this.communityApiDetails = communityApiDetails;
    }

    @Bean(name = "proxyApiOauthRestTemplate")
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        log.info("* * * Creating Proxy resource rest template with URL {}", proxyApiRootUri);
        return restTemplateBuilder
                .rootUri(proxyApiRootUri)
                .additionalInterceptors(getRequestInterceptors())
                .build();
    }

    @Bean(name = "deliusApiHealthRestTemplate")
    public RestTemplate deliusApiHealthRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        log.info("* * * Creating Delius health rest template with URL {}", deliusApiRootUri);
        return restTemplateBuilder
                .rootUri(deliusApiRootUri)
                .additionalInterceptors(getRequestInterceptors())
                .build();
    }

    @Bean(name = "deliusApiResourceRestTemplate")
    public RestTemplate deliusApiRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        log.info("* * * Creating Delius resource rest template with URL {}", deliusApiRootUri);
        return restTemplateBuilder
                .rootUri(deliusApiRootUri)
                .build();
    }

    @Bean(name = "deliusApiLogonRestTemplate")
    public RestTemplate deliusApiLogonRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        log.info("* * * Creating Delius logon rest template with URL {}", deliusApiRootUri);
        return restTemplateBuilder
                .rootUri(deliusApiRootUri)
                .basicAuthentication(deliusUsername, deliusPassword)
                .build();
    }

    private List<ClientHttpRequestInterceptor> getRequestInterceptors() {
        return List.of(
                new UserContextInterceptor(),
                new JwtAuthInterceptor());
    }

    @Bean
    public OAuth2RestTemplate communitySystemRestTemplate(GatewayAwareAccessTokenProvider accessTokenProvider) {

        OAuth2RestTemplate communitySystemRestTemplate = new OAuth2RestTemplate(communityApiDetails, oauth2ClientContext);
        List<ClientHttpRequestInterceptor> systemInterceptors = communitySystemRestTemplate.getInterceptors();
        systemInterceptors.add(new UserContextInterceptor());
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
