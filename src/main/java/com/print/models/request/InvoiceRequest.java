package com.print.models.request;

import lombok.Data;

@Data
public class InvoiceRequest {
    String name;
    String surname;
    String address;
    Long ibanNumber;
    String branchName;
    Long accountNumber;
    Long paymentTotal;
}
