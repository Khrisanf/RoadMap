package ru.utmn.roadmap.web.dto;

public record RuleDto(
        Long id,
        String name,
        String description,
        String condition,
        String templateText,
        String relativeDeadline,
        boolean active,
        int priority
) {}
