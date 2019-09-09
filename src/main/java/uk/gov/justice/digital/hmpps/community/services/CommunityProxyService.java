package uk.gov.justice.digital.hmpps.community.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import uk.gov.justice.digital.hmpps.community.model.ManagedOffender;
import uk.gov.justice.digital.hmpps.community.model.ResponsibleOfficer;

import javax.validation.constraints.NotNull;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class CommunityProxyService {
    private final CommunityApiClient communityApiClient;

    @PreAuthorize("hasRole('ROLE_COMMUNITY')")
    public List<ManagedOffender> getOffendersForResponsibleOfficer(@NotNull final String staffCode) {
        return communityApiClient.getOffendersForResponsibleOfficer(staffCode);
    }

    @PreAuthorize("hasRole('ROLE_COMMUNITY')")
    public List<ResponsibleOfficer> getResponsibleOfficersForOffender(@NotNull final String nomsNumber) {
        return communityApiClient.getResponsibleOfficersForOffender(nomsNumber);
    }

    @PreAuthorize("hasRole('ROLE_COMMUNITY')")
    public String getConvictionsForOffender(@NotNull final String nomsNumber) {
        return communityApiClient.getConvictionsForOffender(nomsNumber);
    }

    @PreAuthorize("hasRole('ROLE_COMMUNITY')")
    public String getOffenderDetails(@NotNull final String nomsNumber) {
        return communityApiClient.getOffenderDetails(nomsNumber);
    }

    @PreAuthorize("hasRole('ROLE_COMMUNITY')")
    public String getRemoteStatus() {
        return communityApiClient.getRemoteStatus();
    }
}
