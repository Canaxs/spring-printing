package com.print.service;

import com.print.models.dto.InvoiceDTO;
import com.print.models.dto.ReceiptDTO;
import com.print.persistence.entity.Invoice;
import com.print.persistence.entity.Receipt;

public interface ModelService {
    Receipt modelReceiptCreate(Receipt receipt);

    Invoice modelInvoiceCreate(Invoice invoice);

    Receipt modelReceiptDelete (Long modelId);

    Invoice modelInvoiceDelete (Long modelId);

    Receipt getReceiptModel(Long modelId);

    Invoice getInvoiceModel(Long modelId);

    ReceiptDTO converterReceiptDTO(Receipt receipt,String templateName);

    InvoiceDTO converterInvoiceDTO(Invoice invoice, String templateName);
}
