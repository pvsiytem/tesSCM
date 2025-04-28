package com.test.model;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tm_header")
public class PurchaseOrderHeader {

    @Id
    @Column(name = "po_number", nullable = false)
    private String poNumber;

    @Column(name = "po_date")
    private Date poDate;

    @Column(name = "buyer_name")
    private String buyerName;

    @Column(name = "buyer_address")
    private String buyerAddress;

    @OneToMany(mappedBy = "purchaseOrderHeader", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseOrderDetail> details = new ArrayList<>();
    
    public void addDetail(PurchaseOrderDetail detail) {
        details.add(detail);
        detail.setPurchaseOrderHeader(this);
    }

    public void removeDetail(PurchaseOrderDetail detail) {
        details.remove(detail);
        detail.setPurchaseOrderHeader(null);
    }
}
