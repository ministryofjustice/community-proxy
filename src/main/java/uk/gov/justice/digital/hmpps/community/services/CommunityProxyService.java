package uk.gov.justice.digital.hmpps.community.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import uk.gov.justice.digital.hmpps.community.model.Offender;
import uk.gov.justice.digital.hmpps.community.model.ResponsibleOfficer;

import javax.validation.constraints.NotNull;
import java.util.List;

@Service
@Slf4j
public class CommunityProxyService {

    @Autowired
    private final CommunityApiClient communityApiClient;

    public CommunityProxyService(CommunityApiClient communityApiClient) {
        this.communityApiClient = communityApiClient;
    }

    @PreAuthorize("hasRole('ROLE_COMMUNITY')")
    public List<Offender> getOffendersForResponsibleOfficer(@NotNull final String staffCode) {
        var offenders = communityApiClient.getOffendersForResponsibleOfficer(staffCode);
        return offenders;
    }

    @PreAuthorize("hasRole('ROLE_COMMUNITY')")
    public ResponsibleOfficer getResponsibleOfficerForOffender(@NotNull final String nomsNumber) {
        var responsibleOfficer = communityApiClient.getResponsibleOfficerForOffender(nomsNumber);
        return responsibleOfficer;
    }

}
