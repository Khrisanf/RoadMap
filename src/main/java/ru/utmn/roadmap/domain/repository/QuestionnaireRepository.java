package ru.utmn.roadmap.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.utmn.roadmap.domain.entity.Questionnaire;

import java.util.Optional;

public interface QuestionnaireRepository extends JpaRepository<Questionnaire, Long> {

    Optional<Questionnaire> findByUserId(Long userId);
}
