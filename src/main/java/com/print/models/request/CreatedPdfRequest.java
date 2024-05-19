package com.print.models.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreatedPdfRequest {
    private Long checkId;
    private String templateName;
    private String templateType;
}
