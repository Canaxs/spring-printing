package com.print.service.Impl;

import com.print.common.exception.CheckException;
import com.print.models.request.DeleteCheck;
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
        receipt.setPaymentTotal(receiptRequest.getPaymentTotal());
        receipt.setAddress(receiptRequest.getAddress());
        receipt.setAccountNumber(receiptRequest.getAccountNumber());
        receipt.setBranchName(receiptRequest.getBranchName());
        receipt.setIbanNumber(receiptRequest.getIbanNumber());
        receipt.setAddressStreet(receiptRequest.getAddressStreet());
        receipt.setAddressCity(receiptRequest.getAddressCity());
        receipt.setAddressCounty(receiptRequest.getAddressCounty());

        receipt.setWritingAreaTitle(receiptRequest.getWritingAreaTitle());
        receipt.setWritingArea1(receiptRequest.getWritingArea1());
        receipt.setWritingArea2(receiptRequest.getWritingArea2());
        receipt.setWritingArea3(receiptRequest.getWritingArea3());
        receipt.setWritingArea4(receiptRequest.getWritingArea4());
        receipt.setWritingArea5(receiptRequest.getWritingArea5());
        receipt.setWritingArea6(receiptRequest.getWritingArea6());
        receipt.setWritingArea7(receiptRequest.getWritingArea7());
        receipt.setWritingArea8(receiptRequest.getWritingArea8());
        receipt.setWritingArea9(receiptRequest.getWritingArea9());
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
        invoice.setDealerAddress(invoiceRequest.getDealerAddress());
        invoice.setDealerVKN(invoiceRequest.getDealerVKN());
        invoice.setDealerAddressCity(invoiceRequest.getDealerAddressCity());
        invoice.setDealerMailAddress(invoiceRequest.getDealerMailAddress());
        invoice.setDealerCompanyName(invoiceRequest.getDealerCompanyName());
        invoice.setDealerPhoneNumber(invoiceRequest.getDealerPhoneNumber());
        invoice.setDealerTradeNumber(invoiceRequest.getDealerTradeNumber());
        invoice.setDealerAddressCounty(invoiceRequest.getDealerAddressCounty());

        invoice.setCustomerAddress(invoiceRequest.getCustomerAddress());
        invoice.setCustomerName(invoiceRequest.getCustomerName());
        invoice.setCustomerTCKN(invoiceRequest.getCustomerTCKN());
        invoice.setCustomerSurname(invoiceRequest.getCustomerSurname());
        invoice.setCustomerPhoneNumber(invoiceRequest.getCustomerPhoneNumber());
        invoice.setCustomerAddressCounty(invoiceRequest.getCustomerAddressCounty());
        invoice.setCustomerAddressCity(invoiceRequest.getCustomerAddressCity());

        invoice.setProducts(invoiceRequest.getProducts());

        try {
            invoiceRepository.save(invoice);
        }
        catch (Exception e) {
            throw new CheckException("");
        }
        return InvoiceResponse.builder()
                .customerSurname(invoice.getCustomerSurname())
                .customerName(invoice.getCustomerName())
                .products(invoice.getProducts())
                .dealerTradeNumber(invoice.getDealerTradeNumber())
                .dealerCompanyName(invoice.getDealerCompanyName())
                .build();
    }

    @Override
    public String deleteReceipt(DeleteCheck deleteCheck) {
        try {
            Receipt receipt = receiptRepository.getReferenceById(deleteCheck.getCheckId());
            receiptRepository.delete(receipt);
        }
        catch (Exception e) {
            throw new CheckException("Error occurred while deleting receipt");
        }
        return "Successfully deleted";
    }

    @Override
    public String deleteInvoice(DeleteCheck deleteCheck) {
        try {
            Invoice invoice = invoiceRepository.getReferenceById(deleteCheck.getCheckId());
            invoiceRepository.delete(invoice);
        }
        catch (Exception e) {
            throw new CheckException("Error occurred while deleting invoice");
        }
        return "Successfully deleted";
    }
}
