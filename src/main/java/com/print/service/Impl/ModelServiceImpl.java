package com.print.service.Impl;

import com.print.common.exception.ModelException;
import com.print.persistence.entity.Invoice;
import com.print.persistence.entity.Receipt;
import com.print.persistence.repository.InvoiceRepository;
import com.print.persistence.repository.ReceiptRepository;
import com.print.service.ModelService;
import org.springframework.stereotype.Service;

@Service
public class ModelServiceImpl implements ModelService {

    private final ReceiptRepository receiptRepository;

    private final InvoiceRepository invoiceRepository;

    public ModelServiceImpl(ReceiptRepository receiptRepository, InvoiceRepository invoiceRepository) {
        this.receiptRepository = receiptRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public Receipt modelReceiptCreate(Receipt receipt) {
        return receiptRepository.save(receipt);
    }

    @Override
    public Invoice modelInvoiceCreate(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    @Override
    public Receipt modelReceiptDelete(Long modelId) {
        Receipt receipt = null;
        try {
            receipt = receiptRepository.getReferenceById(modelId);
            receiptRepository.delete(receipt);
        }
        catch (Exception e) {
            throw new ModelException("An error was encountered while creating the receipt model: "+e.getMessage());
        }
        return receipt;
    }

    @Override
    public Invoice modelInvoiceDelete(Long modelId) {
        Invoice invoice = null;
        try {
            invoice = invoiceRepository.getReferenceById(modelId);
            invoiceRepository.delete(invoice);
        }
        catch (Exception e) {
            throw new ModelException("An error was encountered while creating the invoice model: "+e.getMessage());
        }
        return invoice;
    }

    @Override
    public Receipt getReceiptModel(Long modelId) {
        return receiptRepository.getReferenceById(modelId);
    }

    @Override
    public Invoice getInvoiceModel(Long modelId) {
        return invoiceRepository.getReferenceById(modelId);
    }
}
