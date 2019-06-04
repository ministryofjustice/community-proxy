package uk.gov.justice.digital.hmpps.community.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDate;

@ApiModel(description = "ManagedOffender")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Data
public class ManagedOffender {

    @ApiModelProperty(required = true, value="The officer code for this staff member", example="PX0001")
    private String staffCode;
    @ApiModelProperty(required = true, value="The internal Delius ID for the offender", example="123345")
    private Long offenderId;
    @ApiModelProperty(required = true, value="The Noms number for the offender", example="GK8876H")
    private String nomsNumber;
    @ApiModelProperty(required = true, value="The CRN number in Delius for the offender", example="T99SM")
    private String crnNumber;
    @ApiModelProperty(required = true, value="The offender surname", example="SMITH")
    private String offenderSurname;
    @ApiModelProperty(required = true, value="True if this staff member is the the current responsible officer", example="true")
    private boolean isCurrentRo;
    @ApiModelProperty(required = true, value="True if this staff member is a current offender manager for the offender", example="true")
    private boolean isCurrentOm;
    @ApiModelProperty(required = true, value="True if this staff member is a current prison offender manager for the offender", example="false")
    private boolean isCurrentPom;
    @ApiModelProperty(required = true, value="The date that the offender manager was assigned", example="12/03/2019")
    private LocalDate omStartDate;
    @ApiModelProperty(required = true, value="The date that the offender manager stopped their assignment to this offender (null if active)", example="")
    private LocalDate omEndDate;
}
