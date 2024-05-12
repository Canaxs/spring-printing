package com.print.models.request;

import lombok.Data;

@Data
public class ReceiptRequest {
    String name;
    String surname;
    String address;
    Long ibanNumber;
    String branchName;
    Long accountNumber;
    Long paymentTotal;
}
