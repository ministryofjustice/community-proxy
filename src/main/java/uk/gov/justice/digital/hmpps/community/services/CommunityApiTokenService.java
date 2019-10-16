package uk.gov.justice.digital.hmpps.community.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.impl.DefaultJwtParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
public class CommunityApiTokenService {

    // Cached token - renewed when required
    private String jwtToken = null;

    private final RestTemplate restTemplateResource;
    private final HttpEntity<String> deliusLogonEntity;
    private final DefaultJwtParser parser = new DefaultJwtParser();

    public CommunityApiTokenService(@Qualifier("deliusApiLogonEntity") final HttpEntity<String> deliusLogonEntity,
                                    @Qualifier("deliusApiResourceRestTemplate") final RestTemplate restTemplateResource) {

        this.restTemplateResource = restTemplateResource;
        this.deliusLogonEntity = deliusLogonEntity;
    }

    void checkOrRenew() {

        if (jwtToken == null || isTokenExpired()) {
            renewCachedToken();
        }
    }

    private synchronized void renewCachedToken() {

        try {
            final var exchange = restTemplateResource.exchange("/logon", HttpMethod.POST, deliusLogonEntity, String.class);
            jwtToken = exchange.getBody();
            log.info("* * * Renewed token is {}", jwtToken);
        } catch (final Exception e) {
            log.error("* * * Exception renewing Delius API token {} ", e.getMessage());
        }
    }

    private boolean isTokenExpired() {
        final var expiry = getExpiryDate();
        return expiry.map(d -> {
            log.info("* * * Token expiry time is : {} ", d);
            final var now = new Date();
            return !d.after(now);
        }).orElse(Boolean.TRUE);
    }

    private Optional<Date> getExpiryDate() {
        try {
            // Convert to an unsigned token to extract the claims without the signing key
            final var splitToken = jwtToken.split("\\.");
            final var unsignedToken = splitToken[0] + "." + splitToken[1] + ".";
            final Jwt<?, ?> jwt = parser.parse(unsignedToken);
            final var claims = (Claims) jwt.getBody();
            return Optional.of(claims.getExpiration());
        } catch (final ExpiredJwtException exp) {
            log.info("Token expired {} - msg {}", jwtToken, exp.getMessage());
        } catch (final Exception e) {
            log.warn("Exception during token check {} - msg {}", jwtToken, e);
        }

        return Optional.empty();
    }

    // Creates the request entity and includes the cached JWT token in the Authorization header
    HttpEntity<?> getTokenEnabledRequestEntity() {
        final var headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(null, headers);
    }
}
