package uk.gov.justice.digital.hmpps.community.services;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.justice.digital.hmpps.community.model.Offender;
import uk.gov.justice.digital.hmpps.community.model.ResponsibleOfficer;

import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommunityProxyServiceTest {

    private CommunityProxyService service;

    private List<Offender> offenders;
    private ResponsibleOfficer responsibleOfficer;

    @Mock
    private CommunityApiClient communityApiClient;

    @Before
    public void setup() {
        service = new CommunityProxyService(communityApiClient);
        offenders = List.of(
                Offender.builder().offenderNo("IT9999").build(),
                Offender.builder().offenderNo("IT0001").build()
        );

        responsibleOfficer = ResponsibleOfficer.builder().username("BILLNTED").staffCode("AX999").forenames("John Robert").surname("SMITH").build();
    }

    @Test
    public void testGetListOfOffendersForResponsibleOfficer() {

        when(communityApiClient.getOffendersForResponsibleOfficer("CXF9998")).thenReturn(offenders);
        var result = service.getOffendersForResponsibleOfficer("CXF9998");
        Assertions.assertThat(result).hasSize(2);
        Assertions.assertThat(result).containsAll(offenders);
    }

    @Test
    public void testGetResponsibleOfficerForOffender() {

        when(communityApiClient.getResponsibleOfficerForOffender("AX999")).thenReturn(responsibleOfficer);
        var result = service.getResponsibleOfficerForOffender("AX999");
        Assertions.assertThat(result.getStaffCode()).isEqualToIgnoringCase("AX999");
        Assertions.assertThat(result.getSurname()).isEqualToIgnoringCase("SMITH");
    }
}