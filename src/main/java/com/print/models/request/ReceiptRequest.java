package com.print.models.request;

import lombok.Data;

@Data
public class ReceiptRequest {
    private String name;
    private String surname;
    private String addressCity;
    private String addressCounty;
    private String addressStreet;
    private String address;
    private Long ibanNumber;
    private String branchName;
    private Long accountNumber;
    private Long paymentTotal;

    private String writingAreaTitle;
    private String writingArea1;
    private String writingArea2;
    private String writingArea3;
    private String writingArea4;
    private String writingArea5;
    private String writingArea6;
    private String writingArea7;
    private String writingArea8;
    private String writingArea9;
}
