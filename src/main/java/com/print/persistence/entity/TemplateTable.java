package com.print.persistence.entity;

import com.print.enums.TemplateType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;


@Data
@Entity
@Table
public class TemplateTable {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "id_generator")
    @TableGenerator(name = "id_generator", table = "id_gen", pkColumnName = "gen_name", valueColumnName = "gen_value",
            pkColumnValue="task_gen", initialValue=1000, allocationSize=10)
    private Long Id;

    private String templateName;

    @Enumerated(EnumType.STRING)
    private TemplateType templateType;

    private Date effectiveStartDate;

    private Date effectiveEndDate;
}
