package com.print.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table
@Data
public class Invoice extends BaseEntity {

    // It was made for testing and will be arranged in accordance with the invoice information later.

    private String name;

    private String surname;

    private String address;

    private Long ibanNumber;

    private String branchName;

    private Long accountNumber;

    private Long paymentTotal;

}
