package ru.utmn.roadmap.web.mapper;

import org.mapstruct.Mapper;
import ru.utmn.roadmap.domain.entity.RoadMapStep;
import ru.utmn.roadmap.web.dto.RoadMapStepDto;

@Mapper(componentModel = "spring")
public interface RoadMapStepMapper {

    RoadMapStepDto toDto(RoadMapStep entity);
}
