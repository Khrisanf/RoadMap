package ru.utmn.roadmap.web.mapper;

import org.mapstruct.Mapper;
import ru.utmn.roadmap.domain.entity.User;
import ru.utmn.roadmap.web.dto.UserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User entity);
}
