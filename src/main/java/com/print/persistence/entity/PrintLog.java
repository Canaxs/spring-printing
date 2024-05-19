package com.print.persistence.entity;

import com.print.enums.TemplateType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import jakarta.persistence.*;

import java.util.Date;

@Data
@Entity
@Table
public class PrintLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String templateName;

    @Enumerated(EnumType.STRING)
    private TemplateType templateType;

    private Date dateOfIssue;


}
