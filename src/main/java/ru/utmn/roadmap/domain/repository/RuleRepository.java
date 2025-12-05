package ru.utmn.roadmap.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.utmn.roadmap.domain.entity.Rule;

import java.util.List;

public interface RuleRepository extends JpaRepository<Rule, Long> {

    // активные правила, упорядоченные по приоритету
    List<Rule> findByActiveTrueOrderByPriorityDesc();
}
