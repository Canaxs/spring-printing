package com.print.controller;

import com.print.models.request.FileRequest;
import com.print.service.UploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
public class UploadController {

    private UploadService uploadService;

    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping
    public ResponseEntity<String> fileUploading(@RequestParam("fileType") String fileType,
                                                @RequestParam("templateName") String templateName,
                                                @RequestParam("html") MultipartFile htmlFile,
                                                @RequestParam(value = "css",required = false) MultipartFile cssFile) {
        return ResponseEntity.ok(uploadService.UploadFile(fileType,templateName,htmlFile,cssFile));
    }
}
