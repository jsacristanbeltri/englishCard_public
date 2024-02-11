package com.jorgesacristan.englishCard.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class CreateDeckRequest implements Serializable {

    @NotNull(message = "name is mandatory, it can't be null")
    @NotBlank(message = "name is mandatory, it can't be empty")
    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("language")
    @NotNull(message = "language is mandatory, it can't be null")
    @NotBlank(message = "language is mandatory, it can't be empty")
    private String language;
}
