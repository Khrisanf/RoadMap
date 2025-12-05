package ru.utmn.roadmap.web.dto;

import java.time.LocalDate;

public record RoadMapStepDto(
        Long id,
        String title,
        String description,
        LocalDate deadline,
        String status,
        Integer orderIndex,
        Long ruleId
) {}
