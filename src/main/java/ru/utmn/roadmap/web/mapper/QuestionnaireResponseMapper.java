package ru.utmn.roadmap.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.utmn.roadmap.domain.entity.Questionnaire;
import ru.utmn.roadmap.web.dto.QuestionnaireResponseDto;

@Mapper(componentModel = "spring")
public interface QuestionnaireResponseMapper {

    @Mapping(target = "citizenshipName", source = "citizenship.name")
    QuestionnaireResponseDto toDto(Questionnaire questionnaire);
}