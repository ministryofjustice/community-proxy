package uk.gov.justice.digital.hmpps.community.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class TokenCheckTest {

   private RestCallHelper restCallHelper;

   @Mock private OAuth2RestTemplate oauthTemplate;
   @Mock private HttpEntity<String> deliusLogonEntity;
   @Mock private RestTemplate restTemplateResource;

   private CommunityApiTokenService tokenService;

   private String expiredToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiMWM3NDU0NS1kYzk1LTQ3MzctYjkzZi1iOWZmZTk1Mzg3OTkiLCJ1aWQiOiJib2JieS5kYXZybyIsImV4cCI6MTU1Njk1NTc2MH0.Vav0tJXzpjKaZMU7u3--ZIttvQdGsLCAfE7LY8BmzRG6Vq4fRlpa5muCIYUnCpFKCtrjPwwC8A9JsCBBOFMmiA";

   // Long-life - expires December 2030
   private String validToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiMWM3NDU0NS1kYzk1LTQ3MzctYjkzZi1iOWZmZTk1Mzg3OTkiLCJ1aWQiOiJib2JieS5kYXZybyIsImV4cCI6MTU1Njk1NTc2MH0.Vav0tJXzpjKaZMU7u3--ZIttvQdGsLCAfE7LY8BmzRG6Vq4fRlpa5muCIYUnCpFKCtrjPwwC8A9JsCBBOFMmiA";

   @Before
   public void setup() {

       initMocks(this);
       this.tokenService = new CommunityApiTokenService(deliusLogonEntity, restTemplateResource);
   }

    @Test
    public void testExpiredToken() {

       tokenService.setJwtToken(expiredToken);
       Date expiryDate = tokenService.getExpiryDate();
       assertThat(expiryDate).isNull();
    }

    public void testValidToken() {

       tokenService.setJwtToken(validToken);
       Date expiryDate = tokenService.getExpiryDate();
       assertThat(expiryDate).isInstanceOf(Date.class);
       assertThat(tokenService.isTokenExpired()).isEqualTo(false);
     }


}
