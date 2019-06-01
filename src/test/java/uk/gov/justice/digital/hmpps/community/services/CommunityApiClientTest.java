package uk.gov.justice.digital.hmpps.community.services;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.justice.digital.hmpps.community.model.ManagedOffender;
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
    public void testOffendersForResponsibleOfficer() throws Exception {

        final String testUri = "/staff/staffCode/CX555/managedOffenders?current=true";
        ParameterizedTypeReference<List<ManagedOffender>> OFFENDERS = new ParameterizedTypeReference<>() {};

        final var expectedBody = List.of(
                ManagedOffender.builder().nomsNumber("IT9999").build(),
                ManagedOffender.builder().nomsNumber("IT0001").build()
        );

        final var mockResponse = new ResponseEntity<>(expectedBody, HttpStatus.OK);
        doReturn(mockResponse).when(restCallHelper).getForList(new URI(testUri), OFFENDERS);
        final var listOfOffenders = communityApiClient.getOffendersForResponsibleOfficer("CX555");

        assertThat(listOfOffenders).hasSize(2);
        assertThat(listOfOffenders).containsAll(expectedBody);
        verify(restCallHelper).getForList(eq(new URI(testUri)), isA(ParameterizedTypeReference.class));
        verifyNoMoreInteractions(restCallHelper);
    }

    @Test
    public void testResponsibleOfficersForOffender() throws Exception {

        final String testUri = "/offenders/nomsNumber/IT0001/responsibleOfficers?current=true";
        ParameterizedTypeReference<List<ResponsibleOfficer>> OFFICERS = new ParameterizedTypeReference<>() {};

        final var expectedBody = List.of(
                ResponsibleOfficer.builder().staffCode("AX998").build(),
                ResponsibleOfficer.builder().staffCode("AX998").build()
        );

        final var mockResponse = new ResponseEntity<>(expectedBody, HttpStatus.OK);
        doReturn(mockResponse).when(restCallHelper).getForList(new URI(testUri), OFFICERS);
        final var responsibleOfficers = communityApiClient.getResponsibleOfficersForOffender("IT0001");
        assertThat(responsibleOfficers).hasSize(2);
        assertThat(responsibleOfficers).containsAll(expectedBody);
        verify(restCallHelper).getForList(eq(new URI(testUri)), isA(ParameterizedTypeReference.class));
        verifyNoMoreInteractions(restCallHelper);
    }
}
