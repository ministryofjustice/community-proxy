package uk.gov.justice.digital.hmpps.community.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
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
        return restCallHelper.get(new UriTemplate("/offenders/nomsNumber/{nomsNumber}/convictions").expand(nomsNumber));
    }

    public String getOffenderDetails(final String nomsNumber) {
        return restCallHelper.get(new UriTemplate("/offenders/nomsNumber/{nomsNumber}").expand(nomsNumber));
    }

    public String getOffenderDocuments(final String nomsNumber) {
        return restCallHelper.get(new UriTemplate("/offenders/nomsNumber/{nomsNumber}/documents/grouped").expand(nomsNumber));
    }

    public HttpEntity<Resource> getOffenderDocument(final String nomsNumber, final String documentId) {
        return restCallHelper
                .getEntity(new UriTemplate("/offenders/nomsNumber/{nomsNumber}/documents/{documentId}")
                        .expand(nomsNumber, documentId), Resource.class);
    }

    String getRemoteStatus() {
        final var uriRemoteStatus = "/health";
        return restCallHelper.get(new UriTemplate(uriRemoteStatus).expand());
    }
}
