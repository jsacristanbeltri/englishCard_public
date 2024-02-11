package com.jorgesacristan.englishCard.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class CreateCardRequest implements Serializable {

    @JsonProperty("name1")
    @NotNull(message = "name1 is mandatory")
    @NotBlank(message = "name1 is mandatory")
    private String name1;

    @JsonProperty("name2")
    @NotNull(message = "name2 is mandatory")
    @NotBlank(message = "name2 is mandatory")
    private String name2;

    @JsonProperty("idDeck")
    @NotNull(message = "idDeck is mandatory")
    private Long idDeck;

    public String getName1() {
        return name1;
    }

    public String getName2() {
        return name2;
    }

    public Long getIdDeck() {
        return idDeck;
    }
}
