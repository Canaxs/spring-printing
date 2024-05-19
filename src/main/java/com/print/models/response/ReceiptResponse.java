package com.print.models.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReceiptResponse {
    private String name;
    private String surname;
    private String address;
    private Long ibanNumber;
    private String branchName;
    private Long accountNumber;
    private Long paymentTotal;
}
