package ru.utmn.roadmap.web.dto;

import jakarta.validation.constraints.*;
import ru.utmn.roadmap.domain.entity.enam.ProgramStatus;
import ru.utmn.roadmap.domain.entity.enam.VisitPurpose;

import java.time.LocalDate;

public record QuestionnaireRequestDto(
        @NotNull LocalDate entryDate,
        @NotNull Long countryId,
        @NotNull VisitPurpose purpose,
        @NotNull ProgramStatus resettlementProgramStatus,
        @NotNull ProgramStatus hqSpecialistStatus
) {}
