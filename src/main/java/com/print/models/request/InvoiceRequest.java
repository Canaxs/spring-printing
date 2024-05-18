package com.print.models.request;

import com.print.persistence.entity.InvoiceProduct;
import lombok.Data;

import java.util.List;

@Data
public class InvoiceRequest {
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
    private String customerMailAddress;
    private String customerTCKN;

    private List<InvoiceProduct> products;
}
