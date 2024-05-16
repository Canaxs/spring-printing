package com.print.models.dto;

import com.print.persistence.entity.InvoiceProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDTO {
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
    private String templateName;

    public Object convert(String step) {
        return switch (step) {
            case "dealerCompanyName" -> dealerCompanyName;
            case "dealerAddressCity" -> dealerAddressCity;
            case "dealerAddressCounty" -> dealerAddressCounty;
            case "dealerAddress" -> dealerAddress;
            case "dealerPhoneNumber" -> dealerPhoneNumber;
            case "dealerMailAddress" -> dealerMailAddress;
            case "dealerVKN" -> dealerVKN;
            case "dealerTradeNumber" -> dealerTradeNumber;
            case "customerName" -> customerName;
            case "customerSurname" -> customerSurname;
            case "customerAddressCity" -> customerAddressCity;
            case "customerAddressCounty" -> customerAddressCounty;
            case "customerAddress" -> customerAddress;
            case "customerPhoneNumber" -> customerPhoneNumber;
            case "customerTCKN" -> customerTCKN;
            case "customerMailAddress" -> customerMailAddress;
            default -> null;
        };
    }
}
