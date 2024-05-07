package com.print.models.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GuestPdfDTO {
    private byte[] array;
    private String filename;
}
