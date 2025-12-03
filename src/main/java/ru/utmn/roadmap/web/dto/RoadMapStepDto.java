package ru.utmn.roadmap.web.dto;

import java.time.LocalDate;
import ru.utmn.roadmap.domain.entity.enam.StepStatus;

public record RoadMapStepDto(
        Long id,
        String description,
        LocalDate deadline,
        StepStatus status,
        String message
) { }