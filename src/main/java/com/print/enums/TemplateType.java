package com.print.enums;

import com.print.common.Constants;
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
    public static String convertPathOf(String step) {
        return switch (step) {
            case "receipt" -> Constants.folderReceiptPdfAddress;
            case "invoice" -> Constants.folderInvoicePdfAddress;
            default -> null;
        };
    }
    public static String convertPath(String step) {
        return switch (step) {
            case "receipt" -> Constants.folderReceiptAddress2;
            case "invoice" -> Constants.folderInvoiceAddress2;
            default -> null;
        };
    }
    public static String convertImagePath(String step) {
        return switch (step) {
            case "receipt" -> Constants.folderReceiptImageAddress;
            case "invoice" -> Constants.folderInvoiceImageAddress;
            default -> null;
        };
    }
}
