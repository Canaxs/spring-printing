package com.print.controller;

import com.print.models.dto.ReceiptDTO;
import com.print.service.TemplateService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/print")
public class PrintController {

    private TemplateService templateService;

    public PrintController(TemplateService templateService) {
        this.templateService = templateService;
    }


    @PostMapping("/getGuestPdf")
    public String getGuestPdf(@RequestBody ReceiptDTO receiptDTO) throws IOException {
        templateService.htmlEditData(receiptDTO);
        return null;
    }
}
