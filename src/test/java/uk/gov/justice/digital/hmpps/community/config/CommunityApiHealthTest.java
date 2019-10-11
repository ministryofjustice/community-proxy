package uk.gov.justice.digital.hmpps.community.config;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.MockRestServiceServer.bindTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CommunityApiHealthTest {

    private MockRestServiceServer mockCommunityAPI;

    private final TestRestTemplate testRestTemplate = new TestRestTemplate();

    @LocalServerPort
    int port;

    @Autowired
    @Qualifier("deliusApiHealthRestTemplate")
    private RestTemplate restTemplate;

    @Before
    public void setup() throws IOException {
        mockCommunityAPI = buildMockService(restTemplate);
        mockCommunityAPI
                .expect(requestTo("http://localhost:8099/api/ping"))
                .andRespond(withSuccess());
    }

    private MockRestServiceServer buildMockService(final RestTemplate restTemplate) {
        final var infoBuilder = bindTo(restTemplate);
        infoBuilder.ignoreExpectOrder(true);
        return infoBuilder.build();
    }

    @Test
    public void healthEndpointCallsCommunityAPIPing() {

        final var response = testRestTemplate.getForEntity(
                getBasePath() + "communityapi/health", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("{\"status\":\"UP\"}");
    }

    private String getBasePath() {
        return "http://localhost:" + port;
    }
}
