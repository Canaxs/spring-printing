package com.print.service;

import com.print.models.request.DeleteCheck;
import com.print.models.request.InvoiceRequest;
import com.print.models.request.ReceiptRequest;
import com.print.models.response.InvoiceResponse;
import com.print.models.response.ReceiptResponse;

public interface CheckService {

    ReceiptResponse createReceipt(ReceiptRequest receiptRequest);

    InvoiceResponse createInvoice(InvoiceRequest invoiceRequest);

    String deleteReceipt(DeleteCheck deleteCheck);
    String deleteInvoice(DeleteCheck deleteCheck);

}
