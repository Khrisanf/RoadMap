package ru.utmn.roadmap.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.utmn.roadmap.domain.entity.RoadMap;
import ru.utmn.roadmap.web.dto.RoadMapResponseDto;

@Mapper(componentModel = "spring", uses = RoadMapStepMapper.class)
public interface RoadMapMapper {

    @Mapping(target = "userLogin", source = "user.login")
    RoadMapResponseDto toDto(RoadMap roadMap);
}
