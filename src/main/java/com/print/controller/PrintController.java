package com.print.controller;

import com.print.models.dto.GuestPdfDTO;
import com.print.models.dto.InvoiceDTO;
import com.print.models.dto.ReceiptDTO;
import com.print.models.request.CreatedPdfRequest;
import com.print.models.request.ReadyPDFRequest;
import com.print.models.response.ImageResponse;
import com.print.persistence.entity.Invoice;
import com.print.persistence.entity.Receipt;
import com.print.service.ModelService;
import com.print.service.PrintLogService;
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
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/print")
public class PrintController {

    private final TemplateService templateService;

    private final ModelService modelService;

    public PrintController(TemplateService templateService, ModelService modelService) {
        this.templateService = templateService;
        this.modelService = modelService;
    }


    @PostMapping("/getGuestReceiptPdf")
    public ResponseEntity<byte[]> getGuestReceiptPdf(@RequestBody ReceiptDTO receiptDTO){
        GuestPdfDTO guestPdfDTO= templateService.htmlEditDataReceipt(receiptDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        // Here you have to set the actual filename of your pdf
        headers.setContentDispositionFormData(guestPdfDTO.getFilename(), guestPdfDTO.getFilename());
        headers.add("Accept-Encoding", "UTF-8");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<>(guestPdfDTO.getArray(), headers, HttpStatus.OK);
        return response;
    }
    @PostMapping("/getGuestInvoicePdf")
    public ResponseEntity<byte[]> getGuestInvoicePdf(@RequestBody InvoiceDTO invoiceDTO){
        GuestPdfDTO guestPdfDTO= templateService.htmlEditDataInvoice(invoiceDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        // Here you have to set the actual filename of your pdf
        headers.setContentDispositionFormData(guestPdfDTO.getFilename(), guestPdfDTO.getFilename());
        headers.add("Accept-Encoding", "UTF-8");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<>(guestPdfDTO.getArray(), headers, HttpStatus.OK);
        return response;
    }

    @PostMapping("/getCreatedPdf")
    public ResponseEntity<byte[]> getCreatedPdf(@RequestBody CreatedPdfRequest createdPdfRequest) {
        GuestPdfDTO guestPdfDTO= templateService.getCreatedPdf(createdPdfRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        // Here you have to set the actual filename of your pdf
        headers.setContentDispositionFormData(guestPdfDTO.getFilename(), guestPdfDTO.getFilename());
        headers.add("Accept-Encoding", "UTF-8");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<>(guestPdfDTO.getArray(), headers, HttpStatus.OK);
        return response;
    }

    @GetMapping("/getImages/{templateType}")
    public List<ImageResponse> getImagesTemplateType(@PathVariable("templateType") String templateType) {
        return templateService.getImagesTemplateType(templateType.toLowerCase());
    }

    @PostMapping("/getReadyReceiptPdf")
    public ResponseEntity<byte[]> getReadyReceiptPdf(@RequestBody ReadyPDFRequest readyPDFRequest){
        Receipt receipt = modelService.getReceiptModel(readyPDFRequest.getModelId());
        ReceiptDTO receiptDTO = modelService.converterReceiptDTO(receipt,readyPDFRequest.getTemplateName());

        GuestPdfDTO guestPdfDTO= templateService.htmlEditDataReceipt(receiptDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        // Here you have to set the actual filename of your pdf
        headers.setContentDispositionFormData(guestPdfDTO.getFilename(), guestPdfDTO.getFilename());
        headers.add("Accept-Encoding", "UTF-8");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<>(guestPdfDTO.getArray(), headers, HttpStatus.OK);
        return response;
    }
    @PostMapping("/getReadyInvoicePdf")
    public ResponseEntity<byte[]> getReadyInvoicePdf(@RequestBody ReadyPDFRequest readyPDFRequest){
        Invoice invoice = modelService.getInvoiceModel(readyPDFRequest.getModelId());
        InvoiceDTO invoiceDTO = modelService.converterInvoiceDTO(invoice,readyPDFRequest.getTemplateName());

        GuestPdfDTO guestPdfDTO= templateService.htmlEditDataInvoice(invoiceDTO);

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
