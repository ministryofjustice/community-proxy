package uk.gov.justice.digital.hmpps.community.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.impl.DefaultJwtParser;
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

    /*
     * An Oauth2RestTemplate for future use (contains calling client context including token, roles etc)
     * For example, to get details of the calling user/client where logon to the Community API is a specific user.
     * restTemplateOauth.getOAuth2ClientContext().getAccessToken().getAdditionalInformation();
     */
    private final OAuth2RestTemplate restTemplateOauth;

    // Pre-configured with URL for accessing Delius API resources
    private RestTemplate restTemplateResource;

    private CommunityApiTokenService tokenService;


    @Autowired
    public RestCallHelper(OAuth2RestTemplate restTemplateOauth,
                          @Qualifier("deliusApiResourceRestTemplate") RestTemplate restTemplateResource,
                          CommunityApiTokenService tokenService) {

        // For future use - when Delius API is altered to Oauth2
        this.restTemplateOauth = restTemplateOauth;

        // For current use
        this.restTemplateResource = restTemplateResource;
        this.tokenService = tokenService;
    }

    /**
     * Perform a GET to the community API to retrieve a list of objects.
     * @param uri - the resource path (below the base path found in ${community.api.uri.root}
     * @param  responseType  - a parameterized type reference for the entity expected within the List
     * @return An list of objects of Class <T>
     */
    public <T> ResponseEntity<T> getForList(URI uri, ParameterizedTypeReference<T> responseType) {

        tokenService.checkOrRenew();
        return restTemplateResource.exchange(uri.toString(), HttpMethod.GET, tokenService.getTokenEnabledRequestEntity(null), responseType);
    }

    /**
     * Perform a GET to the community API to retrieve a single object
     * @param uri - the resource path (below the base path found in ${community.api.uri.root}
     * @param responseType - the class type of the object  expected in the response
     * @return An object of type T
     */
    protected <T> T get(URI uri, Class<T> responseType) {
        tokenService.checkOrRenew();
        ResponseEntity<T> exchange = restTemplateResource.exchange(uri.toString(), HttpMethod.GET, tokenService.getTokenEnabledRequestEntity(null), responseType);
        return exchange.getBody();
    }
}