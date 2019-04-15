package uk.gov.justice.digital.hmpps.community.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;
import uk.gov.justice.digital.hmpps.community.model.Offender;

import java.lang.reflect.ParameterizedType;
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
     *  Use the RestCallHelper to send a request to the CommunityApi for the list of offenders managed by this responsible officer.
     * @param staffId String
     * @return List<Offender>
     */

    public List<Offender> getOffendersForResponsibleOfficer(String staffId) {

        log.info("Getting offenders for staff id  {}", staffId);

        final var uriOffendersForResponsibleOfficer = "/api/community/{staffId}/offenders";
        final var  uri = new UriTemplate(uriOffendersForResponsibleOfficer).expand(staffId);

        return restCallHelper.getForList(uri, OFFENDERS).getBody();
    }
}
