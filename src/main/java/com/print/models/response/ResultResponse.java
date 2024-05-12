package com.print.models.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@Builder
public class ResultResponse {
    private List<?> result;
    private Long statusCode = 404L;
    private String message = "NOT FOUND";

    public void setResult(List<?> result) {
        this.result = result;
        if(!result.isEmpty()){
            this.statusCode = 200L;
            this.message = "OK";
        }
    }
}
