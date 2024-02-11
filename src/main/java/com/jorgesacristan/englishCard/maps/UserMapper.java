package com.jorgesacristan.englishCard.maps;

import com.jorgesacristan.englishCard.dtos.UserDto;
import com.jorgesacristan.englishCard.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface UserMapper {
    //UserMapper MAPPER = Mappers.getMapper(UserMapper.class);
    UserDto userToUserDto(User user);
    User userDtoToUser (UserDto userDto);

}
