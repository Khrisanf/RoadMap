package ru.utmn.roadmap.service;

import ru.utmn.roadmap.domain.entity.Rule;
import ru.utmn.roadmap.domain.entity.Questionnaire;
import ru.utmn.roadmap.domain.entity.RoadMapStep;

import java.util.List;

public interface RoadMapGenerator {

    /**
     * По анкете и списку правил формирует список шагов (не сохраняет их)
     */
    List<RoadMapStep> generateSteps(Questionnaire questionnaire, List<Rule> rules);
}
