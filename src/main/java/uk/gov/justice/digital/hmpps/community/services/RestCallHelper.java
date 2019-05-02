package uk.gov.justice.digital.hmpps.community.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Date;

@Slf4j
@Component
public class RestCallHelper {

    // An Oauth2RestTemplate for future use (contains calling client details, token, roles etc)
    // restTemplateOauth.getOAuth2ClientContext().getAccessToken().getAdditionalInformation();
    private final OAuth2RestTemplate restTemplateOauth;

    // Pre-configured with basic authentication headers for logon & token retrieval
    private RestTemplate restTemplateLogon;

    // Pre-configured with JWT bearer token headers for accessing Delius API resources
    private RestTemplate restTemplateResource;

    // Cached token - renewed when required
    private String jwtToken = null;

    @Autowired
    public RestCallHelper(OAuth2RestTemplate restTemplateOauth,
                          @Qualifier("deliusApiLogonRestTemplate") RestTemplate restTemplateLogon,
                          @Qualifier("deliusApiResourceRestTemplate") RestTemplate restTemplateResource) {

        // For future use - when Delius API is altered to Oauth2
        this.restTemplateOauth = restTemplateOauth;

        // For current use
        this.restTemplateLogon = restTemplateLogon;
        this.restTemplateResource = restTemplateResource;
    }

    /**
     * Perform a GET to the community API to retrieve a list of objects.
     * @param uri - the resource path (below the base path found in ${community.api.uri.root}
     * @param  responseType  - a parameterized type reference for the entity expected within the List
     * @return An list of objects of Class <T>
     */
    public <T> ResponseEntity<T> getForList(URI uri, ParameterizedTypeReference<T> responseType) {
        checkForCachedTokenRenewal();
        final var entity = getResourceRequestEntity(null, jwtToken);
        return restTemplateResource.exchange(uri.toString(), HttpMethod.GET, entity, responseType);
    }

    /**
     * Perform a GET to the community API to retrieve a single object
     * @param uri - the resource path (below the base path found in ${community.api.uri.root}
     * @param responseType - the class type of the object  expected in the response
     * @return An object of type T
     */
    protected <T> T get(URI uri, Class<T> responseType) {
        checkForCachedTokenRenewal();
        final var entity = getResourceRequestEntity(null, jwtToken);
        ResponseEntity<T> exchange = restTemplateResource.exchange(uri.toString(), HttpMethod.GET,  entity, responseType);
        return exchange.getBody();
    }

    /**
     * Check the cached token and renew if necessary.
     * Synchronized so two client request don't renew simultaneously
     */
    private synchronized void checkForCachedTokenRenewal() {
        if (invalidToken(jwtToken)) {
            log.info("* * * Invalid token - renewing ... ");
            jwtToken = renewCachedToken();
            log.info("* * * New token obtained {}", jwtToken);
        }
    }

    /**
     * Perform a logon request with the Community API and retrieve a token using pre-configured restTemplate
     * @return String token
     */
    private String renewCachedToken() {
        // final var entity = getLogonRequestEntity(null, deliusUsername, deliusPassword);
        ResponseEntity<String> exchange =  restTemplateLogon.exchange("/logon", HttpMethod.POST, null, String.class);
        return exchange.getBody();
    }

    /**
     * Build HttpEntity with appropriate headers for a logon request to the community API
     * @param entity The object being wrapped in a HttpEntity
     * @param username The delius API username
     * @param password The delius API password
     * @return HttpEntity<T> requestEntity
     */
    private HttpEntity<?> getLogonRequestEntity(Object entity, final String username, final String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        headers.setContentType(MediaType.TEXT_PLAIN);
        return new HttpEntity<>(entity, headers);
    }

    /**
     * Build a HttpEntity with appropriate headers for a resource request to the community API
     * @param entity The object being wrapped in a HttpEntity
     * @param jwtToken The JWT token received from a logon request
     * @return HttpEntity<T> requestEntity
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
     * @return boolean true if token is null or invalid
     */
    private boolean invalidToken(String token) {
        boolean result = false;
        if (token == null || tokenExpired(token)) {
            log.info("* * * Null or expired token - will renew it..");
            result = true;
        }
        return result;
    }

    private boolean tokenExpired(String token) throws SignatureException {
        boolean result = true;

        Claims claims = Jwts.parser().parseClaimsJws(token).getBody();
        Date expiry = claims.getExpiration();
        log.info("* * * Token expiry time is : {} ", expiry.toString());
        Date now = new Date();
        if (expiry.after(now)) {
            result = false;
        }
        return result;
    }
}