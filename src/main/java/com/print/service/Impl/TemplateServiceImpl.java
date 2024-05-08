package com.print.service.Impl;

import com.google.api.Logging;
import com.ironsoftware.ironpdf.PdfDocument;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import com.print.common.Constants;
import com.print.models.dto.GuestPdfDTO;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
    public GuestPdfDTO htmlReceiptEditData(ReceiptDTO receiptDTO){
        String shortId = null;
        try {
            Resource template = resourceLoader.getResource(Constants.folderReceiptAddress +receiptDTO.getTemplateName() + ".html");
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
            shortId = getRandomIdKey();
            //printIronPdf(document);
            printFlyingPdf(document,shortId);
        }
        catch (Exception e) {
            System.out.println("Exception: "+e.getMessage());
        }
        return GuestPdfDTO.builder()
                .filename(getPath(shortId))
                .array(printPDF(shortId)).
                build();
    }

    @Override
    public void printIronPdf(Document document) {
        try {
            PdfDocument myPdf = PdfDocument.renderHtmlAsPdf(document.outerHtml());

            String shortId = getRandomIdKey();

            //Save the PdfDocument to a file
            myPdf.saveAs(Path.of(Constants.folderReceiptPdfAddress,shortId + ".pdf"));

            openBrowser(shortId);
        }
        catch (Exception e) {
            System.out.println("Exception: "+e.getMessage());
        }
    }

    @Override
    public void printFlyingPdf(Document document,String shortId) {

        try {
            document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
            document.outputSettings().charset(StandardCharsets.UTF_8);
            document.charset(StandardCharsets.UTF_8);
            OutputStream os = new FileOutputStream(getPath(shortId));

            ITextRenderer renderer = new ITextRenderer();

            renderer.getFontResolver().addFont(new File(".").getCanonicalPath()+"\\fonts\\JosefinSans-Regular.ttf", BaseFont.EMBEDDED);

            renderer.setDocumentFromString(document.outerHtml());
            renderer.layout();
            renderer.createPDF(os,false);
            renderer.finishPDF();

            //openBrowser(shortId);

        }
        catch (Exception e) {
            System.out.println("Exception: "+e.getMessage());
        }
    }

    @Override
    public void openBrowser(String shortId) {
        try {
            String path = getPath(shortId);
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/C", "explorer "+path);
            processBuilder.start();
        }
        catch (Exception e) {
            System.out.println("Exception: "+e.getMessage());
        }
    }

    @Override
    public byte[] printPDF(String shortId) {
        try {
            FileInputStream fis = new FileInputStream(new File(getPath(shortId)));
            byte[] targetArray = new byte[fis.available()];
            fis.read(targetArray);
            return targetArray;
        }
        catch (Exception e) {
            System.out.println("Exception: "+e.getMessage());
        }
        return null;
    }

    public String getRandomIdKey() {
        return RandomStringUtils.random(8, randomIdKey);
    }
    public String getPath(String shortId) {
        try {
            return new File(".").getCanonicalPath() + Constants.folderReceiptPdfAddress2 + shortId + ".pdf";
        }
        catch (Exception e) {
            System.out.println("Exception: "+e.getMessage());
        }
        return null;
    }
}
