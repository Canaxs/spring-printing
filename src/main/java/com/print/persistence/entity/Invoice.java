package com.print.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table
@Data
public class Invoice extends BaseEntity {

    // It was made for testing and will be arranged in accordance with the invoice information later.

    private String dealerCompanyName;
    private String dealerAddressCity;
    private String dealerAddressCounty;
    private String dealerAddress;
    private String dealerPhoneNumber;
    private String dealerMailAddress;
    private String dealerVKN;
    private String dealerTradeNumber;

    private String customerName;
    private String customerSurname;
    private String customerAddressCity;
    private String customerAddressCounty;
    private String customerAddress;
    private String customerPhoneNumber;
    private String customerTCKN;

    private List<InvoiceProduct> products;

}
