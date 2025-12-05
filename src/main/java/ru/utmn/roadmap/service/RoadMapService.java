package ru.utmn.roadmap.service;

import ru.utmn.roadmap.web.dto.RoadMapResponseDto;
import ru.utmn.roadmap.web.dto.StepStatusUpdateDto;

import java.util.List;

public interface RoadMapService {

    /**
     * UC-02: Получить/сгенерировать дорожную карту для пользователя.
     *
     * @param userId       id пользователя
     * @param forceRefresh если true — сгенерировать заново, игнорируя сохранённую карту
     */
    RoadMapResponseDto getRoadMapForUser(Long userId, boolean forceRefresh);

    /**
     * UC-02: Обновить статусы шагов дорожной карты.
     */
    RoadMapResponseDto updateStepsStatus(Long userId, List<StepStatusUpdateDto> updates);
}
