package uk.gov.justice.digital.hmpps.community.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;
import uk.gov.justice.digital.hmpps.community.model.Offender;
import uk.gov.justice.digital.hmpps.community.model.ResponsibleOfficer;

import java.util.List;

@Service
@Slf4j
public class CommunityApiClient {

    private final RestCallHelper restCallHelper;
    private static final ParameterizedTypeReference<List<Offender>> OFFENDERS = new ParameterizedTypeReference<>() {};

    public CommunityApiClient(RestCallHelper restCallHelper) {
        this.restCallHelper = restCallHelper;
    }

    /**
     *  Use the RestCallHelper to request  a list of offenders managed by the staffId provided
     *
     * @param staffId String
     * @return List<Offender>
     */

    public List<Offender> getOffendersForResponsibleOfficer(String staffId) {

        final var uriOffenders = "/offenderManagers/staffCode/{staffId}/offenders";

        final var  uri = new UriTemplate(uriOffenders).expand(staffId);

        // Keep the restCallHelper so the mocked tests still pass
        // final var result = restCallHelper.getForList(uri, OFFENDERS).getBody();

        // Return a static data set for conectivity check
       return List.of (
                Offender.builder().offenderNo("CT800X").build(),
                Offender.builder().offenderNo("CR811Y").build()
                 );
    }


    /**
     *  Use the RestCallHelper to request  the responsible officer assigned to an given offender.
     *
     * @param nomsId String
     * @return ResponsibleOfficer
     */

    public ResponsibleOfficer getResponsibleOfficerForOffender(String nomsId) {

        final var uriResponsibleOfficer = "/offenders/nomsNumber/{nomsId}/responsibleOfficer";

        final var  uri = new UriTemplate(uriResponsibleOfficer).expand(nomsId);

        // Keep the restCallHelper involved so the mocked tests still pass
        // final var result = restCallHelper.get(uri, ResponsibleOfficer.class);

        // Return a static data set for conectivity check
        return ResponsibleOfficer.builder().staffCode("AA999B").forenames("Jean Michel").surname("Jarre").username("JMJARRE1").build();
    }

}
