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
import uk.gov.justice.digital.hmpps.community.model.ResponsibleOfficer;

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
    public void dummyTest() {
        assertThat(true).isEqualTo(true);
    }

    @Test
    public void testOffendersForResponsibleOfficerCall() throws Exception {

        // Static list of offenders for the mock
        var body = List.of(
                Offender.builder().offenderNo("IT9999").build(),
                Offender.builder().offenderNo("IT0001").build()
        );

        final String testUrl = "/staff/staffCode/CX555/managedOffenders?current=true";

        var mockResponse = new ResponseEntity<>(body, HttpStatus.OK);

        when(restCallHelper.getForList(eq(new URI(testUrl)), isA(ParameterizedTypeReference.class))).thenReturn(mockResponse);

        var listOfOffenders = communityApiClient.getOffendersForResponsibleOfficer("CX555");

        assertThat(listOfOffenders).hasSize(2);
        assertThat(listOfOffenders).containsAll(body);

        verify(restCallHelper).getForList(eq(new URI(testUrl)), isA(ParameterizedTypeReference.class));
        verifyNoMoreInteractions(restCallHelper);
    }

    @Test
    public void testResponsibleOfficerForOffender() throws Exception {

        var body = ResponsibleOfficer.builder().staffCode("AX998").build();
        var response = new ResponseEntity<>(body, HttpStatus.OK);

        final String testUrl = "/offenders/nomsNumber/IT0001/responsibleOfficers?current-true&latest=true";

        when(restCallHelper.get(new URI(testUrl),  ResponsibleOfficer.class)).thenReturn(body);

        var responsibleOfficer = communityApiClient.getResponsibleOfficerForOffender("IT0001");

        assertThat(responsibleOfficer.getStaffCode().equalsIgnoreCase(body.getStaffCode()));

        verify(restCallHelper).get(new URI(testUrl), ResponsibleOfficer.class);
        verifyNoMoreInteractions(restCallHelper);
    }

}
