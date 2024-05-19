package com.print.service;

import com.print.models.dto.UploadDBDTO;
import com.print.models.request.FileRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public interface UploadService {

    String UploadFile(String fileType,String templateName,MultipartFile htmlFile, MultipartFile cssFile,String startDate,String endDate);
    boolean validateFile(MultipartFile htmlFile,MultipartFile cssFile);
    boolean fileControl(MultipartFile htmlFile, MultipartFile cssFile,String fileType);
    boolean saveFile(String fileType,String templateName,MultipartFile htmlFile, MultipartFile cssFile,String fileIdKey,String startDate,String endDate);
    void saveImage(String shortId,String templateType);
    boolean saveDB(UploadDBDTO uploadDBDTO);
    boolean deleteFile(String fileIdKey,String fileExtension,String fileType);
    Date dateTimePatternEdit(String effectiveDate);
    void fileOutputWriting(MultipartFile mpFile, File file);
}
