package uk.gov.justice.digital.hmpps.community.controllers;

import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.digital.hmpps.community.model.ErrorResponse;
import uk.gov.justice.digital.hmpps.community.model.ResponsibleOfficer;
import uk.gov.justice.digital.hmpps.community.services.CommunityProxyService;

import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Api(tags = {"offenders"})
@RestController
@RequestMapping(value = "api", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class OffendersResource {

    private final CommunityProxyService communityProxyService;

    @ApiOperation(
            value = "Return the responsible officer (RO) for an offender",
            notes = "Accepts a NOMIS offender nomsNumber in the format A9999AA",
            authorizations = {@Authorization("ROLE_COMMUNITY")},
            nickname = "getResponsibleOfficersForOffender")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "OK", response = ResponsibleOfficer.class, responseContainer = "List"),
                    @ApiResponse(code = 400, message = "Invalid request", response = ErrorResponse.class),
                    @ApiResponse(code = 401, message = "Unauthorised", response = ErrorResponse.class),
                    @ApiResponse(code = 403, message = "Forbidden", response = ErrorResponse.class),
                    @ApiResponse(code = 500, message = "Unrecoverable error whilst processing request.", response = ErrorResponse.class)
            })
    @GetMapping(path = "/offenders/nomsNumber/{nomsNumber}/responsibleOfficers")
    public List<ResponsibleOfficer> getResponsibleOfficersForOffender(
            @ApiParam(name = "nomsNumber", value = "Nomis number for the offender", example = "A1234BB", required = true)
            @NotNull @PathVariable(value = "nomsNumber") final String nomsNumber) {
        return communityProxyService.getResponsibleOfficersForOffender(nomsNumber);
    }

    @ApiOperation(
            value = "Return the convictions (AKA Delius Event) for an offender",
            notes = "See http://deliusapi-dev.sbw4jt6rsq.eu-west-2.elasticbeanstalk.com/api/swagger-ui.html#!/Offender32convictions/getOffenderConvictionsByNomsNumberUsingGET for further details",
            authorizations = {@Authorization("ROLE_COMMUNITY")},
            nickname = "getConvictionsForOffender")
    @GetMapping(path = "/offenders/nomsNumber/{nomsNumber}/convictions")
    public String getConvictionsForOffender(
            @ApiParam(name = "nomsNumber", value = "Nomis number for the offender", example = "A1234BB", required = true)
            @NotNull @PathVariable(value = "nomsNumber") final String nomsNumber) {
        return communityProxyService.getConvictionsForOffender(nomsNumber);
    }

    @ApiOperation(
            value = "Return the details for an offender",
            notes = "See http://deliusapi-dev.sbw4jt6rsq.eu-west-2.elasticbeanstalk.com/api/swagger-ui.html#!/Offenders/getOffenderSummaryByNomsNumberUsingGET",
            authorizations = {@Authorization("ROLE_COMMUNITY")},
            nickname = "getOffenderDetails")
    @GetMapping(path = "/offenders/nomsNumber/{nomsNumber}")
    public String getOffenderDetails(
            @ApiParam(name = "nomsNumber", value = "Nomis number for the offender", example = "A1234BB", required = true)
            @NotNull @PathVariable(value = "nomsNumber") final String nomsNumber) {
        return communityProxyService.getOffenderDetails(nomsNumber);
    }
}

