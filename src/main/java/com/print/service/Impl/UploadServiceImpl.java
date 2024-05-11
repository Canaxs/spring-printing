package com.print.service.Impl;

import com.print.common.Constants;
import com.print.common.exception.UploadException;
import com.print.enums.TemplateType;
import com.print.models.dto.UploadDBDTO;
import com.print.persistence.entity.Receipt;
import com.print.persistence.entity.TemplateTable;
import com.print.persistence.repository.TemplateRepository;
import com.print.service.UploadService;
import org.apache.commons.lang3.RandomStringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
public class UploadServiceImpl implements UploadService {

    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${randomid}")
    private String randomIdKey;

    private final TemplateRepository templateRepository;

    public UploadServiceImpl(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @Override
    public String UploadFile(String fileType,String templateName,MultipartFile htmlFile, MultipartFile cssFile,String startDate,String endDate) {

        if(!validateFile(htmlFile,cssFile)) {
            if(fileControl(htmlFile,cssFile,Constants.fileTempName)) {
                String fileIdKey = getRandomIdKey();
                if(saveFile(fileType,templateName,htmlFile,cssFile,fileIdKey,startDate,endDate)) {
                    String returnString = " Your file has been uploaded successfully. FileIdKey: "+fileIdKey;
                    return cssFile.isEmpty() ? htmlFile.getOriginalFilename() + returnString
                            : htmlFile.getOriginalFilename() + " " + cssFile.getOriginalFilename() + returnString;
                }
            }
            else {
                return "The file does not match the principles of the application, please change it and try again";
            }
        }
        else {
            throw new UploadException("Check your file and upload it again");
        }
        return "Failed to load file";
    }

    @Override
    public boolean validateFile(MultipartFile htmlFile, MultipartFile cssFile) {
        String htmlFileText = Objects.requireNonNull(htmlFile.getOriginalFilename()).split(Pattern.quote("."))[1];
        String cssFileText = Objects.requireNonNull(cssFile.getOriginalFilename()).split(Pattern.quote("."))[1];
        if(Objects.equals(htmlFileText, "html")) {
            if(!cssFile.isEmpty() && !Objects.equals(cssFileText,"css")) {
                return true;
            }
            else {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean fileControl(MultipartFile htmlFile, MultipartFile cssFile,String fileType) {
        String fileIdKey = getRandomIdKey();
        saveFile(Constants.fileTempName,null,htmlFile,cssFile,fileIdKey,"","");
        boolean fileControlBool = true;

        //
        try {
            File file = new File(new File(".").getCanonicalPath()+Constants.folderTempUploadAddress+"html\\"+fileIdKey+".html");
            Document document = Jsoup.parse(file, "UTF-8");

            Class<Receipt> clz = Receipt.class;
            for (Method m : clz.getDeclaredMethods()) {
                for (Parameter p : m.getParameters()) {
                    System.out.println("Parameter: "+p.getName());
                    if(document.getElementById(p.getName()) == null && !Objects.equals(p.getName(), "other") && !Objects.equals(p.getName(), "o")) {
                        fileControlBool = true;
                    }
                }
            }
        }
        catch (Exception e) {
            throw new UploadException("The file does not match the principles of the application, please change it and try again: "+e.getMessage());
        }
        deleteFile(fileIdKey,"html",fileType);
        deleteFile(fileIdKey,"css",fileType);

        return fileControlBool;
    }

    @Override
    public boolean saveFile(String fileType, String templateName, MultipartFile htmlFile, MultipartFile cssFile
            ,String fileIdKey,String startDate, String endDate) {

        String fileTypeAddress = getFileTypeAddress(fileType.toLowerCase());

        try {
            File htmlIdKeyFile = new File(new File(".").getCanonicalPath()+ fileTypeAddress+"html\\"+fileIdKey+".html");
            File cssIdKeyFile = new File(new File(".").getCanonicalPath()+ fileTypeAddress+"css\\"+fileIdKey+".css");

            fileOutputWriting(htmlFile,htmlIdKeyFile);
            fileOutputWriting(cssFile,cssIdKeyFile);
        }
        catch (Exception e) {
            throw new UploadException("There was a problem saving the files, please try again: "+e.getMessage());
        }

        if(!fileType.equals("temp")) {
            UploadDBDTO uploadDBDTO = UploadDBDTO.builder()
                    .fileType(fileType)
                    .templateName(templateName)
                    .templateShortId(fileIdKey)
                    .effectiveStartDate(startDate)
                    .effectiveEndDate(endDate)
                    .isActive(true)
                    .build();
            saveDB(uploadDBDTO);
        }


        return true;
    }

    @Override
    public boolean saveDB(UploadDBDTO uploadDBDTO) {

        TemplateTable templateTable = new TemplateTable();
        templateTable.setTemplateName(uploadDBDTO.getTemplateName().toLowerCase());
        templateTable.setTemplateType(TemplateType.convert(uploadDBDTO.getFileType().toLowerCase()));
        templateTable.setTemplateShortId(uploadDBDTO.getTemplateShortId());
        templateTable.setEffectiveStartDate(dateTimePatternEdit(uploadDBDTO.getEffectiveStartDate()));
        templateTable.setEffectiveEndDate(dateTimePatternEdit(uploadDBDTO.getEffectiveEndDate()));
        templateTable.setIsActive(uploadDBDTO.getIsActive());
        try {
            templateRepository.save(templateTable);
            return true;
        }
        catch (Exception e) {
            throw new UploadException("There was a problem saving information: "+e.getMessage());
        }
    }

    @Override
    public boolean deleteFile(String randomIdKey,String fileExtension,String fileType) {

        String fileTypeAddress = switch (fileType.toLowerCase()) {
            case "receipt" ->  Constants.folderReceiptUploadAddress;
            case "invoice" -> Constants.folderInvoiceUploadAddress;
            case "temp" -> Constants.folderTempUploadAddress;
            default -> null;
        };
        try {
            String filePath = new File(".").getCanonicalPath() + fileTypeAddress + fileExtension +"\\" + randomIdKey + "."+fileExtension;
            File myObject = new File(filePath);
            if(myObject.exists()) {
                myObject.delete();
                return true;
            }
        }
        catch (Exception e) {
            throw new UploadException("Error occurred while deleting file: "+e.getMessage());
        }
        return false;
    }

    @Override
    public Date dateTimePatternEdit(String effectiveDate) {
        Date date = null;
        try {
            if(effectiveDate != null && !effectiveDate.isEmpty()) {
                date = new SimpleDateFormat(Constants.dateTimeFormatPattern).parse(effectiveDate);
            }
        }
        catch (Exception e) {
            throw new UploadException("There was an error creating the date: "+e.getMessage());
        }
        return date;
    }

    @Override
    public void fileOutputWriting(MultipartFile mpFile, File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(mpFile.getBytes());
            fos.close();
        }
        catch (Exception e) {
            throw new UploadException("Write Error: "+e.getMessage());
        }
    }

    public String getRandomIdKey() {
        return RandomStringUtils.random(8, randomIdKey);
    }

    public String getFileTypeAddress(String fileType) {
        return switch (fileType.toLowerCase()) {
            case "receipt" ->  Constants.folderReceiptUploadAddress;
            case "invoice" -> Constants.folderInvoiceUploadAddress;
            case "temp" -> Constants.folderTempUploadAddress;
            default -> null;
        };
    }

}
