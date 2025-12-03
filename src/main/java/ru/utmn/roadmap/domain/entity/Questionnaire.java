package ru.utmn.roadmap.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.utmn.roadmap.domain.entity.enam.ProgramStatus;
import ru.utmn.roadmap.domain.entity.enam.VisitPurpose;

import java.time.LocalDate;

@Entity
@Table(name = "questionnaires")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Questionnaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country citizenship;

    @Enumerated(EnumType.STRING)
    @Column(name = "visit_purpose", nullable = false)
    private VisitPurpose purpose;

    @Enumerated(EnumType.STRING)
    @Column(name = "resettlement_status", nullable = false)
    private ProgramStatus resettlementProgramStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "hq_status", nullable = false)
    private ProgramStatus hqSpecialistStatus;
}
