package uk.gov.justice.digital.hmpps.community.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDate;

@ApiModel(description = "ResponsibleOfficer")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Data
public class ResponsibleOfficer {

    @ApiModelProperty(required=true, position=0, value="Offender NOMS numbers", example="G1345UK")
    private String nomsNumber;
    @ApiModelProperty(required=true, position=1, value="Delius internal ID for RO", example="1234")
    private Long responsibleOfficerId;
    @ApiModelProperty(value="Delius internal ID for OM", example="3456")
    private Long offenderManagerId;
    @ApiModelProperty(value="Delius internal ID for POM", example="7899")
    private Long prisonOffenderManagerId;
    @ApiModelProperty(required = true, value="The Delius officer code of this staff member",example="DG445J")
    private String staffCode;
    @ApiModelProperty(required = true, value="The officer surname")
    private String surname;
    @ApiModelProperty(value="The officer forenames (combined)")
    private String forenames;
    @ApiModelProperty(value="The provider team code")
    private String providerTeamCode;
    @ApiModelProperty(value="The provider team description")
    private String providerTeamDescription;
    @ApiModelProperty(value="The local delivery unit code")
    private String lduCode;
    @ApiModelProperty(value="The local deliery unit description")
    private String lduDescription;
    @ApiModelProperty(value="The probation area code")
    private String probationAreaCode;
    @ApiModelProperty(value="The probation area description")
    private String probationAreaDescription;
    @ApiModelProperty(required = true, value="True is the officer is the current RO for the requested offender")
    private boolean isCurrentRo;
    @ApiModelProperty(required = true, value="True if the officer is the current OM for the requested offender")
    private boolean isCurrentOm;
    @ApiModelProperty(required = true, value="True if the officer is the current POM for the requested offender")
    private boolean isCurrentPom;
    @ApiModelProperty(value="The date from which the officer was assigned")
    private LocalDate omStartDate;
    @ApiModelProperty(value="The date at which the officer ceased to be assigned")
    private LocalDate omEndDate;
}
