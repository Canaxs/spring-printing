package com.print.enums;

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
}
