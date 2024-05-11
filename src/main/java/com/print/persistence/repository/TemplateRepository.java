package com.print.persistence.repository;

import com.print.enums.TemplateType;
import com.print.persistence.entity.TemplateTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateRepository extends JpaRepository<TemplateTable, Long> {

    Boolean existsByTemplateNameAndTemplateType(String templateName,TemplateType templateType);

    List<TemplateTable> getTemplateTableByTemplateNameAndTemplateType (String templateName,TemplateType templateType);


    TemplateTable getByTemplateShortId(String templateShortId);
}
