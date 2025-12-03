package ru.utmn.roadmap.web.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record MarkStepsRequestDto(
        @NotEmpty List<Long> completedStepIds
) { }
