package uk.gov.justice.digital.hmpps.community.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * Helper class that sets up the rest template with a base URL and required request headers.
 */
@Component
public class RestCallHelper {

    private static final HttpHeaders CONTENT_TYPE_APPLICATION_JSON = httpContentTypeHeaders();

    private final OAuth2RestTemplate restTemplate;

    @Autowired
    public RestCallHelper(OAuth2RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> ResponseEntity<T> getForList(URI uri, ParameterizedTypeReference<T> responseType) {
        return restTemplate.exchange(uri.toString(), HttpMethod.GET, null, responseType);
    }

    protected <T> T get(URI uri, Class<T> responseType) {
        ResponseEntity<T> exchange = restTemplate.exchange(uri.toString(), HttpMethod.GET,  new HttpEntity<>(null, CONTENT_TYPE_APPLICATION_JSON), responseType);
        return exchange.getBody();
    }

    private static HttpHeaders httpContentTypeHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }
}
