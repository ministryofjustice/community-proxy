package uk.gov.justice.digital.hmpps.community.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class TokenCheckTest {

   RestCallHelper restCallHelper;

   @Mock OAuth2RestTemplate oauthTemplate;
   @Mock HttpEntity<String> deliusLogonEntity;
   @Mock RestTemplate restTemplateResource;

   // TODO: Make this a very long-life token - currently expires in May 2019
   private String testToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiMWM3NDU0NS1kYzk1LTQ3MzctYjkzZi1iOWZmZTk1Mzg3OTkiLCJ1aWQiOiJib2JieS5kYXZybyIsImV4cCI6MTU1Njk1NTc2MH0.Vav0tJXzpjKaZMU7u3--ZIttvQdGsLCAfE7LY8BmzRG6Vq4fRlpa5muCIYUnCpFKCtrjPwwC8A9JsCBBOFMmiA";

   @Before
   public void setup() {
     initMocks(this);
   }

    @Test
    public void testTokenClaims() throws Exception {

        RestCallHelper restCallHelper = new RestCallHelper(oauthTemplate, deliusLogonEntity , restTemplateResource);

        try {
            boolean expired = restCallHelper.tokenExpired(testToken);
            assertThat(expired).isEqualTo(false);
        }
        catch(Exception e) {
            assertThat(e).isNotInstanceOfAny(Exception.class);
        }
    }
}
