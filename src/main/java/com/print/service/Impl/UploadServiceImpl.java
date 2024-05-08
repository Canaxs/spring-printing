package com.print.service.Impl;

import com.print.common.Constants;
import com.print.common.exception.UploadException;
import com.print.enums.TemplateType;
import com.print.models.dto.ReceiptDTO;
import com.print.models.dto.UploadDBDTO;
import com.print.models.request.FileRequest;
import com.print.persistence.entity.Receipt;
import com.print.persistence.entity.TemplateTable;
import com.print.persistence.repository.TemplateRepository;
import com.print.service.UploadService;
import jakarta.servlet.ServletContext;
import org.apache.commons.lang3.RandomStringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
public class UploadServiceImpl implements UploadService {

    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${randomid}")
    private String randomIdKey;

    private TemplateRepository templateRepository;

    public UploadServiceImpl(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @Override
    public String UploadFile(String fileType,String templateName,MultipartFile htmlFile, MultipartFile cssFile) {

        if(!validateFile(htmlFile,cssFile)) {
            if(fileControl(htmlFile,cssFile)) {
                String fileIdKey = getRandomIdKey();
                if(saveFile(fileType,templateName,htmlFile,cssFile,fileIdKey)) {
                    String returnString = " Your file has been uploaded successfully";
                    return cssFile.isEmpty() ? htmlFile.getOriginalFilename() + returnString
                            : htmlFile.getOriginalFilename() + " " + cssFile.getOriginalFilename() + returnString;
                }
            }
        }
        else {
            throw new UploadException("Check your file and upload it again");
        }
        return null;
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
    public boolean fileControl(MultipartFile htmlFile, MultipartFile cssFile) {
        String fileIdKey = getRandomIdKey();
        saveFile(Constants.fileTempName,null,htmlFile,cssFile,fileIdKey);

        //
        try {
            Resource template = resourceLoader.getResource(Constants.folderTempClasspathAddress + fileIdKey + ".html");
            File file = new File(template.getURI());
            Document document = Jsoup.parse(file, "UTF-8");

            Class<Receipt> clz = Receipt.class;
            for (Method m : clz.getDeclaredMethods()) {
                for (Parameter p : m.getParameters()) {
                    if(document.getElementById(p.getName()) == null) {
                        return false;
                    }
                }
            }
        }
        catch (Exception e) {
            throw new UploadException("The file does not match the principles of the application, please change it and try again: "+e.getMessage());
        }

        return true;
    }

    @Override
    public boolean saveFile(String fileType, String templateName, MultipartFile htmlFile, MultipartFile cssFile,String fileIdKey) {

        String fileTypeAddress = switch (fileType.toLowerCase()) {
            case "receipt" ->  Constants.folderReceiptUploadAddress;
            case "invoice" -> Constants.folderInvoiceUploadAddress;
            case "temp" -> Constants.folderTempUploadAddress;
            default -> null;
        };
        try {
           // htmlFile.getOriginalFilename().replace(htmlFile.getOriginalFilename(),fileIdKey+".html");
           // cssFile.getOriginalFilename().replace(cssFile.getOriginalFilename(), fileIdKey+".css");

            htmlFile.transferTo(new File(new File(".").getCanonicalPath()+ fileTypeAddress+"html\\"+fileIdKey+".html"));

            cssFile.transferTo(new File(new File(".").getCanonicalPath()+ fileTypeAddress+"css\\"+fileIdKey+".css"));
        }
        catch (Exception e) {
            throw new UploadException("There was a problem saving the files, please try again: "+e.getMessage());
        }

        if(!fileType.equals("temp")) {
            UploadDBDTO uploadDBDTO = UploadDBDTO.builder()
                    .fileType(fileType)
                    .templateName(templateName)
                    .templateShortId(fileIdKey)
                    .build();
            saveDB(uploadDBDTO);
        }


        return true;
    }

    @Override
    public boolean saveDB(UploadDBDTO uploadDBDTO) {
        TemplateTable templateTable = new TemplateTable();
        templateTable.setTemplateType(TemplateType.convert(uploadDBDTO.getFileType().toLowerCase()));
        templateTable.setTemplateShortId(uploadDBDTO.getTemplateShortId());
        templateTable.setTemplateName(uploadDBDTO.getTemplateName().toUpperCase());
        templateTable.setEffectiveStartDate(null);
        templateTable.setEffectiveEndDate(null);
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

    public String getRandomIdKey() {
        return RandomStringUtils.random(8, randomIdKey);
    }
}
