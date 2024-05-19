package com.print.persistence.repository;

import com.print.persistence.entity.PrintLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrintLogRepository extends JpaRepository<PrintLog, Long> {
}
