package com.test.repository;

import com.test.model.PurchaseOrderHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HeaderRepository extends JpaRepository<PurchaseOrderHeader, String> {
    @Query("SELECT h FROM PurchaseOrderHeader h LEFT JOIN FETCH h.details WHERE h.poNumber = :poNumber")
    PurchaseOrderHeader findByPoNumberWithDetails(@Param("poNumber") String poNumber);
}
