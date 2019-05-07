package uk.gov.justice.digital.hmpps.community.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.impl.DefaultJwtParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Slf4j
@Service
public class CommunityApiTokenService {

    // Cached token - renewed when required
    private String jwtToken = null;

    private RestTemplate restTemplateResource;
    private HttpEntity<String> deliusLogonEntity;
    private DefaultJwtParser parser;

    public CommunityApiTokenService(@Qualifier("deliusApiLogonEntity") HttpEntity<String> deliusLogonEntity,
                                    @Qualifier("deliusApiResourceRestTemplate") RestTemplate restTemplateResource) {

        this.restTemplateResource = restTemplateResource;
        this.deliusLogonEntity = deliusLogonEntity;
        this.parser = new DefaultJwtParser();
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public void checkOrRenew() {

        if (jwtToken == null || isTokenExpired()) {
            renewCachedToken();
        }
    }

    private synchronized void renewCachedToken() {

        try {
            ResponseEntity<String> exchange = restTemplateResource.exchange("/logon", HttpMethod.POST, deliusLogonEntity, String.class);
            jwtToken = exchange.getBody();
            log.info("* * * Renewed token is {}", jwtToken);
        }
        catch(Exception e) {
            log.error("* * * Exception renewing Delius API token {} ", e.getMessage());
        }
    }

    public boolean isTokenExpired() {

        boolean result = true;

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
            String[] splitToken = jwtToken.split("\\.");
            String unsignedToken = splitToken[0] + "." + splitToken[1] + ".";
            Jwt<?,?> jwt = parser.parse(unsignedToken);
            Claims claims = (Claims) jwt.getBody();
            return claims.getExpiration();
        }
        catch(ExpiredJwtException exp) {
            log.info("Token expired {} - msg {}", jwtToken, exp.getMessage());
        }
        catch(Exception e) {
            log.info("Exception during token check {} - msg {}", jwtToken, e.getMessage());
        }

        return null;
    }

    // Creates the request entity and includes the cached JWT token in the Authorization header
    public HttpEntity<?> getTokenEnabledRequestEntity(Object entity) {

        final var headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(entity, headers);
    }
}
