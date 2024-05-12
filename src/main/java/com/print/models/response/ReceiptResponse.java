package com.print.models.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReceiptResponse {
    String name;
    String surname;
    String address;
    Long ibanNumber;
    String branchName;
    Long accountNumber;
    Long paymentTotal;
}
