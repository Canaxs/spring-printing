package com.print.controller;

import com.print.persistence.entity.Invoice;
import com.print.persistence.entity.Receipt;
import com.print.service.ModelService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/model")
public class ModelController {

    private final ModelService modelService;

    public ModelController(ModelService modelService) {
        this.modelService = modelService;
    }

    @PostMapping("/create/receipt")
    private ResponseEntity<Receipt> modelReceiptCreate(@RequestBody Receipt receipt) {
        return ResponseEntity.ok(modelService.modelReceiptCreate(receipt));
    }
    @PostMapping("/create/invoice")
    private ResponseEntity<Invoice> modelInvoiceCreate(@RequestBody Invoice invoice) {
        return ResponseEntity.ok(modelService.modelInvoiceCreate(invoice));
    }
    @DeleteMapping("/delete/receipt/{modelId}")
    private ResponseEntity<Receipt> modelReceiptDelete(@PathVariable("modelId") Long modelId) {
        return ResponseEntity.ok(modelService.modelReceiptDelete(modelId));
    }
    @DeleteMapping("/delete/invoice/{modelId}")
    private ResponseEntity<Invoice> modelInvoiceDelete(@PathVariable("modelId") Long modelId) {
        return ResponseEntity.ok(modelService.modelInvoiceDelete(modelId));
    }

}
