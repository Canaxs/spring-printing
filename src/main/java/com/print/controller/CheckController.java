package com.print.controller;

import com.print.models.request.InvoiceRequest;
import com.print.models.request.ReceiptRequest;
import com.print.models.response.ResultResponse;
import com.print.service.CheckService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;

@RestController
@RequestMapping("/check")
public class CheckController {

    private final CheckService checkService;

    public CheckController(CheckService checkService) {
        this.checkService = checkService;
    }

    @PostMapping("/receipt/create")
    public ResponseEntity<ResultResponse> createReceipt(@RequestBody ReceiptRequest receiptRequest) {
        return ResponseEntity.ok(ResultResponse.builder()
                        .result(new ArrayList<>((Collection<?>) checkService.createReceipt(receiptRequest)))
                .build());
    }
    @PostMapping("/invoice/create")
    public ResponseEntity<ResultResponse> createInvoice(@RequestBody InvoiceRequest invoiceRequest) {
        return ResponseEntity.ok(ResultResponse.builder()
                .result(new ArrayList<>((Collection<?>) checkService.createInvoice(invoiceRequest)))
                .build());
    }
}
