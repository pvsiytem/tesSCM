package com.test.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tm_detail")
public class PurchaseOrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "po_number", referencedColumnName = "po_number")
    private PurchaseOrderHeader purchaseOrderHeader;
    
    @Column(name = "part_no")
    private String partNo;

    @Column(name = "part_name")
    private String partName;

    @Column(name = "qty")
    private Integer qty;

    @Column(name = "unit")
    private String unit;

    @Column(name = "price")
    private Double price;
}
