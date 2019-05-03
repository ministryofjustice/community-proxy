package uk.gov.justice.digital.hmpps.community.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public CommunityApiClient(RestCallHelper restCallHelper) {
        this.restCallHelper = restCallHelper;
    }

    public List<Offender> getOffendersForResponsibleOfficer(String staffCode) {
        final var uriOffenders = "/staff/staffCode/{staffCode}/managedOffenders?current=true";
        return restCallHelper.getForList(new UriTemplate(uriOffenders).expand(staffCode), OFFENDERS).getBody();
     }

    public ResponsibleOfficer getResponsibleOfficerForOffender(String nomsNumber) {
        final var uriResponsibleOfficer = "/offenders/nomsNumber/{nomsNumber}/responsibleOfficers?current-true&latest=true";
        return restCallHelper.get(new UriTemplate(uriResponsibleOfficer).expand(nomsNumber), ResponsibleOfficer.class);
    }

    public String getRemoteStatus() {
        final var uriRemoteStatus = "/status";
        return restCallHelper.get(new UriTemplate(uriRemoteStatus).expand(), String.class);
    }
}
