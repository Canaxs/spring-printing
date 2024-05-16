package com.print.enums;

import com.print.models.dto.InvoiceDTO;
import com.print.models.dto.ReceiptDTO;
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
    public static Class<?> convertClass(String step) {
        return switch (step) {
            case "receipt" -> ReceiptDTO.class;
            case "invoice" -> InvoiceDTO.class;
            default -> null;
        };
    }

    public static String convertString(TemplateType templateType) {
        return switch (templateType) {
            case RECEIPT -> "receipt";
            case INVOICE -> "invoice";
            default -> null;
        };
    }
}
