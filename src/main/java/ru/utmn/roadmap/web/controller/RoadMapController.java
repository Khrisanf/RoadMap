package ru.utmn.roadmap.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.utmn.roadmap.service.RoadMapService;
import ru.utmn.roadmap.web.dto.RoadMapResponseDto;
import ru.utmn.roadmap.web.dto.StepStatusUpdateDto;

import java.util.List;

@RestController
@RequestMapping("/api/roadmap")
@RequiredArgsConstructor
public class RoadMapController {

    // Пока используем того же демо-пользователя, что и в QuestionnaireController
    private static final Long DEMO_USER_ID = 1L;

    private final RoadMapService roadMapService;

    /**
     * UC-02: шаги 1–4 + A1.
     *
     * GET /api/roadmap?forceRefresh=false
     *
     * forceRefresh = true — пересчитать карту по текущим правилам, игнорируя сохранённую.
     */
    @GetMapping
    public ResponseEntity<RoadMapResponseDto> getRoadMap(
            @RequestParam(name = "forceRefresh", defaultValue = "false") boolean forceRefresh
    ) {
        RoadMapResponseDto dto = roadMapService.getRoadMapForUser(DEMO_USER_ID, forceRefresh);
        return ResponseEntity.ok(dto);
    }

    /**
     * UC-02: шаги 5–7.
     *
     * PATCH /api/roadmap/steps
     *
     * Тело запроса: список обновлений статусов шагов.
     * Пример:
     * [
     *   { "id": 1, "status": "DONE" },
     *   { "id": 2, "status": "IN_PROGRESS" }
     * ]
     */
    @PatchMapping("/steps")
    public ResponseEntity<RoadMapResponseDto> updateStepsStatus(
            @RequestBody List<StepStatusUpdateDto> updates
    ) {
        RoadMapResponseDto dto = roadMapService.updateStepsStatus(DEMO_USER_ID, updates);
        return ResponseEntity.ok(dto);
    }
}
