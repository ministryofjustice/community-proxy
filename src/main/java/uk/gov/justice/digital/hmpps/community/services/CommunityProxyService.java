package uk.gov.justice.digital.hmpps.community.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import uk.gov.justice.digital.hmpps.community.model.Offender;
import uk.gov.justice.digital.hmpps.community.model.ResponsibleOfficer;

import javax.validation.constraints.NotNull;
import java.util.List;

@Service
@Slf4j
public class CommunityProxyService {

    private final CommunityApiClient communityApiClient;

    public CommunityProxyService(CommunityApiClient communityApiClient) {
        this.communityApiClient = communityApiClient;
    }

    @PreAuthorize("hasRole('ROLE_COMMUNITY')")
    public List<Offender> getOffendersForResponsibleOfficer(@NotNull final String staffId) {

        log.debug("Get offenders for responsible officer with staff ID {}", staffId);

        var offenders = communityApiClient.getOffendersForResponsibleOfficer(staffId);

        log.debug("Returned {} offenders for this officer", offenders == null ? 0 : offenders.size());

        return offenders;
    }

    @PreAuthorize("hasRole('ROLE_COMMUNITY')")
    public ResponsibleOfficer getResponsibleOfficerForOffender(@NotNull final String nomsId) {

        log.debug("Get offenders for responsible officer with staff ID {}", nomsId);

        var responsibleOfficer = communityApiClient.getResponsibleOfficerForOffender(nomsId);

        log.debug("Returned responsible officer staffCode {}", responsibleOfficer == null ? "" : responsibleOfficer.getStaffCode());

        return responsibleOfficer;
    }

}
