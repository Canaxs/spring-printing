package com.print.service.Impl;

import com.print.common.exception.CheckException;
import com.print.models.request.InvoiceRequest;
import com.print.models.request.ReceiptRequest;
import com.print.models.response.InvoiceResponse;
import com.print.models.response.ReceiptResponse;
import com.print.persistence.entity.Invoice;
import com.print.persistence.entity.Receipt;
import com.print.persistence.repository.InvoiceRepository;
import com.print.persistence.repository.ReceiptRepository;
import com.print.service.CheckService;
import org.springframework.stereotype.Service;

@Service
public class CheckServiceImpl implements CheckService {

    // I did not choose to use the mapper class because I often get errors during the install and run phases.
    // Kurulum ve çalıştırma aşamalarında sıklıkla hata aldığım için mapper sınıfını kullanmayı tercih etmedim.

    private final ReceiptRepository receiptRepository;

    private final InvoiceRepository invoiceRepository;

    public CheckServiceImpl(ReceiptRepository receiptRepository, InvoiceRepository invoiceRepository) {
        this.receiptRepository = receiptRepository;
        this.invoiceRepository = invoiceRepository;
    }


    @Override
    public ReceiptResponse createReceipt(ReceiptRequest receiptRequest) {
        Receipt receipt = new Receipt();
        receipt.setName(receiptRequest.getName());
        receipt.setSurname(receiptRequest.getSurname());
        receipt.setAddress(receiptRequest.getAddress());
        receipt.setIbanNumber(receiptRequest.getIbanNumber());
        receipt.setBranchName(receiptRequest.getBranchName());
        receipt.setAccountNumber(receiptRequest.getAccountNumber());
        receipt.setPaymentTotal(receiptRequest.getPaymentTotal());
        try {
            receiptRepository.save(receipt);
        }
        catch (Exception e) {
            throw new CheckException("");
        }
        return ReceiptResponse.builder()
                .name(receipt.getName())
                .surname(receipt.getSurname())
                .address(receipt.getAddress())
                .ibanNumber(receipt.getIbanNumber())
                .branchName(receipt.getBranchName())
                .accountNumber(receipt.getAccountNumber())
                .paymentTotal(receipt.getPaymentTotal())
                .build();
    }

    @Override
    public InvoiceResponse createInvoice(InvoiceRequest invoiceRequest) {
        Invoice invoice = new Invoice();
        invoice.setName(invoiceRequest.getName());
        invoice.setSurname(invoiceRequest.getSurname());
        invoice.setAddress(invoiceRequest.getAddress());
        invoice.setIbanNumber(invoiceRequest.getIbanNumber());
        invoice.setBranchName(invoiceRequest.getBranchName());
        invoice.setAccountNumber(invoiceRequest.getAccountNumber());
        invoice.setPaymentTotal(invoiceRequest.getPaymentTotal());
        try {
            invoiceRepository.save(invoice);
        }
        catch (Exception e) {
            throw new CheckException("");
        }
        return InvoiceResponse.builder()
                .name(invoice.getName())
                .surname(invoice.getSurname())
                .address(invoice.getAddress())
                .ibanNumber(invoice.getIbanNumber())
                .branchName(invoice.getBranchName())
                .accountNumber(invoice.getAccountNumber())
                .paymentTotal(invoice.getPaymentTotal())
                .build();
    }
}
