package uk.gov.justice.digital.hmpps.community.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class CommunityApiHealth implements HealthIndicator {

    private final RestTemplate restTemplate;

    @Autowired
    public CommunityApiHealth(@Qualifier("deliusApiHealthRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Health health() {
        try {
            final ResponseEntity<String> responseEntity = this.restTemplate.getForEntity("/ping", String.class);
            return Health.up().withDetail("HttpStatus", responseEntity.getStatusCode()).build();
        } catch (RestClientException e) {
            log.error("HEALTH: Exception detail ", e);
            return Health.down(e).build();
        }
    }
}
