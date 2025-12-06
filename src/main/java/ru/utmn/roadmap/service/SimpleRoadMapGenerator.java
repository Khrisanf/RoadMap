package ru.utmn.roadmap.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.utmn.roadmap.domain.entity.Questionnaire;
import ru.utmn.roadmap.domain.entity.RoadMapStep;
import ru.utmn.roadmap.domain.entity.Rule;
import ru.utmn.roadmap.domain.entity.enam.StepStatus;
import ru.utmn.roadmap.domain.entity.enam.VisitPurpose;

import java.time.LocalDate;
import java.time.Period;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SimpleRoadMapGenerator implements RoadMapGenerator {

    @Override
    public List<RoadMapStep> generateSteps(Questionnaire questionnaire, List<Rule> rules) {
        if (rules == null || rules.isEmpty()) {
            return List.of();
        }

        if (questionnaire.getPurpose() != VisitPurpose.WORK) {
            return List.of();
        }

        LocalDate entryDate = questionnaire.getEntryDate();

        return rules.stream()
                .filter(Rule::isActive)
                .filter(rule -> "purpose=WORK".equals(rule.getCondition()))
                .sorted(Comparator.comparingInt(Rule::getPriority))
                .map(rule -> {
                    LocalDate deadline = null;
                    String relative = rule.getRelativeDeadline();
                    if (relative != null && !relative.isBlank()) {
                        Period period = Period.parse(relative);
                        deadline = entryDate.plus(period);
                    }

                    return RoadMapStep.builder()
                            .title(rule.getName())
                            .description(rule.getDescription())
                            .deadline(deadline)
                            .status(StepStatus.PLANNED)
                            .orderIndex(rule.getPriority())
                            .ruleId(rule.getId())
                            .build();
                })
                .toList();
    }

}
