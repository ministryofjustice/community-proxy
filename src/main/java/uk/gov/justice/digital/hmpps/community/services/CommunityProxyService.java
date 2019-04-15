package uk.gov.justice.digital.hmpps.community.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import uk.gov.justice.digital.hmpps.community.model.Offender;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.ArrayList;

@Service
@Slf4j
public class CommunityProxyService {

    private final CommunityApiClient communityApiClient;
    private int minNumAssaults;

    public CommunityProxyService(CommunityApiClient communityApiClient,
                                 @Value("${app.assaults.min:5}") int minNumAssaults) {

        this.communityApiClient = communityApiClient;
        this.minNumAssaults = minNumAssaults;
    }

    @PreAuthorize("hasRole('COMMUNITY_API')")
    public List<Offender> getOffendersForResponsibleOfficer(@NotNull final String staffId) {

        log.debug("Get offenders for responsible officer with staff ID {}", staffId);

        var offenders = communityApiClient.getOffendersForResponsibleOfficer(staffId);

        log.debug("Returned {} offenders for this officer", offenders == null ? 0 : offenders.size());

        return offenders;
    }

}
