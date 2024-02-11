package com.jorgesacristan.englishCard.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class UpdateLanguageRequest implements Serializable {
    Long id;
    String language;
    String image;
}
