package uk.gov.justice.digital.hmpps.community.controllers;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.digital.hmpps.community.model.ErrorResponse;
import uk.gov.justice.digital.hmpps.community.model.Offender;
import uk.gov.justice.digital.hmpps.community.model.ResponsibleOfficer;
import uk.gov.justice.digital.hmpps.community.services.CommunityProxyService;

import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Api(tags = {"community-proxy"}, authorizations = {@Authorization("ROLE_COMMUNITY")})
@RestController
@RequestMapping(value = "api", produces = MediaType.APPLICATION_JSON_VALUE)
public class CommunityProxyResource {

    private final CommunityProxyService communityProxyService;

    public CommunityProxyResource(CommunityProxyService communityProxyService) {
        this.communityProxyService = communityProxyService;
    }

    @ApiOperation(
            value = "Return list of of currently managed offenders for one responsible officer (RO)",
            notes = "Accepts a Delius staff code for the responsible officer",
            authorizations = {@Authorization("ROLE_COMMUNITY")},
            nickname = "getOffendersForResponsibleOfficer")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "OK", response = Offender.class ,responseContainer = "List"),
                    @ApiResponse(code = 400, message = "Invalid request", response = ErrorResponse.class),
                    @ApiResponse(code = 401, message = "Unauthorised", response = ErrorResponse.class),
                    @ApiResponse(code = 403, message = "Forbidden", response = ErrorResponse.class),
                    @ApiResponse(code = 500, message = "Unrecoverable error whilst processing request.", response = ErrorResponse.class)
            })
    @GetMapping(path = "/staff/staffCode/{staffCode}/managedOffenders")
    public List<Offender> getOffendersForResponsibleOfficer(
            @ApiParam(name = "staffCode", value = "Delius staff code of the responsible officer", example = "ASPD956", required = true)
            @NotNull @PathVariable(value = "staffCode") String staffCode) {
        return communityProxyService.getOffendersForResponsibleOfficer(staffCode);
    }

    @ApiOperation(
            value = "Return the responsible officer (RO) for an offender",
            notes = "Accepts a nomsNumber (offenderNo) in the format A9999AA",
            authorizations = {@Authorization("ROLE_COMMUNITY")},
            nickname = "getResponsibleOfficerForOffender")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "OK", response = ResponsibleOfficer.class),
                    @ApiResponse(code = 400, message = "Invalid request", response = ErrorResponse.class),
                    @ApiResponse(code = 401, message = "Unauthorised", response = ErrorResponse.class),
                    @ApiResponse(code = 403, message = "Forbidden", response = ErrorResponse.class),
                    @ApiResponse(code = 500, message = "Unrecoverable error whilst processing request.", response = ErrorResponse.class)
            })
    @GetMapping(path = "/offenders/nomsNumber/{nomsNumber}/responsibleOfficers")
    public ResponsibleOfficer getResponsibleOfficerForOffender(
            @ApiParam(name = "nomsNumber", value = "Noms ID for the offender", example = "A1234BB", required = true)
            @NotNull @PathVariable(value = "nomsNumber") String nomsNumber) {
        return communityProxyService.getResponsibleOfficerForOffender(nomsNumber);
    }
}

