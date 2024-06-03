package com.print.service.Impl;

import com.print.common.exception.ModelException;
import com.print.models.dto.InvoiceDTO;
import com.print.models.dto.ReceiptDTO;
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

    @Override
    public ReceiptDTO converterReceiptDTO(Receipt receipt,String templateName) {
        ReceiptDTO receiptDTO = new ReceiptDTO();
        receiptDTO.setTitle(receipt.getTitle());
        receiptDTO.setName(receipt.getName());
        receiptDTO.setSurname(receipt.getSurname());
        receiptDTO.setAddressCity(receipt.getAddressCity());
        receiptDTO.setAddressStreet(receipt.getAddressStreet());
        receiptDTO.setAddressCounty(receipt.getAddressCounty());
        receiptDTO.setAddress(receipt.getAddress());
        receiptDTO.setIbanNumber(receipt.getIbanNumber());
        receiptDTO.setBranchName(receipt.getBranchName());
        receiptDTO.setAccountNumber(receipt.getAccountNumber());
        receiptDTO.setPaymentTotal(receipt.getPaymentTotal());
        receiptDTO.setTemplateName(templateName);

        receiptDTO.setWritingAreaTitle(receipt.getWritingAreaTitle());
        receiptDTO.setWritingArea1(receipt.getWritingArea1());
        receiptDTO.setWritingArea2(receipt.getWritingArea2());
        receiptDTO.setWritingArea3(receipt.getWritingArea3());
        receiptDTO.setWritingArea4(receipt.getWritingArea4());
        receiptDTO.setWritingArea5(receipt.getWritingArea5());
        receiptDTO.setWritingArea6(receipt.getWritingArea6());
        receiptDTO.setWritingArea7(receipt.getWritingArea7());
        receiptDTO.setWritingArea8(receipt.getWritingArea8());
        receiptDTO.setWritingArea9(receipt.getWritingArea9());
        return receiptDTO;
    }

    @Override
    public InvoiceDTO converterInvoiceDTO(Invoice invoice,String templateName) {
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.setDealerCompanyName(invoice.getDealerCompanyName());
        invoiceDTO.setDealerAddressCity(invoice.getDealerAddressCity());
        invoiceDTO.setDealerAddressCounty(invoice.getDealerAddressCounty());
        invoiceDTO.setDealerAddress(invoice.getDealerAddress());
        invoiceDTO.setDealerPhoneNumber(invoice.getDealerPhoneNumber());
        invoiceDTO.setDealerMailAddress(invoice.getDealerMailAddress());
        invoiceDTO.setDealerVKN(invoice.getDealerVKN());
        invoiceDTO.setDealerTradeNumber(invoice.getDealerTradeNumber());

        invoiceDTO.setCustomerName(invoice.getCustomerName());
        invoiceDTO.setCustomerSurname(invoice.getCustomerSurname());
        invoiceDTO.setDealerAddressCity(invoice.getDealerAddressCity());
        invoiceDTO.setCustomerAddressCounty(invoice.getCustomerAddressCounty());
        invoiceDTO.setCustomerAddress(invoice.getCustomerAddress());
        invoiceDTO.setCustomerPhoneNumber(invoice.getCustomerPhoneNumber());
        invoiceDTO.setCustomerMailAddress(invoice.getCustomerMailAddress());
        invoiceDTO.setCustomerTCKN(invoice.getCustomerTCKN());

        invoiceDTO.setProducts(invoice.getProducts());
        invoiceDTO.setTemplateName(templateName);
        return invoiceDTO;
    }
}
