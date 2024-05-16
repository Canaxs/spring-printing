package com.print.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Table
@Entity
public class InvoiceProduct implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String name;
    private Double unitPrice;
    private Integer amount;
}
