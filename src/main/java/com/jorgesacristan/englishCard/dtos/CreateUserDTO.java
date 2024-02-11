package com.jorgesacristan.englishCard.dtos;

import com.jorgesacristan.englishCard.validations.ValidPassword;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserDTO implements Serializable {
    private String username;
    private String password;
    private String email;
    private String avatar;
}
