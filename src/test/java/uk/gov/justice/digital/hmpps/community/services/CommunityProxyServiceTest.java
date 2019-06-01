package uk.gov.justice.digital.hmpps.community.services;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.justice.digital.hmpps.community.model.ManagedOffender;
import uk.gov.justice.digital.hmpps.community.model.ResponsibleOfficer;

import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommunityProxyServiceTest {

    private CommunityProxyService service;

    @Mock
    private CommunityApiClient communityApiClient;

    private List<ManagedOffender> managedOffenders;
    private List<ResponsibleOfficer> responsibleOfficers;

    @Before
    public void setup() {

        service = new CommunityProxyService(communityApiClient);

        managedOffenders = List.of(
                ManagedOffender.builder().nomsNumber("IT9999").build(),
                ManagedOffender.builder().nomsNumber("IT0001").build()
        );

        responsibleOfficers = List.of(
                ResponsibleOfficer.builder().surname("BILLNTED").staffCode("AX111").forenames("AMAZING").build(),
                ResponsibleOfficer.builder().surname("SPICE").staffCode("AX222").forenames("SCARY").build()
        );
    }

    @Test
    public void testGetListOfOffendersForResponsibleOfficer() {

        when(communityApiClient.getOffendersForResponsibleOfficer("CXF9998")).thenReturn(managedOffenders);

        final var result = service.getOffendersForResponsibleOfficer("CXF9998");

        Assertions.assertThat(result).hasSize(2);
        Assertions.assertThat(result).containsAll(managedOffenders);
    }

    @Test
    public void testGetResponsibleOfficerForOffender() {

        when(communityApiClient.getResponsibleOfficersForOffender("AX999")).thenReturn(responsibleOfficers);

        final var result = service.getResponsibleOfficersForOffender("AX999");

        Assertions.assertThat(result).hasSize(2);
        Assertions.assertThat(result).containsAll(responsibleOfficers);
    }
}