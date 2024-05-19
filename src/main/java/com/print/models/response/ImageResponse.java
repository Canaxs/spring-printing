package com.print.models.response;

import lombok.Data;

@Data
public class ImageResponse {
    private String shortId;
    private String templateName;
    private byte[] imageByte;
}
