package com.print.models.request;

import lombok.Data;

@Data
public class ReadyPDFRequest {
    private Long modelId;
    private String templateName;
}
