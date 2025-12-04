package ru.utmn.roadmap.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.utmn.roadmap.domain.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
