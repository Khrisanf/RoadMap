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

    @Test
    void generateSteps_shouldReturnThreeSteps_whenPurposeIsWorkAndRulesPresent() {
        LocalDate entryDate = LocalDate.of(2024, 10, 1);
        Questionnaire questionnaire = Questionnaire.builder()
                .id(1L)
                .entryDate(entryDate)
                .purpose(VisitPurpose.WORK)
                .build();

        Rule rule = Rule.builder()
                .id(10L)
                .name("Test rule")
                .description("desc")
                .condition("purpose==WORK")
                .build();

        List<RoadMapStep> result = generator.generateSteps(questionnaire, List.of(rule));

        // 1) Количество шагов
        assertThat(result).hasSize(3);

        RoadMapStep step1 = result.get(0);
        RoadMapStep step2 = result.get(1);
        RoadMapStep step3 = result.get(2);

        // 2) Общие проверки
        assertThat(step1.getStatus()).isEqualTo(StepStatus.PLANNED);
        assertThat(step2.getStatus()).isEqualTo(StepStatus.PLANNED);
        assertThat(step3.getStatus()).isEqualTo(StepStatus.PLANNED);

        assertThat(step1.getRuleId()).isEqualTo(rule.getId());
        assertThat(step2.getRuleId()).isEqualTo(rule.getId());
        assertThat(step3.getRuleId()).isEqualTo(rule.getId());

        // 3) Порядок шагов
        assertThat(step1.getOrderIndex()).isEqualTo(1);
        assertThat(step2.getOrderIndex()).isEqualTo(2);
        assertThat(step3.getOrderIndex()).isEqualTo(3);

        // 4) Дедлайны
        assertThat(step1.getDeadline()).isEqualTo(entryDate.plusDays(7));
        assertThat(step2.getDeadline()).isEqualTo(entryDate.plusDays(15));
        assertThat(step3.getDeadline()).isEqualTo(entryDate.plusDays(30));

        // 5) Тексты
        assertThat(step1.getTitle()).isEqualTo("Подготовить документы для патента");
        assertThat(step2.getTitle()).isEqualTo("Оплатить авансовый платёж за патент");
        assertThat(step3.getTitle()).isEqualTo("Подать заявление на патент");
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
