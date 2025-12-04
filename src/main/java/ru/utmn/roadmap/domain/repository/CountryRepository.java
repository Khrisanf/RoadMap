package ru.utmn.roadmap.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.utmn.roadmap.domain.entity.Country;

public interface CountryRepository extends JpaRepository<Country, Long> {
}
