package com.print.models.response;

import com.print.persistence.entity.InvoiceProduct;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class InvoiceResponse {
    // It was made for testing and will be arranged in accordance with the invoice information later.
    private String dealerCompanyName;
    private String dealerTradeNumber;
    private String customerName;
    private String customerSurname;
    private List<InvoiceProduct> products;
}
