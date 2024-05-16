package com.print.persistence.entity;

import lombok.Data;

@Data
public class InvoiceProduct {
    private String name;
    private Long unitPrice;
    private Long amount;
}
