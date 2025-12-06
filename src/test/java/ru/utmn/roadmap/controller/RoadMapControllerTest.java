package ru.utmn.roadmap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.utmn.roadmap.domain.entity.enam.StepStatus;
import ru.utmn.roadmap.handler.GlobalExceptionHandler;
import ru.utmn.roadmap.service.RoadMapService;
import ru.utmn.roadmap.web.controller.RoadMapController;
import ru.utmn.roadmap.web.dto.RoadMapResponseDto;
import ru.utmn.roadmap.web.dto.RoadMapStepDto;
import ru.utmn.roadmap.web.dto.StepStatusUpdateDto;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RoadMapControllerTest {

    @Mock
    private RoadMapService roadMapService;

    @InjectMocks
    private RoadMapController roadMapController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(roadMapController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ---------- GET /api/roadmap ----------

    @Test
    void getRoadMap_shouldReturnDto_andUseDefaultForceRefreshFalse() throws Exception {
        RoadMapStepDto step = new RoadMapStepDto(
                1L,
                "Подготовить документы",
                "Описание",
                LocalDate.of(2024, 10, 5),
                "PLANNED",
                1,
                10L
        );

        RoadMapResponseDto dto = new RoadMapResponseDto(
                100L,
                "demoUser",
                List.of(step)
        );

        // data.sql
        given(roadMapService.getRoadMapForUser(1L, false))
                .willReturn(dto);

        mockMvc.perform(get("/api/roadmap"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.userLogin").value("demoUser"))
                .andExpect(jsonPath("$.steps[0].id").value(1))
                .andExpect(jsonPath("$.steps[0].title").value("Подготовить документы"))
                .andExpect(jsonPath("$.steps[0].status").value("PLANNED"));

        verify(roadMapService).getRoadMapForUser(1L, false);
    }

    @Test
    void getRoadMap_shouldPassForceRefreshTrue_whenParamIsTrue() throws Exception {
        RoadMapResponseDto dto = new RoadMapResponseDto(
                100L,
                "demoUser",
                List.of()
        );

        given(roadMapService.getRoadMapForUser(1L, true))
                .willReturn(dto);

        mockMvc.perform(get("/api/roadmap")
                        .param("forceRefresh", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100));

        verify(roadMapService).getRoadMapForUser(1L, true);
    }

    // ---------- PATCH /api/roadmap/steps ----------

    @Test
    void updateStepsStatus_shouldReturnDto_andCallService() throws Exception {
        List<StepStatusUpdateDto> updates = List.of(
                new StepStatusUpdateDto(1L, StepStatus.DONE),
                new StepStatusUpdateDto(2L, StepStatus.IN_PROGRESS)
        );

        RoadMapStepDto step1 = new RoadMapStepDto(
                1L,
                "Шаг 1",
                "Описание 1",
                LocalDate.of(2024, 10, 5),
                "DONE",
                1,
                10L
        );

        RoadMapStepDto step2 = new RoadMapStepDto(
                2L,
                "Шаг 2",
                "Описание 2",
                LocalDate.of(2024, 10, 10),
                "IN_PROGRESS",
                2,
                10L
        );

        RoadMapResponseDto dto = new RoadMapResponseDto(
                100L,
                "demoUser",
                List.of(step1, step2)
        );

        given(roadMapService.updateStepsStatus(eq(1L), anyList()))
                .willReturn(dto);

        String json = objectMapper.writeValueAsString(updates);

        mockMvc.perform(
                        patch("/api/roadmap/steps")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.steps[0].status").value("DONE"))
                .andExpect(jsonPath("$.steps[1].status").value("IN_PROGRESS"));

        verify(roadMapService).updateStepsStatus(eq(1L), anyList());
    }
}
