package com.jorgesacristan.englishCard.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class CreateLanguageRequest implements Serializable {

    @NotBlank(message = "Language is mandatory, It can't be empty")
    @NotNull(message = "Language is mandatory, It can't be null")
    String language;

    @NotBlank(message = "Language is mandatory, It can't be empty")
    @NotNull(message = "Language is mandatory, It can't be null")
    String image;
}
