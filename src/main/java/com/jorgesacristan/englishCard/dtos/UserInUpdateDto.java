package com.jorgesacristan.englishCard.dtos;

import com.jorgesacristan.englishCard.validations.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInUpdateDto {

    @NotNull(message = "username is mandatory, It can't be null")
    @NotBlank(message = "username is mandatory, It can't be empty")
    private String username;

    @NotNull(message = "password is mandatory, It can't be null")
    @NotBlank(message = "password is mandatory, It can't be empty")

    @ValidPassword
    private String password;

    @Email
    private String email;

    private int level;
    private int experience;
    private int gems;
    private int logStreak;
    private String avatar;
}
