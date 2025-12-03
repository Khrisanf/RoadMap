package ru.utmn.roadmap.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.utmn.roadmap.domain.entity.enam.StepStatus;

import java.time.LocalDate;

@Entity
@Table(name = "roadmap_steps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoadMapStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_id", nullable = false)
    private RoadMap roadMap;

    @Column(nullable = false)
    private String description;

    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StepStatus status;

    private String message;
}
