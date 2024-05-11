package com.print.service.Impl;

import com.ironsoftware.ironpdf.PdfDocument;
import com.lowagie.text.pdf.BaseFont;
import com.print.common.Constants;
import com.print.enums.TemplateType;
import com.print.models.dto.GuestPdfDTO;
import com.print.models.dto.ReceiptDTO;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
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
    public GuestPdfDTO htmlEditData(ReceiptDTO receiptDTO){
        String shortId = null;

        if(templateRepository.existsByTemplateNameAndTemplateType(receiptDTO.getTemplateName().toLowerCase(),TemplateType.convert(receiptDTO.getTemplateType().toLowerCase()))) {
            String templateShortId = getBringSuitableTemplate(receiptDTO.getTemplateName().toLowerCase() , TemplateType.convert(receiptDTO.getTemplateType().toLowerCase()));
            try {
                String folderAddress =  getFolderAddress(receiptDTO.getTemplateType().toLowerCase());
                Resource template = resourceLoader.getResource(folderAddress + templateShortId + ".html");
                File file = new File(template.getURI());
                Document document = Jsoup.parse(file, "UTF-8");

                Class classObject = TemplateType.convertClass(receiptDTO.getTemplateType().toLowerCase());

                Class<?> clz = classObject;
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
                printFlyingPdf(document, shortId);
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }
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
    public String getFolderAddress(String templateType) {
        return switch (templateType) {
            case "receipt" ->  Constants.folderReceiptAddress;
            case "invoice" -> Constants.folderInvoiceAddress;
            default -> null;
        };
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

    public String getRandomIdKey() {
        return RandomStringUtils.random(8, randomIdKey);
    }
}
