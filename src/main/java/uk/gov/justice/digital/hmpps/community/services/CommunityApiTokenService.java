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

@Slf4j
@Service
public class CommunityApiTokenService {

    // Cached token - renewed when required
    private String jwtToken = null;

    private final RestTemplate restTemplateResource;
    private final HttpEntity<String> deliusLogonEntity;
    private final DefaultJwtParser parser;

    public CommunityApiTokenService(@Qualifier("deliusApiLogonEntity") final HttpEntity<String> deliusLogonEntity,
                                    @Qualifier("deliusApiResourceRestTemplate") final RestTemplate restTemplateResource) {

        this.restTemplateResource = restTemplateResource;
        this.deliusLogonEntity = deliusLogonEntity;
        this.parser = new DefaultJwtParser();
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(final String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public void checkOrRenew() {

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

    public boolean isTokenExpired() {

        var result = true;

        final var expiry = getExpiryDate();
        if (expiry != null) {
            log.info("* * * Token expiry time is : {} ", expiry);
            final var now = new Date();
            if (expiry != null && expiry.after(now)) {
                result = false;
            }
        }
        return result;
    }

    public Date getExpiryDate() {

        try {
            // Convert to an unsigned token to extract the claims without the signing key
            final var splitToken = jwtToken.split("\\.");
            final var unsignedToken = splitToken[0] + "." + splitToken[1] + ".";
            final Jwt<?, ?> jwt = parser.parse(unsignedToken);
            final var claims = (Claims) jwt.getBody();
            return claims.getExpiration();
        } catch (final ExpiredJwtException exp) {
            log.info("Token expired {} - msg {}", jwtToken, exp.getMessage());
        } catch (final Exception e) {
            log.info("Exception during token check {} - msg {}", jwtToken, e.getMessage());
        }

        return null;
    }

    // Creates the request entity and includes the cached JWT token in the Authorization header
    public HttpEntity<?> getTokenEnabledRequestEntity(final Object entity) {

        final var headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(entity, headers);
    }
}
