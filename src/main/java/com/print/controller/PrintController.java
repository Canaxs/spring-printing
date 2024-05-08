package com.print.controller;

import com.print.models.dto.GuestPdfDTO;
import com.print.models.dto.ReceiptDTO;
import com.print.service.TemplateService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/print")
public class PrintController {

    private TemplateService templateService;

    public PrintController(TemplateService templateService) {
        this.templateService = templateService;
    }


    @PostMapping("/getGuestPdf")
    public ResponseEntity<byte[]> getGuestPdf(@RequestBody ReceiptDTO receiptDTO) throws IOException {
        GuestPdfDTO guestPdfDTO= templateService.htmlReceiptEditData(receiptDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        // Here you have to set the actual filename of your pdf
        headers.setContentDispositionFormData(guestPdfDTO.getFilename(), guestPdfDTO.getFilename());
        headers.add("Accept-Encoding", "UTF-8");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<>(guestPdfDTO.getArray(), headers, HttpStatus.OK);
        return response;
    }
}
