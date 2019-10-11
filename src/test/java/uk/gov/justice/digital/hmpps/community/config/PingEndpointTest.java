package uk.gov.justice.digital.hmpps.community.config;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class PingEndpointTest {
    @Test
    public void ping() {

        final var headers = new HttpHeaders();
        headers.add("Content-Type", "text/plain");

        assertThat(new PingEndpoint().ping()).isEqualTo(new ResponseEntity<>("pong", headers, HttpStatus.OK));
    }
}
