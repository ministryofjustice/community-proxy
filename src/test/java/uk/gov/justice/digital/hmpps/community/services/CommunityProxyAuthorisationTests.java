package uk.gov.justice.digital.hmpps.community.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import uk.gov.justice.digital.hmpps.community.CommunityProxyApplication;
import uk.gov.justice.digital.hmpps.community.model.ManagedOffender;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@Configuration
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CommunityProxyApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CommunityProxyAuthorisationTests {

    @MockBean
    private CommunityApiClient communityApiClient;

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

    @Test
    public void testTokenWithNoRole() {

        // Proxy endpoint (needs /api)
        final var testUrl = "/api/staff/staffCode/CX9998/managedOffenders";

        assertThatThrownBy(() -> restTemplate.exchange(
                testUrl,
                HttpMethod.GET,
                createRequestEntityWithJwtToken(roleNotPresentToken),
                new ParameterizedTypeReference<List<ManagedOffender>>() {
                })
        ).hasMessageContaining("403");
    }

    @Test
    public void testWithExpiredToken() {

        // Proxy endpoint (needs /api)
        final var testUrl = "/api/staff/staffCode/CX9998/managedOffenders";

        assertThatThrownBy(() -> restTemplate.exchange(
                testUrl,
                HttpMethod.GET,
                createRequestEntityWithJwtToken(expiredToken),
                new ParameterizedTypeReference<List<ManagedOffender>>() {
                })
        ).hasMessageContaining("401");
    }

    @Test
    public void testWithValidToken() {

        final var staffCode = "CX9998";

        // Proxy endpoint (needs /api)
        final var testUrl = "/api/staff/staffCode/" + staffCode + "/managedOffenders";

        // Mocked response in place of the Delius API connection being established
        final var expectedBody = List.of(
                ManagedOffender.builder().nomsNumber("IT9999").build(),
                ManagedOffender.builder().nomsNumber("IT0001").build()
        );

        when(communityApiClient.getOffendersForResponsibleOfficer(staffCode)).thenReturn(expectedBody);

        final var response = restTemplate.exchange(
                testUrl,
                HttpMethod.GET,
                createRequestEntityWithJwtToken(goodToken),
                new ParameterizedTypeReference<List<ManagedOffender>>() {
                });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsAll(expectedBody);
        assertThat(response.getBody()).hasSize(2);
    }

    private HttpEntity<?> createRequestEntityWithJwtToken(final String token) {

        final var headers = new HttpHeaders();
        headers.add("Authorization", "bearer " + token);
        return new HttpEntity<>(null, headers);
    }
}
