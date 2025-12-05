package ru.utmn.roadmap.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.utmn.roadmap.domain.entity.Country;
import ru.utmn.roadmap.domain.entity.Questionnaire;
import ru.utmn.roadmap.domain.entity.User;
import ru.utmn.roadmap.domain.repository.CountryRepository;
import ru.utmn.roadmap.domain.repository.QuestionnaireRepository;
import ru.utmn.roadmap.domain.repository.UserRepository;
import ru.utmn.roadmap.web.dto.QuestionnaireRequestDto;
import ru.utmn.roadmap.web.dto.QuestionnaireResponseDto;
import ru.utmn.roadmap.web.mapper.QuestionnaireRequestMapper;
import ru.utmn.roadmap.web.mapper.QuestionnaireResponseMapper;

@Service
@RequiredArgsConstructor
public class QuestionnaireServiceImpl implements QuestionnaireService {

    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final QuestionnaireRepository questionnaireRepository;
    private final QuestionnaireRequestMapper requestMapper;
    private final QuestionnaireResponseMapper responseMapper;

    /**
     * UC-01: шаги 1–2 + A1 + A2.
     */
    @Override
    @Transactional(readOnly = true)
    public QuestionnaireResponseDto getQuestionnaireForUser(Long currentUserId) {
        if (currentUserId == null) {
            // A3: пользователь не авторизован
            throw new IllegalStateException("Пользователь должен быть авторизован");
        }

        // A1: профиль пользователя отсутствует
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Пользователь с id %d не найден. Сначала создайте профиль.".formatted(currentUserId)
                ));

        // A2: анкета отсутствует → вернём null
        return questionnaireRepository.findByUserId(user.getId())
                .map(responseMapper::toDto)
                .orElse(null);
    }

    /**
     * UC-01: шаги 3–6 + A1.
     */
    @Override
    @Transactional
    public QuestionnaireResponseDto saveQuestionnaireForUser(Long currentUserId,
                                                             QuestionnaireRequestDto requestDto) {
        if (currentUserId == null) {
            // A3: пользователь не авторизован
            throw new IllegalStateException("Пользователь должен быть авторизован");
        }

        // A1: профиль пользователя отсутствует
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Пользователь с id %d не найден. Сначала создайте профиль.".formatted(currentUserId)
                ));

        Country country = countryRepository.findById(requestDto.countryId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Страна с id %d не найдена".formatted(requestDto.countryId())
                ));

        Questionnaire questionnaire = questionnaireRepository.findByUserId(user.getId())
                .orElse(null);

        if (questionnaire == null) {
            questionnaire = requestMapper.toEntity(requestDto, country, user);
            user.setQuestionnaire(questionnaire);
        } else {
            questionnaire.setEntryDate(requestDto.entryDate());
            questionnaire.setCitizenship(country);
            questionnaire.setPurpose(requestDto.purpose());
            questionnaire.setResettlementProgramStatus(requestDto.resettlementProgramStatus());
            questionnaire.setHqSpecialistStatus(requestDto.hqSpecialistStatus());
        }

        Questionnaire saved = questionnaireRepository.save(questionnaire);

        // шаг 6: вернуть информацию об успешно сохранённой анкете
        return responseMapper.toDto(saved);
        //TODO: ADD LOGS
    }
}
