package com.print.service;

import com.print.models.dto.ReceiptDTO;
import org.jsoup.nodes.Document;

import java.io.IOException;

public interface TemplateService {

    void htmlEditData(ReceiptDTO receiptDTO) throws IOException;

    void printIronPdf(Document document);

    void printFlyingPdf(Document document);

    void openBrowser(String shortId);
}
