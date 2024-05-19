package com.print.service.Impl;

import com.print.common.exception.LogServiceException;
import com.print.enums.TemplateType;
import com.print.persistence.entity.PrintLog;
import com.print.persistence.repository.PrintLogRepository;
import com.print.service.PrintLogService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PrintLogServiceImpl implements PrintLogService {

    private final PrintLogRepository printLogRepository;

    public PrintLogServiceImpl(PrintLogRepository printLogRepository) {
        this.printLogRepository = printLogRepository;
    }

    @Override
    public void createLog(String templateName, String templateType) {
        PrintLog printLog = new PrintLog();
        printLog.setTemplateName(templateName);
        printLog.setTemplateType(TemplateType.convert(templateType.toLowerCase()));
        Date date = new Date();
        printLog.setDateOfIssue(date);
        try {
            printLogRepository.save(printLog);
        }
        catch (Exception e) {
            throw new LogServiceException("An error was encountered while creating the log");
        }
    }
}
