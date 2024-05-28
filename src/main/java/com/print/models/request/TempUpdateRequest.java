package com.print.models.request;

import lombok.Data;

import java.util.Date;

@Data
public class TempUpdateRequest {
    private Long Id;
    private Boolean isActive;
    private String templateName;
    private Date effectiveStartDate;
    private Date effectiveEndDate;
}
