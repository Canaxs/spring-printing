package com.print.persistence.repository;

import com.print.persistence.entity.TemplateTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateRepository extends JpaRepository<TemplateTable, Long> {
}
