package com.print.service.Impl;

import com.google.api.Logging;
import com.ironsoftware.ironpdf.PdfDocument;
import com.lowagie.text.pdf.PdfWriter;
import com.print.common.Constants;
import com.print.models.dto.ReceiptDTO;
import com.print.persistence.entity.Receipt;
import com.print.service.TemplateService;
import org.apache.commons.lang3.RandomStringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;
import com.lowagie.text.*;
import org.xhtmlrenderer.simple.PDFRenderer;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class TemplateServiceImpl implements TemplateService {


    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${randomid}")
    private String randomIdKey;

    @Override
    public void htmlEditData(ReceiptDTO receiptDTO){
        try {
            Resource template = resourceLoader.getResource(Constants.folderAddress + receiptDTO.getTemplateName() + ".html");
            File file = new File(template.getURI());
            Document document = Jsoup.parse(file, "UTF-8");

            Class<ReceiptDTO> clz = ReceiptDTO.class;
            for (Method m : clz.getDeclaredMethods()) {
                for (Parameter p : m.getParameters()) {
                    if(document.getElementById(p.getName()) != null) {
                        Element div = document.getElementById(p.getName());
                        div.html(receiptDTO.convert(p.getName()).toString());
                    }
                }
            }
            printIronPdf(document);
        }
        catch (Exception e) {
            System.out.println("Exception: "+e.getMessage());
        }
    }

    @Override
    public void printIronPdf(Document document) {
        try {
            PdfDocument myPdf = PdfDocument.renderHtmlAsPdf(document.outerHtml());

            String shortId = RandomStringUtils.random(8, randomIdKey);
            //Save the PdfDocument to a file
            myPdf.saveAs(Path.of(Constants.folderPdfAddress,shortId + ".pdf"));

            openBrowser(shortId);
        }
        catch (Exception e) {
            System.out.println("Exception: "+e.getMessage());
        }
    }

    @Override
    public void openBrowser(String shortId) {
        try {
            String path = new File(".").getCanonicalPath() + Constants.folderPdfAddress2 + shortId + ".pdf";
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/C", "explorer "+path);
            processBuilder.start();

        }
        catch (Exception e) {
            System.out.println("Exception: "+e.getMessage());
        }
    }
}
