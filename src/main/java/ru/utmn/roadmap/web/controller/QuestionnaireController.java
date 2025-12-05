package ru.utmn.roadmap.web.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.utmn.roadmap.service.QuestionnaireService;
import ru.utmn.roadmap.web.dto.QuestionnaireRequestDto;
import ru.utmn.roadmap.web.dto.QuestionnaireResponseDto;

@RestController
@RequestMapping("/api/questionnaire")
@RequiredArgsConstructor
@Tag(name = "Questionnaire", description = "Работа с анкетой пользователя (UC-01)")
public class QuestionnaireController {

    private final QuestionnaireService questionnaireService;

    // data.sql
    private static final Long DEMO_USER_ID = 1L;

    /**
     * UC-01: шаги 1–2 + A2.
     * Получить анкету пользователя или пустой ответ, если анкета ещё не создавалась.
     */
    @GetMapping
    public ResponseEntity<QuestionnaireResponseDto> getQuestionnaire() {
        QuestionnaireResponseDto dto = questionnaireService.getQuestionnaireForUser(DEMO_USER_ID);

        if (dto == null) {
            // A2: анкеты нет → вернём 204 No Content
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(dto);
    }

    /**
     * UC-01: шаги 3–6.
     * Создать или обновить анкету пользователя.
     */
    @PostMapping
    public ResponseEntity<QuestionnaireResponseDto> saveQuestionnaire(
            @Valid @RequestBody QuestionnaireRequestDto requestDto
    ) {
        QuestionnaireResponseDto dto =
                questionnaireService.saveQuestionnaireForUser(DEMO_USER_ID, requestDto);

        // шаг 6: информирование об успешном сохранении
        return ResponseEntity.ok(dto);
    }
}
