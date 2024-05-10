package com.print.models.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class UploadDBDTO {
    String fileType;
    String templateName;
    String templateShortId;
    String effectiveStartDate;
    String effectiveEndDate;
    Boolean isActive;
}
