package com.print.enums;

import com.print.persistence.entity.Invoice;
import com.print.persistence.entity.Receipt;

import java.util.Objects;

public enum TemplateType {
    RECEIPT,
    INVOICE;

    public static TemplateType convert(String step) {
        return switch (step) {
            case "receipt" -> TemplateType.RECEIPT;
            case "invoice" -> TemplateType.INVOICE;
            default -> null;
        };
    }
    public static Class convertClass(String step) {
        return switch (step) {
            case "receipt" -> Receipt.class;
            case "invoice" -> Invoice.class;
            default -> null;
        };
    }
}
