package com.print.service.Impl;

import com.ironsoftware.ironpdf.PdfDocument;
import com.lowagie.text.pdf.BaseFont;
import com.print.common.Constants;
import com.print.common.exception.TemplateException;
import com.print.enums.TemplateType;
import com.print.models.dto.GuestPdfDTO;
import com.print.models.dto.InvoiceDTO;
import com.print.models.dto.ReceiptDTO;
import com.print.models.request.CreatedPdfRequest;
import com.print.persistence.entity.InvoiceProduct;
import com.print.persistence.entity.Receipt;
import com.print.persistence.entity.TemplateTable;
import com.print.persistence.repository.TemplateRepository;
import com.print.service.TemplateService;
import org.apache.commons.lang3.RandomStringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class TemplateServiceImpl implements TemplateService {


    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${randomid}")
    private String randomIdKey;

    private final TemplateRepository templateRepository;

    public TemplateServiceImpl(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @Override
    public GuestPdfDTO htmlEditDataReceipt(ReceiptDTO receiptDTO){
        String shortId = null;

        if(templateRepository.existsByTemplateNameAndTemplateType(receiptDTO.getTemplateName().toLowerCase(),TemplateType.RECEIPT)) {
            String templateShortId = getBringSuitableTemplate(receiptDTO.getTemplateName().toLowerCase() , TemplateType.RECEIPT);
            try {
                Resource template = resourceLoader.getResource(Constants.folderReceiptAddress + templateShortId + ".html");
                File file = new File(template.getURI());
                Document document = Jsoup.parse(file, "UTF-8");

                Class<?> clz = TemplateType.convertClass(TemplateType.convertString(TemplateType.RECEIPT));

                for (Method m : clz.getDeclaredMethods()) {
                    for (Parameter p : m.getParameters()) {
                        if (document.getElementById(p.getName()) != null && !Objects.equals(p.getName(), "other") && !Objects.equals(p.getName(), "o")) {
                            Element div = document.getElementById(p.getName());
                            div.html(receiptDTO.convert(p.getName()).toString());
                        }
                    }
                }
                shortId = getRandomIdKey();
                //printIronPdf(document);
                printFlyingPdf(document, shortId,"receipt");
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }
        }
        return GuestPdfDTO.builder()
                .filename(getPath(shortId,"receipt"))
                .array(printPDF(shortId,"receipt")).
                build();
    }

    @Override
    public GuestPdfDTO htmlEditDataInvoice(InvoiceDTO invoiceDTO){
        String shortId = null;

        if(templateRepository.existsByTemplateNameAndTemplateType(invoiceDTO.getTemplateName().toLowerCase(),TemplateType.INVOICE)) {
            String templateShortId = getBringSuitableTemplate(invoiceDTO.getTemplateName().toLowerCase() , TemplateType.INVOICE);
            try {
                Resource template = resourceLoader.getResource(Constants.folderInvoiceAddress + templateShortId + ".html");
                File file = new File(template.getURI());
                Document document = Jsoup.parse(file, "UTF-8");

                Class<?> clz = TemplateType.convertClass(TemplateType.convertString(TemplateType.INVOICE));

                for (Method m : clz.getDeclaredMethods()) {
                    for (Parameter p : m.getParameters()) {
                        if (document.getElementById(p.getName()) != null && !Objects.equals(p.getName(), "other") && !Objects.equals(p.getName(), "o")) {
                            if(Objects.equals(p.getName(), "products")) {
                                int index = 1;
                                for(InvoiceProduct invoiceProduct : invoiceDTO.getProducts()) {
                                    Element div = document.getElementById(p.getName());
                                    div.append(tableProducts(invoiceProduct,index));
                                    index++;
                                }
                            }
                            else {
                                Element div = document.getElementById(p.getName());
                                div.html(invoiceDTO.convert(p.getName()).toString());
                            }
                        }
                    }
                }
                shortId = getRandomIdKey();
                //printIronPdf(document,"invoice",shortId);
                printFlyingPdf(document, shortId,"invoice");
            } catch (Exception e) {
                throw new TemplateException("Exception: "+e.getMessage());
            }
        }
        return GuestPdfDTO.builder()
                .filename(getPath(shortId,"invoice"))
                .array(printPDF(shortId,"invoice")).
                build();
    }

    @Override
    public GuestPdfDTO getCreatedPdf(CreatedPdfRequest createdPdfRequest) {
        ReceiptDTO receiptDTO;
        try {
            switch (createdPdfRequest.getTemplateType().toLowerCase()) {
                case "receipt" : {

                }
            }
        }
        catch (Exception e) {
            throw new TemplateException("An error occurred: "+e.getMessage());
        }
        return GuestPdfDTO.builder().build();
    }

    @Override
    public void printIronPdf(Document document,String templateType,String shortId) {
        try {
            PdfDocument myPdf = PdfDocument.renderHtmlAsPdf(document.outerHtml());

            //Save the PdfDocument to a file
            myPdf.saveAs(Path.of(Constants.folderReceiptPdfAddress,shortId + ".pdf"));

            openBrowser(shortId,templateType);
        }
        catch (Exception e) {
            throw new TemplateException("Exception: "+e.getMessage());
        }
    }

    @Override
    public void printFlyingPdf(Document document,String shortId,String templateType) {

        try {
            document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
            document.outputSettings().charset(StandardCharsets.UTF_8);
            document.charset(StandardCharsets.UTF_8);
            OutputStream os = new FileOutputStream(getPath(shortId,templateType));

            ITextRenderer renderer = new ITextRenderer();

            renderer.getFontResolver().addFont(new File(".").getCanonicalPath()+"\\fonts\\JosefinSans-Regular.ttf", BaseFont.EMBEDDED);

            renderer.setDocumentFromString(document.outerHtml());
            renderer.layout();
            renderer.createPDF(os,false);
            renderer.finishPDF();

            //openBrowser(shortId);

        }
        catch (Exception e) {
            throw new TemplateException("Exception: "+e.getMessage());
        }
    }

    @Override
    public void openBrowser(String shortId,String templateType) {
        try {
            String path = getPath(shortId,templateType);
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/C", "explorer "+path);
            processBuilder.start();
        }
        catch (Exception e) {
            throw new TemplateException("Exception: "+e.getMessage());
        }
    }

    @Override
    public byte[] printPDF(String shortId,String templateType) {
        try {
            FileInputStream fis = new FileInputStream(new File(getPath(shortId,templateType)));
            byte[] targetArray = new byte[fis.available()];
            fis.read(targetArray);
            return targetArray;
        }
        catch (Exception e) {
            throw new TemplateException("Exception: "+e.getMessage());
        }
    }

    @Override
    public String getBringSuitableTemplate(String templateName, TemplateType templateType) {
        String returnTemplateShortId = null;
        List<TemplateTable> templateTables = templateRepository.getTemplateTableByTemplateNameAndTemplateType(templateName,templateType);
        Date todayDate = new Date();
        for (TemplateTable templateTable : templateTables) {
            if(templateTable.getEffectiveStartDate() != null && templateTable.getEffectiveEndDate() != null) {
                if(todayDate.compareTo(templateTable.getEffectiveStartDate()) > 0 && templateTable.getEffectiveEndDate().compareTo(todayDate) > 0) {
                    return templateTable.getTemplateShortId();
                }
            }
            else if(templateTable.getEffectiveEndDate() != null) {
                if(returnTemplateShortId != null) {
                    TemplateTable templateTableOnce = templateRepository.getByTemplateShortId(returnTemplateShortId);
                    if(templateTableOnce.getEffectiveEndDate() == null && templateTable.getEffectiveEndDate().compareTo(todayDate) > 0 ) {
                        returnTemplateShortId = templateTable.getTemplateShortId();
                    }
                    else if (templateTableOnce.getEffectiveEndDate() != null
                            && templateTableOnce.getEffectiveEndDate().compareTo(templateTable.getEffectiveEndDate()) > 0
                            && templateTable.getEffectiveEndDate().compareTo(todayDate) > 0 ) {
                        returnTemplateShortId = templateTable.getTemplateShortId();
                    }
                }
                else {
                    if(templateTable.getEffectiveEndDate().compareTo(todayDate) > 0) {
                        returnTemplateShortId = templateTable.getTemplateShortId();
                    }
                }
            }
            else if (templateTable.getEffectiveStartDate() != null) {
                if(returnTemplateShortId != null) {
                    TemplateTable templateTableOnce = templateRepository.getByTemplateShortId(returnTemplateShortId);
                    if(templateTableOnce.getEffectiveEndDate() == null
                            && templateTableOnce.getEffectiveStartDate() != null
                            && templateTable.getEffectiveStartDate().compareTo(templateTableOnce.getEffectiveStartDate()) > 0
                            && todayDate.compareTo(templateTable.getEffectiveStartDate()) > 0) {
                        returnTemplateShortId = templateTable.getTemplateShortId();
                    }
                    else if (templateTableOnce.getEffectiveEndDate() == null
                            && templateTableOnce.getEffectiveStartDate() == null
                            && todayDate.compareTo(templateTable.getEffectiveStartDate()) > 0) {
                        returnTemplateShortId = templateTable.getTemplateShortId();
                    }
                }
                else {
                    if(todayDate.compareTo(templateTable.getEffectiveStartDate()) > 0) {
                        returnTemplateShortId = templateTable.getTemplateShortId();
                    }
                }
            }
            else {
                if(returnTemplateShortId == null) {
                    returnTemplateShortId = templateTable.getTemplateShortId();
                }
            }
        }

        return returnTemplateShortId;
    }

    public String getPath(String shortId,String templateType) {
        try {
            if(Objects.equals(templateType, "receipt")) {
                return new File(".").getCanonicalPath() + Constants.folderReceiptPdfAddress2 + shortId + ".pdf";
            }
            else {
                return new File(".").getCanonicalPath() + Constants.folderInvoicePdfAddress2 + shortId + ".pdf";
            }
        }
        catch (Exception e) {
            throw new TemplateException("Exception: "+e.getMessage());
        }
    }

    public String getRandomIdKey() {
        return RandomStringUtils.random(8, randomIdKey);
    }

    public String tableProducts(InvoiceProduct invoiceProduct,Integer productNo) {
        return "<tr>" +
                "<td>" +productNo.toString()+"</td>"+
                "<td>" +invoiceProduct.getName()+"</td>"+
                "<td>" +invoiceProduct.getAmount()+"</td>"+
                "<td>" +invoiceProduct.getUnitPrice()+"</td>"+
                "<td>"+Constants.KDVAmount.toString()+"%"+"</td>"+
                "<td>"+KDVCalculation(invoiceProduct.getUnitPrice())+"</td>"+
                "<td>"+KDVTotal(KDVCalculation(invoiceProduct.getUnitPrice()),invoiceProduct.getUnitPrice())+"</td>"+
                "</tr>";
    }
    public Double KDVCalculation(Double unitPrice) {
        return (unitPrice /100) * Constants.KDVAmount;
    }
    public Double KDVTotal(Double unitPrice,Double KDVCalc) {
        return unitPrice+KDVCalc;
    }
}
