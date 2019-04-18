package uk.gov.justice.digital.hmpps.community.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@ApiModel(description = "ResponsibleOfficer")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Data
public class ResponsibleOfficer {

    @ApiModelProperty(value = "Delius username", example = "KENWOODG", position = 0)
    private String username;

    @ApiModelProperty(value = "Delius staff code", example = "C9999X", position = 1)
    private String staffCode;

    @ApiModelProperty(value = "Forenames", example = "JOHN ROBERT", position = 2)
    private String forenames;

    @ApiModelProperty(value = "Surname", example = "SMITH", position = 3)
    private String surname;
}
