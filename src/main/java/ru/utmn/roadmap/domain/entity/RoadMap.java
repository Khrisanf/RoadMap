package ru.utmn.roadmap.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "roadmaps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoadMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "roadMap", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RoadMapStep> steps = new ArrayList<>();
}
