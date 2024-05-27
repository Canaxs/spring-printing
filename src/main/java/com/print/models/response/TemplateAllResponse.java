package com.print.models.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TemplateAllResponse {
    private Integer allTemplateNumber;
    private Integer allReceiptNumber;
    private Integer allInvoiceNumber;
    private Integer allExpiredTemplateNumber;
}
