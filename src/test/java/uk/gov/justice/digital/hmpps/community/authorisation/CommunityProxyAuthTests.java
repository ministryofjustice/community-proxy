package uk.gov.justice.digital.hmpps.community.authorisation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import uk.gov.justice.digital.hmpps.community.CommunityProxyApplication;
import uk.gov.justice.digital.hmpps.community.model.Offender;
import uk.gov.justice.digital.hmpps.community.services.CommunityApiClient;
import uk.gov.justice.digital.hmpps.community.services.CommunityProxyService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@Configuration
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CommunityProxyApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CommunityProxyAuthTests {

    // Mock out any calls to the back-end Delius API - only interested in the proxy JWT token authentication

    @MockBean
    private CommunityApiClient communityApiClient;

    // Real service object
    @Autowired
    private CommunityProxyService service;

    // Preconfigured as a named bean in RestTemplateConfiguration
    @Autowired
    @Qualifier("proxyApiOauthRestTemplate")
    RestTemplate restTemplate;

    @Value("${test.token.expired}")
    private String expiredToken;

    @Value("${test.token.role-not-present}")
    private String roleNotPresentToken;

    @Value("${test.token.good}")
    private String goodToken;

    @Before
    public void setup() {

        initMocks(this);
    }

    @Test
    public void testTokenWithNoRole() throws Exception {

        final var testUrl = "/staff/staffCode/CX9998/managedOffenders";

        try {

            final var response = restTemplate.exchange(
                    testUrl,
                    HttpMethod.GET,
                    createRequestEntityWithJwtToken(null, roleNotPresentToken),
                    new ParameterizedTypeReference<List<Offender>>(){});
        }
        catch(Exception e) {
            assertThat(e.getMessage()).contains("403 Forbidden");
        }
    }

    @Test
    public void testWithExpiredToken() throws Exception {

        final var testUrl = "/staff/staffCode/CX9998/managedOffenders";

        try {
             final var response = restTemplate.exchange(
                     testUrl,
                     HttpMethod.GET,
                     createRequestEntityWithJwtToken(null, expiredToken),
                     new ParameterizedTypeReference<List<Offender>>(){});
        }
        catch(Exception e) {
            assertThat(e.getMessage()).contains("401 Unauthorized");
        }
    }

    @Test
    public void testWithValidToken() throws Exception {

        var staffCode = "CX9998";
        var testUrl = "/staff/staffCode/" + staffCode + "/managedOffenders";

        // Mocked response in place of the Delius API connection being established
        var expectedBody = List.of(
                Offender.builder().offenderNo("IT9999").build(),
                Offender.builder().offenderNo("IT0001").build()
        );

        var mockResponse = new ResponseEntity<>(expectedBody, HttpStatus.OK);

        when(communityApiClient.getOffendersForResponsibleOfficer(staffCode)).thenReturn(expectedBody);

        try {

            final var response = restTemplate.exchange(
                    testUrl,
                    HttpMethod.GET,
                    createRequestEntityWithJwtToken(null, goodToken),
                    new ParameterizedTypeReference<List<Offender>>(){});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).containsAll(expectedBody);
            assertThat(response.getBody()).hasSize(2);
        }
        catch(Exception e) {
            assertThat(e).isNotInstanceOfAny(Exception.class);
        }
    }

    private HttpEntity<?> createRequestEntityWithJwtToken(final Object entity, String token) {

        final var headers = new HttpHeaders();
        headers.add("Authorization", "bearer " + token);
        return new HttpEntity<>(entity, headers);
    }
}