package com.print.persistence.repository;

import com.print.enums.TemplateType;
import com.print.persistence.entity.TemplateTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TemplateRepository extends JpaRepository<TemplateTable, Long> {

    Boolean existsByTemplateNameAndTemplateType(String templateName,TemplateType templateType);

    List<TemplateTable> getTemplateTableByTemplateNameAndTemplateType (String templateName,TemplateType templateType);

    TemplateTable getByTemplateShortId(String templateShortId);

    @Query("SELECT t.templateName FROM TemplateTable t WHERE t.templateType = :templateType GROUP BY t.templateName")
    List<String> bringNamesOfTheSameTypeAlone(TemplateType templateType);

    @Query("SELECT COUNT(*) FROM TemplateTable t")
    Integer getAllTemplateNumber();

    @Query("SELECT COUNT(*) FROM TemplateTable t WHERE t.templateType = :templateType")
    Integer getAllTemplateTypeNumber(TemplateType templateType);

    @Query("SELECT COUNT(*) FROM TemplateTable t WHERE t.effectiveEndDate <= :date")
    Integer getAllExpiredTemplateNumber(Date date);

}
