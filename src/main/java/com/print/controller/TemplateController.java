package com.print.controller;

import com.print.models.response.TemplateAllResponse;
import com.print.persistence.entity.TemplateTable;
import com.print.service.TemplateService;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/template")
public class TemplateController {
    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping
    private ResponseEntity<TemplateAllResponse> getAllTemplateInfo() {
        return ResponseEntity.ok(templateService.getAllTemplateInfo());
    }
    @DeleteMapping("/{templateId}")
    private ResponseEntity<String> deleteTemplateId(@PathVariable("templateId") Long templateId) {
        return ResponseEntity.ok(templateService.deleteTemplateId(templateId));
    }

    @GetMapping("/all")
    private ResponseEntity<List<TemplateTable>> getAllTemplate() {
        return ResponseEntity.ok(templateService.getAllTemplate());
    }
}
