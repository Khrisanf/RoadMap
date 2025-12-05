package ru.utmn.roadmap.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.utmn.roadmap.domain.entity.*;
import ru.utmn.roadmap.domain.repository.*;
import ru.utmn.roadmap.web.dto.RoadMapResponseDto;
import ru.utmn.roadmap.web.dto.StepStatusUpdateDto;
import ru.utmn.roadmap.web.mapper.RoadMapMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoadMapServiceImpl implements RoadMapService {

    private final UserRepository userRepository;
    private final QuestionnaireRepository questionnaireRepository;
    private final RoadMapRepository roadMapRepository;
    private final RoadMapStepRepository roadMapStepRepository;
    private final RuleRepository ruleRepository;
    private final RoadMapGenerator roadMapGenerator;
    private final RoadMapMapper roadMapMapper;

    /**
     * UC-02: шаги 1–4 + A1.
     */
    @Override
    @Transactional
    public RoadMapResponseDto getRoadMapForUser(Long userId, boolean forceRefresh) {
        if (userId == null) {
            // A3 из UC-01 переиспользуем: пользователь не авторизован
            throw new IllegalStateException("Пользователь должен быть авторизован");
        }

        // проверим, что пользователь существует (аналог A1 UC-01)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Пользователь с id %d не найден. Сначала создайте профиль.".formatted(userId)
                ));

        // UC-02 предусловие: должна быть анкета
        Questionnaire questionnaire = questionnaireRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Анкета пользователя не найдена. Сначала заполните анкету."
                ));

        // пробуем использовать уже сохранённую карту, если не запросили форс-перегенерацию
        RoadMap existing = roadMapRepository.findByUserId(userId).orElse(null);
        if (existing != null && !forceRefresh) {
            return roadMapMapper.toDto(existing);
        }

        // шаг 2: получаем текущие правила
        List<Rule> rules = ruleRepository.findAll();

        // шаг 3: генерируем шаги
        List<RoadMapStep> generatedSteps = roadMapGenerator.generateSteps(questionnaire, rules);

        if (generatedSteps.isEmpty()) {
            // A1: ни одно правило не подошло
            throw new IllegalArgumentException(
                    "Невозможно составить дорожную карту: ни одно из правил не подошло для данной анкеты."
            );
        }

        RoadMap roadMap;
        if (existing == null) {
            roadMap = RoadMap.builder()
                    .user(user)
                    .build();
        } else {
            // очищаем старые шаги, orphanRemoval=true удалит их из БД
            existing.getSteps().clear();
            roadMap = existing;
        }

        int index = 1;
        for (RoadMapStep step : generatedSteps) {
            step.setRoadMap(roadMap);
            if (step.getOrderIndex() == null) {
                step.setOrderIndex(index++);
            }
            roadMap.getSteps().add(step);
        }

        RoadMap saved = roadMapRepository.save(roadMap);
        return roadMapMapper.toDto(saved);
    }

    /**
     * UC-02: шаги 5–7.
     */
    @Override
    @Transactional
    public RoadMapResponseDto updateStepsStatus(Long userId, List<StepStatusUpdateDto> updates) {
        if (userId == null) {
            throw new IllegalStateException("Пользователь должен быть авторизован");
        }

        RoadMap roadMap = roadMapRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Дорожная карта для пользователя с id %d не найдена.".formatted(userId)
                ));

        if (updates == null || updates.isEmpty()) {
            // ничего не меняем, просто возвращаем текущее состояние
            return roadMapMapper.toDto(roadMap);
        }

        List<Long> ids = updates.stream()
                .map(StepStatusUpdateDto::id)
                .toList();

        List<RoadMapStep> steps = roadMapStepRepository.findByIdIn(ids);

        Map<Long, RoadMapStep> stepById = steps.stream()
                .collect(Collectors.toMap(RoadMapStep::getId, s -> s));

        for (StepStatusUpdateDto update : updates) {
            RoadMapStep step = stepById.get(update.id());
            if (step == null) {
                throw new IllegalArgumentException(
                        "Шаг с id %d не найден".formatted(update.id())
                );
            }
            if (!step.getRoadMap().getId().equals(roadMap.getId())) {
                throw new IllegalArgumentException(
                        "Шаг с id %d не принадлежит дорожной карте данного пользователя".formatted(update.id())
                );
            }
            step.setStatus(update.status());
        }

        roadMapStepRepository.saveAll(steps);

        // шаг 7: информируем об успешном сохранении
        return roadMapMapper.toDto(roadMap);
    }
}
