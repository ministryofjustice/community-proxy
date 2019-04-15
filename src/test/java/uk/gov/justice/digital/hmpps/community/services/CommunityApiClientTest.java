package uk.gov.justice.digital.hmpps.community.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.justice.digital.hmpps.community.model.Offender;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class CommunityApiClientTest {

    private CommunityApiClient communityApiClient;

    @Mock
    private RestCallHelper restCallHelper;

    @Before
    public void setup() {
        initMocks(restCallHelper);
        communityApiClient = new CommunityApiClient(restCallHelper);
    }

    @Test
    public void testOffendersForResponsibleOfficerCall() throws Exception {

        var body = List.of(
                Offender.builder().offenderNo("IT9999").firstname("Stevie").surname("Vaughn").build(),
                Offender.builder().offenderNo("IT0001").firstname("Carlos").surname("Santana").build()
        );

        final String testUrl = "/api/community/CXF9998/offenders";

        var response = new ResponseEntity<>(body, HttpStatus.OK);

        when(restCallHelper.getForList(eq(new URI(testUrl)), isA(ParameterizedTypeReference.class))).thenReturn(response);

        var listOfOffenders = communityApiClient.getOffendersForResponsibleOfficer("CXF9998");

        assertThat(listOfOffenders).hasSize(2);

        verify(restCallHelper).getForList(eq(new URI(testUrl)), isA(ParameterizedTypeReference.class));
        verifyNoMoreInteractions(restCallHelper);
    }

}
