package uk.gov.justice.digital.hmpps.community.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class TokenCheckTest {
    @Mock
    private HttpEntity<String> deliusLogonEntity;
    @Mock
    private RestTemplate restTemplateResource;

    private CommunityApiTokenService tokenService;

    private static final String EXPIRED_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiMWM3NDU0NS1kYzk1LTQ3MzctYjkzZi1iOWZmZTk1Mzg3OTkiLCJ1aWQiOiJib2JieS5kYXZybyIsImV4cCI6MTU1Njk1NTc2MH0.Vav0tJXzpjKaZMU7u3--ZIttvQdGsLCAfE7LY8BmzRG6Vq4fRlpa5muCIYUnCpFKCtrjPwwC8A9JsCBBOFMmiA";

    // Long-life - expires in November 2030
    private static final String VALID_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJyZWFkIl0sImF1dGhfc291cmNlIjoibm9uZSIsImV4cCI6MTkyMDc2NjQ1NSwiYXV0aG9yaXRpZXMiOlsiUk9MRV9TWVNURU1fVVNFUiIsIlJPTEVfR0xPQkFMX1NFQVJDSCIsIlJPTEVfQ09NTVVOSVRZIiwiUk9MRV9MSUNFTkNFX1JPIl0sImp0aSI6ImMzNjkyOGJkLWEwZDgtNDM2Yy1hMjc3LWQwMTc2MDY3MGY2OSIsImNsaWVudF9pZCI6ImxpY2VuY2VzYWRtaW4ifQ.dZOaS4nK3A8RdRs0cI_Jno3KAPuttRaBWlceu_3_i9C2gLpoBhlCndZLFONfHj0FWqGv6O1iboW3DAkGupIJLQmOKs-rRqw_xIifkUOBwpkBkWfJv9AW6JT6hVR3U7UZtblCgaj1q5Z2Xt983h5ITBUH0vlIGVJk7G2vUw0qE992fgOhqqSIpIMJuoDyL2BqBCPUgIXnu-M59UCG3T0dawOROnQn9evlB32uZlifVj1gkn9SSDSTqz_EjNFtl3igj7AOTL5rBIrryG59z2O4xIbbStZZLpA2hCl8-15tX9iX7ZOakPGMtKKwulfwOLslWiFS1XkDQWTMyTDbZUK8MA";

    @Before
    public void setup() {
        this.tokenService = new CommunityApiTokenService(deliusLogonEntity, restTemplateResource);
    }

    @Test
    public void testNullToken() {
        when(restTemplateResource.exchange(anyString(), any(), any(), any(Class.class))).thenReturn(ResponseEntity.ok("some response"));

        ReflectionTestUtils.setField(tokenService, "jwtToken", null);
        tokenService.checkOrRenew();

        // should make a call to logon since token expired
        verify(restTemplateResource).exchange("/logon", HttpMethod.POST, deliusLogonEntity, String.class);

        // token should now be used in request header entity
        assertThat(tokenService.getTokenEnabledRequestEntity().getHeaders().getFirst(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer some response");
    }

    @Test
    public void testExpiredToken() {
        when(restTemplateResource.exchange(anyString(), any(), any(), any(Class.class))).thenReturn(ResponseEntity.ok("some response"));

        ReflectionTestUtils.setField(tokenService, "jwtToken", EXPIRED_TOKEN);
        tokenService.checkOrRenew();

        // should make a call to logon since token expired
        verify(restTemplateResource).exchange("/logon", HttpMethod.POST, deliusLogonEntity, String.class);

        // token should now be used in request header entity
        assertThat(tokenService.getTokenEnabledRequestEntity().getHeaders().getFirst(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer some response");
    }

    @Test
    public void testValidToken() {
        ReflectionTestUtils.setField(tokenService, "jwtToken", VALID_TOKEN);
        tokenService.checkOrRenew();

        // no call expected
        verify(restTemplateResource, never()).exchange(anyString(), any(), any(), any(Class.class));
    }

    @Test
    public void testDeliusFailureThrowsException() {
        when(restTemplateResource.exchange(anyString(), any(), any(), any(Class.class))).thenThrow(new RuntimeException("Call to delius failed"));

        assertThatThrownBy(() -> tokenService.checkOrRenew()).hasMessageContaining("Call to delius failed");
    }
}
