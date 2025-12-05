package ru.utmn.roadmap.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.utmn.roadmap.domain.entity.RoadMap;

import java.util.Optional;

public interface RoadMapRepository extends JpaRepository<RoadMap, Long> {

    Optional<RoadMap> findByUserId(Long userId);
}
