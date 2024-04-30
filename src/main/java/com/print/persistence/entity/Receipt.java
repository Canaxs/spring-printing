package com.print.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "receipt")
@Data
public class Receipt extends BaseEntity {

    private String name;

    private String surname;

    private String address;

    private Long ibanNumber;

    private String branchName;

    private Long accountNumber;

    private Long paymentTotal;

}
