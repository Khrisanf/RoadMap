package ru.utmn.roadmap.web.mapper;

import org.mapstruct.Mapper;
import ru.utmn.roadmap.domain.entity.Rule;
import ru.utmn.roadmap.web.dto.RuleDto;

@Mapper(componentModel = "spring")
public interface RuleMapper {

    RuleDto toDto(Rule rule);

    Rule toEntity(RuleDto dto);
}
