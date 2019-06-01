package uk.gov.justice.digital.hmpps.community.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import uk.gov.justice.digital.hmpps.community.model.ManagedOffender;
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
    public List<ManagedOffender> getOffendersForResponsibleOfficer(@NotNull final String staffCode) {
        return communityApiClient.getOffendersForResponsibleOfficer(staffCode);
    }

    @PreAuthorize("hasRole('ROLE_COMMUNITY')")
    public List<ResponsibleOfficer> getResponsibleOfficersForOffender(@NotNull final String nomsNumber) {
        return communityApiClient.getResponsibleOfficersForOffender(nomsNumber);
    }

    @PreAuthorize("hasRole('ROLE_COMMUNITY')")
    public String getRemoteStatus() {
        return communityApiClient.getRemoteStatus();
    }
}