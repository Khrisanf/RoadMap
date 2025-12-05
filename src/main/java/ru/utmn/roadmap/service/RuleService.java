package ru.utmn.roadmap.service;

import ru.utmn.roadmap.web.dto.RuleDto;

import java.util.List;

public interface RuleService {

    /**
     * Вернуть активные правила, отсортированные по приоритету
     */
    List<RuleDto> findActiveRules();
}
