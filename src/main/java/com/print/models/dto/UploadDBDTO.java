package com.print.models.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UploadDBDTO {
    String fileType;
    String templateName;
    String templateShortId;
}
