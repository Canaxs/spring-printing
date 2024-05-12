package com.print.models.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvoiceResponse {
    // It was made for testing and will be arranged in accordance with the invoice information later.
    String name;
    String surname;
    String address;
    Long ibanNumber;
    String branchName;
    Long accountNumber;
    Long paymentTotal;
}
