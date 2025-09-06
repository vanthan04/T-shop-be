package com.cartservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.cartservice.exception.ErrorCode;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class ApiResponse <T> {
    private boolean success;
    private int statusCode;
    private String message;
    private T dataResponse;

    public ApiResponse(ErrorCode errorCode){
        this.success = false;
        this.statusCode = errorCode.getStatusCode();
        this.message = errorCode.getMessage();
        this.dataResponse = null;
    }

    public ApiResponse(EnumCode enumCode, T dataResponse){
        this.success = true;
        this.statusCode = enumCode.getStatusCode();
        this.message = enumCode.getMessage();
        this.dataResponse = dataResponse;
    }
}

