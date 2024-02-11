package com.jorgesacristan.englishCard.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jorgesacristan.englishCard.dtos.ErrorDetailsDto;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

public class StandardResponse implements Serializable {


    @JsonProperty("timestamp")
    private Instant timestamp;
    @JsonProperty("status")
    private String status;

    @JsonProperty("code")
    private String code;

    @JsonProperty("message")
    private String message;
    @JsonProperty("uuid")
    private String uuid;

    @JsonProperty("details")
    private List<ErrorDetailsDto> errorDetails;

    public StandardResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public StandardResponse(String status, String message,Instant timestamp) {
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
    }

    public StandardResponse(String status, String message,Instant timestamp, String uuid) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
        this.uuid = uuid;
    }

    public StandardResponse(String status,String code, String message,Instant timestamp, String uuid, List<ErrorDetailsDto> errorDetails) {
        this.status = status;
        this.code=code;
        this.message = message;
        this.timestamp = timestamp;
        this.uuid = uuid;
        this.errorDetails = errorDetails;
    }
}
