package com.print.service;

import com.print.enums.TemplateType;
import com.print.models.dto.GuestPdfDTO;
import com.print.models.dto.InvoiceDTO;
import com.print.models.dto.ReceiptDTO;
import com.print.models.request.CreatedPdfRequest;
import com.print.models.request.TempUpdateRequest;
import com.print.models.response.ImageResponse;
import com.print.models.response.TemplateAllResponse;
import com.print.persistence.entity.TemplateTable;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;

public interface TemplateService {

    GuestPdfDTO htmlEditDataReceipt(ReceiptDTO receiptDTO);

    GuestPdfDTO htmlEditDataInvoice(InvoiceDTO invoiceDTO);

    void printIronPdf(Document document,String templateType,String shortId);

    void printFlyingPdf(Document document,String shortId,String templateType);

    void openBrowser(String shortId,String templateType);

    byte[] printPDF(String shortId,String templateType);

    String getBringSuitableTemplate(String templateName, TemplateType templateType);

    List<ImageResponse> getImagesTemplateType(String templateType);

    TemplateAllResponse getAllTemplateInfo();

    String deleteTemplateId(Long templateId);

    List<TemplateTable> getAllTemplate();

    TemplateTable updateTemplate(TempUpdateRequest tempUpdateRequest);
}
