package ru.utmn.roadmap.web.mapper;

import org.mapstruct.Mapper;
import ru.utmn.roadmap.domain.entity.Country;
import ru.utmn.roadmap.web.dto.CountryDto;

@Mapper(componentModel = "spring")
public interface CountryMapper {

    CountryDto toDto(Country entity);
}
