package uk.gov.justice.digital.hmpps.community.services;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.justice.digital.hmpps.community.model.Offender;

import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommunityProxyServiceTest {

    private CommunityProxyService service;
    private List<Offender> offenders;

    @Mock
    private CommunityApiClient communityApiClient;

    @Before
    public void setup() {
        service = new CommunityProxyService(communityApiClient, 0);
        offenders = List.of(
                Offender.builder().offenderNo("IT9999").firstname("Stevie").surname("Vaughn").build(),
                Offender.builder().offenderNo("IT0001").firstname("Carlos").surname("Santana").build()
        );
    }

    @Test
    public void testGetListOfOffendersForResponsibleOfficer() {

        when(communityApiClient.getOffendersForResponsibleOfficer("CXF9998")).thenReturn(offenders);
        var result = service.getOffendersForResponsibleOfficer("CXF9998");
        Assertions.assertThat(result).hasSize(2);
        Assertions.assertThat(result).containsAll(offenders);
    }
}