package ru.utmn.roadmap.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.utmn.roadmap.domain.entity.Questionnaire;
import ru.utmn.roadmap.domain.entity.RoadMapStep;
import ru.utmn.roadmap.domain.entity.Rule;
import ru.utmn.roadmap.domain.entity.enam.StepStatus;
import ru.utmn.roadmap.domain.entity.enam.VisitPurpose;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SimpleRoadMapGenerator implements RoadMapGenerator {

    @Override
    public List<RoadMapStep> generateSteps(Questionnaire questionnaire, List<Rule> rules) {
        // если нет правил — считаем, что A1 (ни одно правило не подошло)
        if (rules == null || rules.isEmpty()) {
            return List.of();
        }

        // дорожная карта есть только для цели WORK
        if (questionnaire.getPurpose() != VisitPurpose.WORK) {
            return List.of();
        }

        LocalDate entryDate = questionnaire.getEntryDate();
        Long ruleId = rules.get(0).getId(); // просто привяжем шаги к первому правилу для примера

        List<RoadMapStep> steps = new ArrayList<>();

        steps.add(RoadMapStep.builder()
                .title("Подготовить документы для патента")
                .description("Собрать паспорт, миграционную карту, регистрацию, фото.")
                .deadline(entryDate.plusDays(7))
                .status(StepStatus.PLANNED)
                .orderIndex(1)
                .ruleId(ruleId)
                .build()
        );

        steps.add(RoadMapStep.builder()
                .title("Оплатить авансовый платёж за патент")
                .description("Оплатить НДФЛ за первый месяц действия патента.")
                .deadline(entryDate.plusDays(15))
                .status(StepStatus.PLANNED)
                .orderIndex(2)
                .ruleId(ruleId)
                .build()
        );

        steps.add(RoadMapStep.builder()
                .title("Подать заявление на патент")
                .description("Обратиться в уполномоченный орган с полным пакетом документов.")
                .deadline(entryDate.plusDays(30))
                .status(StepStatus.PLANNED)
                .orderIndex(3)
                .ruleId(ruleId)
                .build()
        );

        return steps;
    }
}
