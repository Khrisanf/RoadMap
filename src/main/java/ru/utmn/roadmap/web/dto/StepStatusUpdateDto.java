package ru.utmn.roadmap.web.dto;

import ru.utmn.roadmap.domain.entity.enam.StepStatus;

public record StepStatusUpdateDto(
        Long id,
        StepStatus status
) {}
