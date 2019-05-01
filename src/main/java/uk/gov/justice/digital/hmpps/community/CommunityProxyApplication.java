package uk.gov.justice.digital.hmpps.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@SpringBootApplication
@EnableResourceServer
public class CommunityProxyApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommunityProxyApplication.class, args);
    }
}
