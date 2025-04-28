package com.test.repository;

import com.test.model.PurchaseOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DetailRepository extends JpaRepository<PurchaseOrderDetail, Long> {
    List<PurchaseOrderDetail> findByPurchaseOrderHeader_PoNumber(String poNumber);
}
