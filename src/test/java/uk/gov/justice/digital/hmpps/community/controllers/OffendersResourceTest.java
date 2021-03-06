package uk.gov.justice.digital.hmpps.community.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.justice.digital.hmpps.community.CommunityProxyApplication;
import uk.gov.justice.digital.hmpps.community.model.ResponsibleOfficer;
import uk.gov.justice.digital.hmpps.community.services.CommunityApiClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.core.ResolvableType.forType;

@Configuration
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CommunityProxyApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class OffendersResourceTest {

    @MockBean
    private CommunityApiClient communityApiClient;

    @Autowired
    TestRestTemplate restTemplate;

    @Value("${test.token.expired}")
    private String expiredToken;

    @Value("${test.token.role-not-present}")
    private String roleNotPresentToken;

    @Value("${test.token.good}")
    private String goodToken;

    @Before
    public void setup() {
        when(communityApiClient.getConvictionsForOffender(any())).thenReturn("[]]");
    }


    @Test
    public void getResponsibleOfficersForOffender() {

        final var nomsNumber = "CX9998";

        // Proxy endpoint (needs /api)
        final var testUrl = String.format("/api/offenders/nomsNumber/%s/responsibleOfficers", nomsNumber);

        // Mocked response in place of the Delius API connection being established
        final var expectedBody = List.of(
                ResponsibleOfficer.builder().staffCode("IT9999").build(),
                ResponsibleOfficer.builder().staffCode("IT0001").build()
        );

        when(communityApiClient.getResponsibleOfficersForOffender(nomsNumber)).thenReturn(expectedBody);

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

        assertThat(json).isEqualToJson("getResponsibleOfficersForOffender.json");
    }

    @Test
    public void convictionsForOffenderWillPassthroughResponseFromProxiedAPI() {
        final var nomsNumber = "CX9998";
        final var someAPIResponse = "[\"data\": 1 ]";

        when(communityApiClient.getConvictionsForOffender(nomsNumber)).thenReturn(someAPIResponse);

        final var response = restTemplate.exchange(
                String.format("/api/offenders/nomsNumber/%s/convictions", nomsNumber),
                HttpMethod.GET,
                createRequestEntityWithJwtToken(goodToken), String.class);


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(someAPIResponse);
    }

    @Test
    public void convictionsForOffenderIsForbiddenWhenRoleNotPresent() {
        final var response = restTemplate.exchange(
                "/api/offenders/nomsNumber/CX9998/convictions",
                HttpMethod.GET,
                createRequestEntityWithJwtToken(roleNotPresentToken), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void offenderDetailsWillPassthroughResponseFromProxiedAPI() {
        final var nomsNumber = "CX9998";
        final var someAPIResponse = "{\"data\": 1 }";

        when(communityApiClient.getOffenderDetails(nomsNumber)).thenReturn(someAPIResponse);

        final var response = restTemplate.exchange(
                String.format("/api/offenders/nomsNumber/%s", nomsNumber),
                HttpMethod.GET,
                createRequestEntityWithJwtToken(goodToken), String.class);


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(someAPIResponse);
    }

    @Test
    public void offenderDetailsIsForbiddenWhenRoleNotPresent() {
        final var response = restTemplate.exchange(
                "/api/offenders/nomsNumber/CX9998",
                HttpMethod.GET,
                createRequestEntityWithJwtToken(roleNotPresentToken), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void offenderDocumentsWillPassthroughResponseFromProxiedAPI() {
        final var nomsNumber = "CX9998";
        final var someAPIResponse = "{\"data\": 1 }";

        when(communityApiClient.getOffenderDocuments(nomsNumber)).thenReturn(someAPIResponse);

        final var response = restTemplate.exchange(
                String.format("/api/offenders/nomsNumber/%s/documents/grouped", nomsNumber),
                HttpMethod.GET,
                createRequestEntityWithJwtToken(goodToken), String.class);


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(someAPIResponse);
    }

    @Test
    public void offenderDocumentsIsForbiddenWhenRoleNotPresent() {
        final var response = restTemplate.exchange(
                "/api/offenders/nomsNumber/CX9998/documents/grouped",
                HttpMethod.GET,
                createRequestEntityWithJwtToken(roleNotPresentToken), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void offenderDocumentWillPassthroughEntityFromProxiedAPI() {
        final var nomsNumber = "CX9998";
        final var documentId = "1e593ff6-d5d6-4048-a671-cdeb8f65608b";
        final var body = new ByteArrayResource("content".getBytes());
        final var headers = new HttpHeaders();
        headers.set("Content-Type", "application/pdf");

        when(communityApiClient.getOffenderDocument(nomsNumber, documentId)).thenReturn(new HttpEntity<>(body, headers));

        final var response = restTemplate.exchange(
                String.format("/api/offenders/nomsNumber/%s/documents/%s", nomsNumber, documentId),
                HttpMethod.GET,
                createRequestEntityWithJwtToken(goodToken), Resource.class);


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(body);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PDF);
    }

    @Test
    public void offenderDocumentIsForbiddenWhenRoleNotPresent() {
        final var response = restTemplate.exchange(
                "/api/offenders/nomsNumber/CX9998/documents/1e593ff6-d5d6-4048-a671-cdeb8f65608b",
                HttpMethod.GET,
                createRequestEntityWithJwtToken(roleNotPresentToken), Resource.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private HttpEntity<?> createRequestEntityWithJwtToken(final String token) {

        final var headers = new HttpHeaders();
        headers.add("Authorization", "bearer " + token);
        return new HttpEntity<>(null, headers);
    }
}
