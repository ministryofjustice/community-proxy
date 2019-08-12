package uk.gov.justice.digital.hmpps.community.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import uk.gov.justice.digital.hmpps.community.CommunityProxyApplication;
import uk.gov.justice.digital.hmpps.community.model.ManagedOffender;
import uk.gov.justice.digital.hmpps.community.services.CommunityApiClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.springframework.core.ResolvableType.forType;

@Configuration
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CommunityProxyApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CommunityProxyResourceTest {

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
    public void getOffendersForResponsibleOfficer() {

        final var staffCode = "CX9998";

        // Proxy endpoint (needs /api)
        final var testUrl = String.format("/api/staff/staffCode/%s/managedOffenders", staffCode);

        // Mocked response in place of the Delius API connection being established
        final var expectedBody = List.of(
                ManagedOffender.builder().nomsNumber("IT9999").build(),
                ManagedOffender.builder().nomsNumber("IT0001").build()
        );

        when(communityApiClient.getOffendersForResponsibleOfficer(staffCode)).thenReturn(expectedBody);

        final var response = restTemplate.exchange(
                testUrl,
                HttpMethod.GET,
                createRequestEntityWithJwtToken(goodToken), String.class);

        if (response.getStatusCodeValue() != 200) {
            fail("Detail call failed. Response body : " + response.getBody());
            return;
        }

        // noinspection ConstantConditions
        final var json = new JsonContent<String>(getClass(), forType(String.class), response.getBody());

        assertThat(json).isEqualToJson("getOffendersForResponsibleOfficer.json");
    }

    private HttpEntity<?> createRequestEntityWithJwtToken(final String token) {

        final var headers = new HttpHeaders();
        headers.add("Authorization", "bearer " + token);
        return new HttpEntity<>(null, headers);
    }
}
