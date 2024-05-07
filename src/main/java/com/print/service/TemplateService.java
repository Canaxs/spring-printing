package com.print.service;

import com.print.models.dto.GuestPdfDTO;
import com.print.models.dto.ReceiptDTO;
import org.jsoup.nodes.Document;

import java.io.IOException;

public interface TemplateService {

    GuestPdfDTO htmlEditData(ReceiptDTO receiptDTO) throws IOException;

    void printIronPdf(Document document);

    void printFlyingPdf(Document document,String shortId);

    void openBrowser(String shortId);

    byte[] printPDF(String shortId);
}
