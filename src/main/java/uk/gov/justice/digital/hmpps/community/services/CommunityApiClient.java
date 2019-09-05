package uk.gov.justice.digital.hmpps.community.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;
import uk.gov.justice.digital.hmpps.community.model.ManagedOffender;
import uk.gov.justice.digital.hmpps.community.model.ResponsibleOfficer;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class CommunityApiClient {

    private final RestCallHelper restCallHelper;
    private static final ParameterizedTypeReference<List<ManagedOffender>> OFFENDERS = new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<List<ResponsibleOfficer>> OFFICERS = new ParameterizedTypeReference<>() {};

    public List<ManagedOffender> getOffendersForResponsibleOfficer(final String staffCode) {
        final var uriManagedOffenders = "/staff/staffCode/{staffCode}/managedOffenders?current=true";
        return restCallHelper.getForList(new UriTemplate(uriManagedOffenders).expand(staffCode), OFFENDERS).getBody();
     }

    public List<ResponsibleOfficer> getResponsibleOfficersForOffender(final String nomsNumber) {
        final var uriResponsibleOfficers = "/offenders/nomsNumber/{nomsNumber}/responsibleOfficers?current=true";
        return restCallHelper.getForList(new UriTemplate(uriResponsibleOfficers).expand(nomsNumber), OFFICERS).getBody();
    }

    public String getConvictionsForOffender(final String nomsNumber) {
        final var uriResponsibleOfficers = "/offenders/nomsNumber/{nomsNumber}/convictions";
        return restCallHelper.get(new UriTemplate(uriResponsibleOfficers).expand(nomsNumber));
    }

    String getRemoteStatus() {
        final var uriRemoteStatus = "/health";
        return restCallHelper.get(new UriTemplate(uriRemoteStatus).expand());
    }
}
