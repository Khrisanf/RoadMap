package ru.utmn.roadmap.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false, length = 2000)
    private String condition;

    @Column(length = 2000)
    private String templateText;

    @Column(name = "relative_deadline")
    private String relativeDeadline;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private int priority = 0;
}
