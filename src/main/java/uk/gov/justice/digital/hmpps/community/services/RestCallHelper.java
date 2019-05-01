package uk.gov.justice.digital.hmpps.community.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.net.URI;

@Slf4j
@Component
@Configuration
public class RestCallHelper {

    // An Oauth2RestTemplate for future use (contains calling client details, token, roles etc)
    // restTemplateOauth.getOAuth2ClientContext().getAccessToken().getAdditionalInformation();
    private final OAuth2RestTemplate restTemplateOauth;

    // The standard RestTemplate being instantiated in the constructor
    private RestTemplate restTemplate;

    // Cached JWT token from the community API
    private String jwtToken = null;

    @Value("${community.api.username}")
    private String communityUsername;

    @Value("${community.api.uri.root}")
    private String apiRootUri;

    @Autowired
    public RestCallHelper(OAuth2RestTemplate restTemplateOauth) {
        this.restTemplateOauth = restTemplateOauth;
        this.restTemplate = new RestTemplateBuilder().rootUri(apiRootUri).build();
    }

    /**
     * Perform a GET to the community API to retrieve a list of objects.
     * @param uri - the resource path (below the base path found in ${community.api.uri.root}
     * @param  responseType  - a parameterized type reference for the entity expected within the List
     * @return An list of objects of Class <T>
     */
    public <T> ResponseEntity<T> getForList(URI uri, ParameterizedTypeReference<T> responseType) {
        checkForTokenRenewal();
        final var result = restTemplate.exchange(uri.toString(), HttpMethod.GET, getResourceRequestEntity(null, jwtToken), responseType);
        return result;
    }

    /**
     * Perform a GET to the community API to retrieve a single object
     * @param uri - the resource path (below the base path found in ${community.api.uri.root}
     * @param responseType - the class type of the object  expected in the response
     * @return An object of type T
     */
    protected <T> T get(URI uri, Class<T> responseType) {
        checkForTokenRenewal();
        final var entity = getResourceRequestEntity(null, jwtToken);
        ResponseEntity<T> exchange = restTemplate.exchange(uri.toString(), HttpMethod.GET,  entity, responseType);
        return exchange.getBody();
    }

    /**
     * Perform a logon request with the Community API and retrieve a token
     * @return String token on successful logon request
     */

    protected String logonRequest() {
        final var uri = new UriTemplate("/logon");
        final var entity =  getLogonRequestEntity(null, communityUsername);
         ResponseEntity<String> exchange =  restTemplate.exchange(uri.toString(), HttpMethod.POST, entity, String.class);
         return exchange.getBody();
    }

    /**
     * Check the cached token and renew if necessary.
     * Synchronized so two client request don't renew simultaneously
     */
    private synchronized void checkForTokenRenewal() {
        if (invalidToken(jwtToken)) {
            log.info("Invalid token - logging on to renew it");
            jwtToken = logonRequest();
            log.info("New token {}", jwtToken);
        }
    }

    /**
     * Build HttpEntity with appropriate headers for a logon request to the community API
     * @param entity
     * @param username
     * @return
     */

    private HttpEntity<?> getLogonRequestEntity(Object entity, final String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, null);
        headers.setContentType(MediaType.TEXT_PLAIN);
        return new HttpEntity<>(entity, headers);
    }

    /**
     * Build a HttpEntity with appropriate headers for a resource request to the community API
     * @param entity
     * @param jwtToken
     * @return
     */
   private HttpEntity<?> getResourceRequestEntity(Object entity, String jwtToken) {
        final var headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
       return new HttpEntity<>(entity, headers);
    }

    /**
     * Check for an invalid token - either null or expired
     * @param token
     * @return
     */
    private boolean invalidToken(String token) {
        boolean result = false;
        if (token == null || tokenExpired(token)) {
            result = true;
        }
        return result;
    }

    private boolean tokenExpired(String token) {
        // TODO: Unwrap the token details
        return true;
    }
}
