package ru.utmn.roadmap.web.dto;

import java.time.LocalDate;

public record QuestionnaireResponseDto(
        Long id,
        LocalDate entryDate,
        String citizenshipName,
        String purpose,
        String resettlementProgramStatus,
        String hqSpecialistStatus
) {}
