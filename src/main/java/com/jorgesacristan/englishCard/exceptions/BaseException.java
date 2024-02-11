package com.jorgesacristan.englishCard.exceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Data
public class BaseException extends Exception{

    private String code;

    public BaseException() {
        super();
    }

    public BaseException(final String code) {
        super();
        this.code = code;
    }

    public BaseException(String message,final String code) {
        super(message);
        this.code = code;
    }



}
