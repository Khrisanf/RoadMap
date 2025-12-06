package ru.utmn.roadmap.service;

import org.junit.jupiter.api.Test;
import ru.utmn.roadmap.domain.entity.Questionnaire;
import ru.utmn.roadmap.domain.entity.RoadMapStep;
import ru.utmn.roadmap.domain.entity.Rule;
import ru.utmn.roadmap.domain.entity.enam.StepStatus;
import ru.utmn.roadmap.domain.entity.enam.VisitPurpose;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleRoadMapGeneratorTest {

    private final SimpleRoadMapGenerator generator = new SimpleRoadMapGenerator();

    @Test
    void generateSteps_shouldReturnEmpty_whenRulesIsNull() {
        Questionnaire questionnaire = createQuestionnaire(VisitPurpose.WORK);

        List<RoadMapStep> result = generator.generateSteps(questionnaire, null);

        assertThat(result).isEmpty();
    }

    @Test
    void generateSteps_shouldReturnEmpty_whenRulesIsEmpty() {
        Questionnaire questionnaire = createQuestionnaire(VisitPurpose.WORK);

        List<RoadMapStep> result = generator.generateSteps(questionnaire, List.of());

        assertThat(result).isEmpty();
    }

    @Test
    void generateSteps_shouldReturnEmpty_whenPurposeIsNotWork() {
        Questionnaire questionnaire = createQuestionnaire(VisitPurpose.STUDY);
        Rule rule = Rule.builder()
                .id(10L)
                .name("Test rule")
                .description("desc")
                .condition("purpose==WORK")
                .build();

        List<RoadMapStep> result = generator.generateSteps(questionnaire, List.of(rule));

        assertThat(result).isEmpty();
    }

    // ---------- helper ----------

    private Questionnaire createQuestionnaire(VisitPurpose purpose) {
        return Questionnaire.builder()
                .id(1L)
                .entryDate(LocalDate.of(2024, 10, 1))
                .purpose(purpose)
                .build();
    }
}
