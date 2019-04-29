package uk.gov.justice.digital.hmpps.community.authorisation;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.justice.digital.hmpps.community.model.ErrorResponse;
import uk.gov.justice.digital.hmpps.community.model.Offender;
import uk.gov.justice.digital.hmpps.community.model.ResponsibleOfficer;
import uk.gov.justice.digital.hmpps.community.services.CommunityApiClient;
import uk.gov.justice.digital.hmpps.community.services.CommunityProxyService;
import uk.gov.justice.digital.hmpps.community.services.RestCallHelper;

import java.net.URI;
import java.net.http.HttpResponse;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringRunner.class)
@SpringBootTest
@Configuration
public class CommunityProxyAuthTests {

    private CommunityApiClient communityApiClient;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    CommunityProxyService service;

    @Value("${test.token.expired}")
    private String expiredToken;

    @Value("${test.token.role-not-present}")
    private String roleNotPresentToken;

    @Value("${test.token.good}")
    private String goodToken;

    @Mock
    private RestCallHelper restCallHelper;

    @Before
    public void setup() {
        initMocks(restCallHelper);
        communityApiClient = new CommunityApiClient(restCallHelper);
    }

    @Test
    public void testTokenWithNoRole() throws Exception {

        final var testUrl = "/staff/staffCode/{staffCode}/managedOffenders";
        final var staffCode = "CX9998";

        try {

            final var response = restTemplate.exchange(
                    testUrl,
                    HttpMethod.GET,
                    createRequestEntityWithJwtToken(null, roleNotPresentToken),
                    List.class,
                    staffCode);

            assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
            assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }
        catch(Exception e) {
            assertThat(e).isNull();
        }
    }

     @Test
    public void testWithExpiredToken() throws Exception {

         final var testUrl = "/staff/staffCode/{staffCode}/managedOffenders";
         final var staffCode = "CX9998";

         try {

             final var response = restTemplate.exchange(
                     testUrl,
                     HttpMethod.GET,
                     createRequestEntityWithJwtToken(null, expiredToken),
                     List.class,
                     staffCode);

             assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
             assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.UNAUTHORIZED);
         }
         catch(Exception e) {
             assertThat(e).isNull();
         }
    }

    @Test
    public void testWithValidToken() throws Exception {

        final var testUrl = "/staff/staffCode/{staffCode}/managedOffenders";
        final var staffCode = "CX9998";

        // Static list of offenders for the mock
        var body = List.of(
                Offender.builder().offenderNo("IT9999").build(),
                Offender.builder().offenderNo("IT0001").build()
        );

        var mockResponse = new ResponseEntity<>(body, HttpStatus.OK);

        when(restCallHelper.getForList(eq(new URI(testUrl)), isA(ParameterizedTypeReference.class))).thenReturn(mockResponse);

        try {

            final var response = restTemplate.exchange(
                    testUrl,
                    HttpMethod.GET,
                    createRequestEntityWithJwtToken(null, goodToken),
                    List.class,
                    staffCode);

            assertThat(response.getBody()).isInstanceOf(List.class);
            assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.OK);
        }
        catch(Exception e) {
            assertThat(e).isNull();
        }
    }

    private HttpEntity<?> createRequestEntityWithJwtToken(final Object entity, String token) {

        final var headers = new HttpHeaders();
        headers.add("Authorization", "bearer " + token);

        return new HttpEntity<>(entity, headers);
    }
}
