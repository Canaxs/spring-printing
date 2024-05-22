package com.print.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptDTO {
    private String title="";
    private String name= "";
    private String surname= "";
    private String addressCity= "";
    private String addressCounty= "";
    private String addressStreet= "";
    private String address= "";
    private Long ibanNumber=0L;
    private String branchName= "";
    private Long accountNumber = 0L;
    private Long paymentTotal= 0L;
    private String templateName;

    private String writingAreaTitle= "";
    private String writingArea1= "";
    private String writingArea2= "";
    private String writingArea3= "";
    private String writingArea4= "";
    private String writingArea5= "";
    private String writingArea6= "";
    private String writingArea7= "";
    private String writingArea8= "";
    private String writingArea9 = "";

    public Object convert(String step) {
        return switch (step) {
            case "title" -> title;
            case "name" -> name;
            case "surname" -> surname;
            case "addressCity" -> addressCity;
            case "addressCounty" -> addressCounty;
            case "addressStreet" -> addressStreet;
            case "address" -> address;
            case "ibanNumber" -> ibanNumber;
            case "branchName" -> branchName;
            case "accountNumber" -> accountNumber;
            case "paymentTotal" -> paymentTotal;
            case "writingAreaTitle" -> writingAreaTitle;
            case "writingArea1" -> writingArea1;
            case "writingArea2" -> writingArea2;
            case "writingArea3" -> writingArea3;
            case "writingArea4" -> writingArea4;
            case "writingArea5" -> writingArea5;
            case "writingArea6" -> writingArea6;
            case "writingArea7" -> writingArea7;
            case "writingArea8" -> writingArea8;
            case "writingArea9" -> writingArea9;
            default -> "";
        };
    }
}
