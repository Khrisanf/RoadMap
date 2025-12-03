package ru.utmn.roadmap.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.utmn.roadmap.domain.entity.Country;
import ru.utmn.roadmap.domain.entity.Questionnaire;
import ru.utmn.roadmap.domain.entity.User;
import ru.utmn.roadmap.web.dto.QuestionnaireRequestDto;

@Mapper(componentModel = "spring")
public interface QuestionnaireRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "citizenship", source = "country")
    Questionnaire toEntity(QuestionnaireRequestDto dto,
                           Country country,
                           User user);
}
