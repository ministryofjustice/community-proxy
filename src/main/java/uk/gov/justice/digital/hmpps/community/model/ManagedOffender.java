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

    @ApiModelProperty(required = true)
    private String staffCode;
    @ApiModelProperty(required = true)
    private Long offenderId;
    @ApiModelProperty(required = true)
    private String nomsNumber;
    @ApiModelProperty(required = true)
    private String crnNumber;
    @ApiModelProperty(required = true)
    private String offenderSurname;
    @ApiModelProperty(required = true)
    private boolean isCurrentRo;
    @ApiModelProperty(required = true)
    private boolean isCurrentOm;
    @ApiModelProperty(required = true)
    private boolean isCurrentPom;
    @ApiModelProperty(required = true)
    private LocalDate omStartDate;
    @ApiModelProperty(required = true)
    private LocalDate omEndDate;
}
