package com.print.service.Impl;

import com.ironsoftware.ironpdf.License;
import com.ironsoftware.ironpdf.PdfDocument;
import com.ironsoftware.ironpdf.edit.PageSelection;
import com.ironsoftware.ironpdf.image.DrawImageOptions;
import com.ironsoftware.ironpdf.render.ChromePdfRenderOptions;
import com.ironsoftware.ironpdf.render.CssMediaType;
import com.ironsoftware.ironpdf.render.PaperOrientation;
import com.ironsoftware.ironpdf.render.PaperSize;
import com.lowagie.text.pdf.BaseFont;
import com.print.common.Constants;
import com.print.common.exception.TemplateException;
import com.print.common.exception.UploadException;
import com.print.enums.TemplateType;
import com.print.models.dto.GuestPdfDTO;
import com.print.models.dto.InvoiceDTO;
import com.print.models.dto.ReceiptDTO;
import com.print.models.request.CreatedPdfRequest;
import com.print.models.request.TempUpdateRequest;
import com.print.models.response.ImageResponse;
import com.print.models.response.TemplateAllResponse;
import com.print.persistence.entity.InvoiceProduct;
import com.print.persistence.entity.Receipt;
import com.print.persistence.entity.TemplateTable;
import com.print.persistence.repository.TemplateRepository;
import com.print.service.PrintLogService;
import com.print.service.TemplateService;
import org.apache.commons.io.FileUtils;
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
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TemplateServiceImpl implements TemplateService {


    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${randomid}")
    private String randomIdKey;

    @Value("${ironpdfKEY}")
    private String ironPdfKey;

    private final TemplateRepository templateRepository;

    private final PrintLogService printLogService;

    public TemplateServiceImpl(TemplateRepository templateRepository, PrintLogService printLogService) {
        this.templateRepository = templateRepository;
        this.printLogService = printLogService;
    }

    @Override
    public GuestPdfDTO htmlEditDataReceipt(ReceiptDTO receiptDTO){
        String shortId = null;
        printLogService.createLog(receiptDTO.getTemplateName(),"receipt");

        if(templateRepository.existsByTemplateNameAndTemplateType(receiptDTO.getTemplateName().toLowerCase(),TemplateType.RECEIPT)) {
            String templateShortId = getBringSuitableTemplate(receiptDTO.getTemplateName().toLowerCase() , TemplateType.RECEIPT);
            try {
                File file = new File(getHtmlCanonicalPath(templateShortId,TemplateType.RECEIPT));
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
                timeHtml(document);

                shortId = getRandomIdKey();
                printIronPdf(document,"receipt",shortId);
                //printFlyingPdf(document, shortId,"receipt");
            } catch (Exception e) {
                System.out.println("Exception:e " + e.getMessage());
            }
        }
        GuestPdfDTO guestPdfDTO = GuestPdfDTO.builder()
                .filename(getPath(shortId,"receipt"))
                .array(printPDF(shortId,"receipt")).
                build();
        deletePDFFile("receipt",shortId);
        return guestPdfDTO;
    }

    @Override
    public GuestPdfDTO htmlEditDataInvoice(InvoiceDTO invoiceDTO){
        String shortId = null;
        printLogService.createLog(invoiceDTO.getTemplateName(),"invoice");

        if(templateRepository.existsByTemplateNameAndTemplateType(invoiceDTO.getTemplateName().toLowerCase(),TemplateType.INVOICE)) {
            String templateShortId = getBringSuitableTemplate(invoiceDTO.getTemplateName().toLowerCase() , TemplateType.INVOICE);
            try {
                File file = new File(getHtmlCanonicalPath(templateShortId,TemplateType.INVOICE));
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
                                amountsHtml(document,invoiceDTO.getProducts());
                            }
                            else {
                                Element div = document.getElementById(p.getName());
                                div.html(invoiceDTO.convert(p.getName()).toString());
                            }
                        }
                    }
                }
                timeHtml(document);

                shortId = getRandomIdKey();
                printIronPdf(document,"invoice",shortId);
                //printFlyingPdf(document, shortId,"invoice");
            } catch (Exception e) {
                throw new TemplateException("Exception: "+e.getMessage());
            }
        }
        GuestPdfDTO guestPdfDTO = GuestPdfDTO.builder()
                .filename(getPath(shortId,"invoice"))
                .array(printPDF(shortId,"invoice")).
                build();
        deletePDFFile("invoice",shortId);
        return guestPdfDTO;
    }

    @Override
    public void printIronPdf(Document document,String templateType,String shortId) {
        try {
            System.out.println(License.getLicenseKey());
            ChromePdfRenderOptions chromePdfRenderOptions = getChromePdfRenderOptions();

            PdfDocument myPdf = PdfDocument.renderHtmlAsPdf(document.outerHtml(),chromePdfRenderOptions);

            //Save the PdfDocument to a file
            myPdf.saveAs(Path.of(TemplateType.convertPathOf(templateType),shortId + ".pdf"));

            //openBrowser(shortId,templateType);
        }
        catch (Exception e) {
            throw new TemplateException("Exception: "+e.getMessage());
        }
    }

    private static ChromePdfRenderOptions getChromePdfRenderOptions() {
        ChromePdfRenderOptions chromePdfRenderOptions = new ChromePdfRenderOptions();
        chromePdfRenderOptions.setMarginTop(0);
        chromePdfRenderOptions.setMarginLeft(0);
        chromePdfRenderOptions.setMarginRight(0);
        chromePdfRenderOptions.setMarginBottom(0);
        chromePdfRenderOptions.setPaperSize(PaperSize.A4);
        chromePdfRenderOptions.setPrintHtmlBackgrounds(true);
        chromePdfRenderOptions.setPaperOrientation(PaperOrientation.PORTRAIT);
        chromePdfRenderOptions.setCssMediaType(CssMediaType.PRINT);
        return chromePdfRenderOptions;
    }

    @Override
    public void printFlyingPdf(Document document,String shortId,String templateType) {

        try {
            final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            builder.setEntityResolver(new EntityResolver() {
                @Override
                public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                    if (systemId.contains("xhtml1-transitional.dtd")) {
                        return new InputSource(new FileReader(getRealPath() + "/WEB-INF/dtd/xhtml1-transitional.dtd"));
                    } else if (systemId.contains("xhtml-lat1.ent")) {
                        return new InputSource(new FileReader(getRealPath() + "/WEB-INF/dtd/xhtml-lat1.ent"));
                    } else if (systemId.contains("xhtml-symbol.ent")) {
                        return new InputSource(new FileReader(getRealPath() + "/WEB-INF/dtd/xhtml-symbol.ent"));
                    } else if (systemId.contains("xhtml-special.ent")) {
                        return new InputSource(new FileReader(getRealPath() + "/WEB-INF/dtd/xhtml-special.ent"));
                    } else {
                        return null;
                    }
                }
            });
            document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
            //document.outputSettings().charset(StandardCharsets.UTF_8);
            //document.charset(StandardCharsets.UTF_8);
            OutputStream os = new FileOutputStream(getPath(shortId,templateType));

            ITextRenderer renderer = new ITextRenderer(26f * 4f / 3f, 26);


            renderer.getFontResolver().addFont(new File(".").getCanonicalPath()+"\\fonts\\OpenSans-Light.ttf",BaseFont.IDENTITY_H ,BaseFont.NOT_EMBEDDED);

            final ByteArrayInputStream inputStream = new ByteArrayInputStream(document.outerHtml().getBytes(StandardCharsets.UTF_8));
            final org.w3c.dom.Document doc = builder.parse(inputStream);
            inputStream.close();
            renderer.setDocument(doc,null);
            renderer.layout();
            renderer.createPDF(os,false);
            renderer.finishPDF();

            os.close();

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
            fis.close();
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

    @Override
    public List<ImageResponse> getImagesTemplateType(String templateType) {
        List<ImageResponse> imageResponses = new ArrayList<>();
        List<String> templateNames = templateRepository.bringNamesOfTheSameTypeAlone(TemplateType.convert(templateType.toLowerCase()));

        try {
            for (String templateName : templateNames) {
                ImageResponse imageResponse = new ImageResponse();
                String suitableTemplate = getBringSuitableTemplate(templateName, TemplateType.convert(templateType.toLowerCase()));
                File file = new File(new File(".").getCanonicalPath() + TemplateType.convertImagePath(templateType.toLowerCase()) + suitableTemplate + ".png");
                imageResponse.setImageByte(FileUtils.readFileToByteArray(file));
                imageResponse.setTemplateName(templateName);
                imageResponse.setShortId(suitableTemplate);
                imageResponses.add(imageResponse);
            }
        }
        catch (Exception e) {
            throw new TemplateException("An error occurred in the images template type service: "+e.getMessage());
        }
        return imageResponses;
    }

    @Override
    public TemplateAllResponse getAllTemplateInfo() {
        return TemplateAllResponse.builder()
                .allTemplateNumber(templateRepository.getAllTemplateNumber())
                .allReceiptNumber(templateRepository.getAllTemplateTypeNumber(TemplateType.RECEIPT))
                .allInvoiceNumber(templateRepository.getAllTemplateTypeNumber(TemplateType.INVOICE))
                .allExpiredTemplateNumber(templateRepository.getAllExpiredTemplateNumber(new Date()))
                .build();
    }

    @Override
    public String deleteTemplateId(Long templateId) {
        try {
            TemplateTable templateTable = templateRepository.getReferenceById(templateId);
            templateRepository.delete(templateTable);
        }
        catch (Exception e) {
            throw new TemplateException("An error occurred while deleting the template");
        }
        return "Successfully Deleted: "+templateId;
    }

    @Override
    public List<TemplateTable> getAllTemplate() {
        return templateRepository.findAll();
    }

    @Override
    public TemplateTable updateTemplate(TempUpdateRequest tempUpdateRequest) {
        try {
            TemplateTable templateTable = templateRepository.getReferenceById(tempUpdateRequest.getId());
            templateTable.setTemplateName(tempUpdateRequest.getTemplateName());
            templateTable.setIsActive(tempUpdateRequest.getIsActive());
            templateTable.setEffectiveStartDate(tempUpdateRequest.getEffectiveStartDate());
            templateTable.setEffectiveEndDate(tempUpdateRequest.getEffectiveEndDate());
            return templateRepository.save(templateTable);
        }
        catch (Exception e) {
            throw new TemplateException("An error occurred while updating the template");
        }
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
                "<td>"+serviceAmount(invoiceProduct.getUnitPrice(),invoiceProduct.getAmount())+"</td>"+
                "</tr>";
    }
    public Double KDVCalculation(Double unitPrice) {
        return (unitPrice /100) * Constants.KDVAmount;
    }
    public Double KDVTotal(Double unitPrice,Double KDVCalc,Integer amount) {
        return (unitPrice+KDVCalc) * amount;
    }
    public Double serviceAmount(Double num1,Integer num2) {
        return num1 * num2;
    }

    public String getRealPath() throws IOException {
        return new File(".").getCanonicalPath();
    }
    public void timeHtml(Document document) {
        Date date = new Date();
        if(document.getElementById("createdDate") != null) {
            SimpleDateFormat formatter = new SimpleDateFormat(Constants.dateTimeFormatPattern);
            Element div = document.getElementById("createdDate");
            div.html(formatter.format(date));
        }
        if(document.getElementById("valor") != null) {
            SimpleDateFormat formatter = new SimpleDateFormat(Constants.dateTimeFormatPatternValor);
            Element div = document.getElementById("valor");
            div.html(formatter.format(date));
        }
        if(document.getElementById("invoiceTime") != null) {
            SimpleDateFormat formatter = new SimpleDateFormat(Constants.dateTimeFormatPatternTime);
            Element div = document.getElementById("invoiceTime");
            div.html(formatter.format(date));
        }
    }

    public void amountsHtml(Document document, List<InvoiceProduct> products) {
        Double serviceAmount = 0.0;
        Double calculateKDV = 0.0;
        Double amountTaxes = 0.0;

        document.getElementById("vatRate").append("(%)"+Constants.KDVAmount.toString());

        for(InvoiceProduct product : products) {
            serviceAmount = serviceAmount + serviceAmount(product.getUnitPrice(),product.getAmount());
            calculateKDV = calculateKDV + KDVCalculation(product.getUnitPrice() * product.getAmount());
            amountTaxes = amountTaxes + ( (KDVCalculation(product.getUnitPrice()) + product.getUnitPrice()) * product.getAmount());
        }

        if(document.getElementById("serviceAmount") != null) {
            Element div = document.getElementById("serviceAmount");
            div.html(serviceAmount.toString());
        }
        if(document.getElementById("calculateKDV") != null) {
            Element div = document.getElementById("calculateKDV");
            div.html(calculateKDV.toString());
        }
        if(document.getElementById("amountTaxes") != null) {
            Element div = document.getElementById("amountTaxes");
            div.html(amountTaxes.toString());
        }
        if(document.getElementById("amountPaid") != null) {
            Element div = document.getElementById("amountPaid");
            div.html(amountTaxes.toString());
        }
    }

    public String getHtmlCanonicalPath(String templateShortId,TemplateType templateType) {
        String folderAddress = templateType == TemplateType.INVOICE ? Constants.folderInvoiceAddress2 : Constants.folderReceiptAddress2;
        try {
            return new File(".").getCanonicalPath() + folderAddress + "html\\" + templateShortId + ".html";
        }
        catch (Exception e) {
            throw new TemplateException("getHtmlCanonicalPath Error: "+e.getMessage());
        }
    }

    public void deletePDFFile(String fileType,String shortId) {
        String fileTypeAddress = TemplateType.convertPdf2(fileType.toLowerCase());
        try {
            String filePath = new File(".").getCanonicalPath() + fileTypeAddress + shortId + ".pdf";
            File myObject = new File(filePath);
            if(myObject.exists()) {
                Files.delete(myObject.toPath());
            }
        }
        catch (Exception e) {
            throw new UploadException("Error occurred while deleting file: "+e.getMessage());
        }
    }
}
