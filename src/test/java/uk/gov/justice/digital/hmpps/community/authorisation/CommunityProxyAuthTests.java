package uk.gov.justice.digital.hmpps.community.authorisation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.gov.justice.digital.hmpps.community.model.ErrorResponse;
import uk.gov.justice.digital.hmpps.community.model.Offender;
import uk.gov.justice.digital.hmpps.community.services.CommunityApiClient;
import uk.gov.justice.digital.hmpps.community.services.CommunityProxyService;
import uk.gov.justice.digital.hmpps.community.services.RestCallHelper;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringRunner.class)
@SpringBootTest()
@Configuration
public class CommunityProxyAuthTests {

    private CommunityApiClient communityApiClient;

    @Autowired
    CommunityProxyService service;

    @Autowired
    RestTemplate restTemplate;

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

        final var testUrl = "/staff/staffCode/CX9998/managedOffenders";;

        try {

            final var response = restTemplate.exchange(
                    testUrl,
                    HttpMethod.GET,
                    createRequestEntityWithJwtToken(null, roleNotPresentToken),
                    List.class );

            assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }
        catch(Exception e) {
            assertThat(e.getMessage()).contains("403 Forbidden");
        }
    }

     @Test
    public void testWithExpiredToken() throws Exception {

         final var testUrl = "/staff/staffCode/CX998/managedOffenders";

         try {

             final var response = restTemplate.exchange(
                     testUrl,
                     HttpMethod.GET,
                     createRequestEntityWithJwtToken(null, expiredToken),
                     List.class);

             assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
             assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
         }
         catch(Exception e) {
             assertThat(e.getMessage()).contains("401 Unauthorized");
         }
    }

    @Test
    public void testWithValidToken() throws Exception {

        var testUrl = "/staff/staffCode/CX9998/managedOffenders";

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
                    List.class);

            assertThat(response.getBody()).isInstanceOf(List.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
        catch(Exception e) {
            assertThat(e.getClass()).isInstanceOf(ErrorResponse.class);
        }
    }

    private HttpEntity<?> createRequestEntityWithJwtToken(final Object entity, String token) {

        final var headers = new HttpHeaders();
        headers.add("Authorization", "bearer " + token);

        return new HttpEntity<>(entity, headers);
    }
}
