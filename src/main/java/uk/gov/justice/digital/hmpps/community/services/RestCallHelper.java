package uk.gov.justice.digital.hmpps.community.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Slf4j
@Component
public class RestCallHelper {
    // Pre-configured with URL for accessing Delius API resources
    private final RestTemplate restTemplateResource;

    private final CommunityApiTokenService tokenService;


    @Autowired
    public RestCallHelper(@Qualifier("deliusApiResourceRestTemplate") final RestTemplate restTemplateResource,
                          final CommunityApiTokenService tokenService) {

        this.restTemplateResource = restTemplateResource;
        this.tokenService = tokenService;
    }

    /**
     * Perform a GET to the community API to retrieve a list of objects.
     *
     * @param uri          - the resource path (below the base path found in ${community.api.uri.root}
     * @param responseType - a parameterized type reference for the entity expected within the List
     * @return An list of objects of Class <T>
     */
    public <T> ResponseEntity<T> getForList(final URI uri, final ParameterizedTypeReference<T> responseType) {

        tokenService.checkOrRenew();
        return restTemplateResource.exchange(uri.toString(), HttpMethod.GET, tokenService.getTokenEnabledRequestEntity(null), responseType);
    }

    /**
     * Perform a GET to the community API to retrieve a single object
     *
     * @param uri - the resource path (below the base path found in ${community.api.uri.root}
     * @return An object of type String
     */
    String get(final URI uri) {
        return get(uri, String.class);
    }

    /**
     * Perform a GET to the community API to retrieve a resource object
     *
     * @param uri - the resource path (below the base path found in ${community.api.uri.root}
     * @return A resource of type responseType
     */
    <T> T get(final URI uri, final Class<T> responseType) {
        tokenService.checkOrRenew();
        final var exchange = restTemplateResource.exchange(
                uri.toString(),
                HttpMethod.GET,
                tokenService.getTokenEnabledRequestEntity(null), responseType);
        return exchange.getBody();
    }

    <T> HttpEntity<T> getEntity(final URI uri, final Class<T> responseType) {
        tokenService.checkOrRenew();
        return restTemplateResource.exchange(
                uri.toString(),
                HttpMethod.GET,
                tokenService.getTokenEnabledRequestEntity(null), responseType);
    }
}
