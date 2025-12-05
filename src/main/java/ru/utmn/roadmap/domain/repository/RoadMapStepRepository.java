package ru.utmn.roadmap.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.utmn.roadmap.domain.entity.RoadMapStep;

import java.util.List;

public interface RoadMapStepRepository extends JpaRepository<RoadMapStep, Long> {

    List<RoadMapStep> findByRoadMapIdOrderByOrderIndexAsc(Long roadMapId);

    // получение по id
    List<RoadMapStep> findByIdIn(List<Long> ids);
}
