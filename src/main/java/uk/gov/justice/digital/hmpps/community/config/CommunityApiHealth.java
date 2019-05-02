package uk.gov.justice.digital.hmpps.community.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.HttpStatus;
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
            final ResponseEntity<String> responseEntity = this.restTemplate.getForEntity("/health", String.class);
            return health(Health.up(), responseEntity.getStatusCode());
        } catch (RestClientException e) {
            log.error("HEALTH: Exception detail " + e.getMessage());
            return health(Health.outOfService(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    private Health health(Health.Builder builder, HttpStatus code) {
        return builder
                .withDetail("HttpStatus", code.value())
                .build();
    }
}
