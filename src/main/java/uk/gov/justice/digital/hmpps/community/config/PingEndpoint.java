package uk.gov.justice.digital.hmpps.community.config;

import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Configuration
@WebEndpoint(id = "ping")
public class PingEndpoint {

    @ReadOperation
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body("pong");
    }
}
