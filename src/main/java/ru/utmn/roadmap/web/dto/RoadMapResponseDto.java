package ru.utmn.roadmap.web.dto;

import java.util.List;

public record RoadMapResponseDto(
        Long id,
        String userLogin,
        List<RoadMapStepDto> steps
) { }