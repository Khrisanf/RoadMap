package ru.utmn.roadmap.service;

import ru.utmn.roadmap.web.dto.QuestionnaireRequestDto;
import ru.utmn.roadmap.web.dto.QuestionnaireResponseDto;

public interface QuestionnaireService {

    /**
     * UC-01: шаги 1–2 + A1 + A2.
     * Вернуть анкету текущего пользователя или "пустой" ответ, если её ещё нет.
     */
    QuestionnaireResponseDto getQuestionnaireForUser(Long currentUserId);

    /**
     * UC-01: шаги 3–6 + A1.
     * Сохранить/обновить анкету текущего пользователя.
     */
    QuestionnaireResponseDto saveQuestionnaireForUser(Long currentUserId,
                                                      QuestionnaireRequestDto requestDto);
}
