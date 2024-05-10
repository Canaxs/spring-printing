package com.print.persistence.repository;

import com.print.persistence.entity.TemplateTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateRepository extends JpaRepository<TemplateTable, Long> {

    boolean existsByTemplateName(String templateName);

    List<TemplateTable> getTemplateTableByTemplateName (String templateName);

    TemplateTable getByTemplateShortId(String templateShortId);
}
