package com.print.models.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class UploadDBDTO {
    private String fileType;
    private String templateName;
    private String templateShortId;
    private String effectiveStartDate;
    private String effectiveEndDate;
    private Boolean isActive;
}
