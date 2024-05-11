package com.print.models.dto;

import lombok.Data;

@Data
public class ReceiptDTO {
    private String name;
    private String surname;
    private String address;
    private Long ibanNumber;
    private String branchName;
    private Long accountNumber;
    private Long paymentTotal;
    private String templateName;
    private String templateType;

    public Object convert(String step) {
        return switch (step) {
            case "name" -> name;
            case "surname" -> surname;
            case "address" -> address;
            case "ibanNumber" -> ibanNumber;
            case "branchName" -> branchName;
            case "accountNumber" -> accountNumber;
            case "paymentTotal" -> paymentTotal;
            default -> null;
        };
    }
}
