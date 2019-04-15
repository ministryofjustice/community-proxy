package uk.gov.justice.digital.hmpps.community.controllers;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import uk.gov.justice.digital.hmpps.community.model.Offender;
import uk.gov.justice.digital.hmpps.community.model.ErrorResponse;
import uk.gov.justice.digital.hmpps.community.services.CommunityProxyService;
import java.util.List;

import javax.validation.constraints.NotNull;

@Api(tags = {"community-proxy"},
        authorizations = {@Authorization("COMMUNITY_API")},
        description = "Provides proxying and authentication for the Community API and Delius data")
@RestController
@RequestMapping(
        value = "communityapi",
        produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class CommunityProxyResource {

    private final CommunityProxyService communityProxyService;

    public CommunityProxyResource(CommunityProxyService communityProxyService) {
        this.communityProxyService = communityProxyService;
    }

    @ApiOperation(
            value = "Return list of of offenders for one responsible officer (RO)",
            notes = "Accepts a Delius staff id for the responsible officer",
            authorizations = {@Authorization("COMMUNITY_API")},
            nickname = "getOffendersForResponsibleOfficer")
    @ApiResponses(
            value = {
            @ApiResponse(code = 200, message = "OK", response = List.class),
            @ApiResponse(code = 400, message = "Invalid request", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Unrecoverable error whilst processing request.", response = ErrorResponse.class)
            })
    @GetMapping(path = "/offendersByResponsibleOfficer/{staffId}")
    public List<Offender> getOffendersForResponsibleOfficer(@ApiParam(name = "staffId", value = "Staff ID of the responsible officer", example = "CWD9898", required = true)
                                                                                                                 @NotNull @PathVariable(value = "staffId") String staffId) {
        return communityProxyService.getOffendersForResponsibleOfficer(staffId);
    }
}

