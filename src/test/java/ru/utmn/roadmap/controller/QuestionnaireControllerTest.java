package ru.utmn.roadmap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.utmn.roadmap.domain.entity.enam.ProgramStatus;
import ru.utmn.roadmap.domain.entity.enam.VisitPurpose;
import ru.utmn.roadmap.handler.GlobalExceptionHandler;
import ru.utmn.roadmap.service.QuestionnaireService;
import ru.utmn.roadmap.web.controller.QuestionnaireController;
import ru.utmn.roadmap.web.dto.QuestionnaireRequestDto;
import ru.utmn.roadmap.web.dto.QuestionnaireResponseDto;

import java.time.LocalDate;

import static org.hamcrest.Matchers.isEmptyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class QuestionnaireControllerTest {

    @Mock
    private QuestionnaireService questionnaireService;

    @InjectMocks
    private QuestionnaireController questionnaireController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc() {

        return MockMvcBuilders.standaloneSetup(questionnaireController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getQuestionnaire_shouldReturn200_whenExists() throws Exception {
        QuestionnaireResponseDto dto = new QuestionnaireResponseDto(
                10L,
                LocalDate.of(2024, 10, 1),
                "Россия",
                "WORK",
                "NONE",
                "NONE"
        );

        given(questionnaireService.getQuestionnaireForUser(1L)).willReturn(dto);

        mockMvc().perform(get("/api/questionnaire"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.citizenshipName").value("Россия"));
    }

    @Test
    void getQuestionnaire_shouldReturn204_whenNotExists() throws Exception {
        given(questionnaireService.getQuestionnaireForUser(1L)).willReturn(null);

        mockMvc().perform(get("/api/questionnaire"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(isEmptyString()));
    }
}
