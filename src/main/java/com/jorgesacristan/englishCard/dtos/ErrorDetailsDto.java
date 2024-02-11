package com.jorgesacristan.englishCard.dtos;

public class ErrorDetailsDto {
    private String code;
    private String target;
    private String message;

    public ErrorDetailsDto(String code, String target, String message) {
        this.code = code;
        this.target = target;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ErrorDetailsDto{");
        sb.append("code='").append(code).append('\'');
        sb.append(", target='").append(target).append('\'');
        sb.append(", message='").append(message).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
