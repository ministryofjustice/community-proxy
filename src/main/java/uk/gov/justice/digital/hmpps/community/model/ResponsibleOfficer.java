package uk.gov.justice.digital.hmpps.community.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
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
    private String username;
    private String staffCode;
    private String forenames;
    private String surname;
}
