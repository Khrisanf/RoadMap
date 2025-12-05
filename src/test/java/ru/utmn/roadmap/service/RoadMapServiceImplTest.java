package ru.utmn.roadmap.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.utmn.roadmap.domain.entity.*;
import ru.utmn.roadmap.domain.entity.enam.StepStatus;
import ru.utmn.roadmap.domain.entity.enam.VisitPurpose;
import ru.utmn.roadmap.domain.repository.*;
import ru.utmn.roadmap.web.dto.RoadMapResponseDto;
import ru.utmn.roadmap.web.dto.StepStatusUpdateDto;
import ru.utmn.roadmap.web.mapper.RoadMapMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoadMapServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private QuestionnaireRepository questionnaireRepository;

    @Mock
    private RoadMapRepository roadMapRepository;

    @Mock
    private RoadMapStepRepository roadMapStepRepository;

    @Mock
    private RuleRepository ruleRepository;

    @Mock
    private RoadMapGenerator roadMapGenerator;

    @Mock
    private RoadMapMapper roadMapMapper;

    @InjectMocks
    private RoadMapServiceImpl roadMapService;

    // ---------- getRoadMapForUser ----------

    @Test
    void getRoadMapForUser_shouldThrow_whenUserIdIsNull() {
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> roadMapService.getRoadMapForUser(null, false)
        );

        assertThat(ex.getMessage()).isEqualTo("Пользователь должен быть авторизован");
    }

    @Test
    void getRoadMapForUser_shouldThrow_whenUserNotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> roadMapService.getRoadMapForUser(userId, false)
        );

        assertThat(ex.getMessage()).contains("Пользователь с id 1 не найден");
    }

    @Test
    void getRoadMapForUser_shouldThrow_whenQuestionnaireNotFound() {
        Long userId = 1L;
        User user = createUser(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(questionnaireRepository.findByUserId(userId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> roadMapService.getRoadMapForUser(userId, false)
        );

        assertThat(ex.getMessage()).contains("Анкета пользователя не найдена");
    }

    @Test
    void getRoadMapForUser_shouldReturnExistingRoadMap_whenNotForceRefresh() {
        Long userId = 1L;
        User user = createUser(userId);
        Questionnaire questionnaire = createQuestionnaire(user);
        RoadMap existing = RoadMap.builder()
                .id(100L)
                .user(user)
                .build();

        RoadMapResponseDto dto = new RoadMapResponseDto(
                existing.getId(),
                user.getLogin(),
                List.of()
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(questionnaireRepository.findByUserId(userId)).thenReturn(Optional.of(questionnaire));
        when(roadMapRepository.findByUserId(userId)).thenReturn(Optional.of(existing));
        when(roadMapMapper.toDto(existing)).thenReturn(dto);

        RoadMapResponseDto result = roadMapService.getRoadMapForUser(userId, false);

        assertThat(result).isEqualTo(dto);

        verify(ruleRepository, never()).findAll();
        verify(roadMapGenerator, never()).generateSteps(any(), anyList());
    }

    @Test
    void getRoadMapForUser_shouldGenerateAndSaveRoadMap_whenNoExisting() {
        Long userId = 1L;
        User user = createUser(userId);
        Questionnaire questionnaire = createQuestionnaire(user);

        Rule rule = Rule.builder()
                .id(10L)
                .name("Test rule")
                .description("desc")
                .condition("purpose==WORK")
                .build();

        RoadMapStep step1 = RoadMapStep.builder()
                .id(null)
                .title("s1")
                .orderIndex(null)
                .status(StepStatus.PLANNED)
                .build();

        RoadMapStep step2 = RoadMapStep.builder()
                .id(null)
                .title("s2")
                .orderIndex(5)
                .status(StepStatus.PLANNED)
                .build();

        List<RoadMapStep> generated = List.of(step1, step2);

        RoadMapResponseDto dto = new RoadMapResponseDto(
                200L,
                user.getLogin(),
                List.of()
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(questionnaireRepository.findByUserId(userId)).thenReturn(Optional.of(questionnaire));
        when(roadMapRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(ruleRepository.findAll()).thenReturn(List.of(rule));
        when(roadMapGenerator.generateSteps(questionnaire, List.of(rule))).thenReturn(generated);

        when(roadMapRepository.save(any(RoadMap.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(roadMapMapper.toDto(any(RoadMap.class))).thenReturn(dto);

        RoadMapResponseDto result = roadMapService.getRoadMapForUser(userId, false);

        assertThat(result).isEqualTo(dto);

        ArgumentCaptor<RoadMap> captor = ArgumentCaptor.forClass(RoadMap.class);
        verify(roadMapRepository).save(captor.capture());

        RoadMap saved = captor.getValue();
        assertThat(saved.getUser()).isEqualTo(user);
        assertThat(saved.getSteps()).hasSize(2);

        RoadMapStep saved1 = saved.getSteps().get(0);
        RoadMapStep saved2 = saved.getSteps().get(1);

        assertThat(saved1.getRoadMap()).isEqualTo(saved);
        assertThat(saved2.getRoadMap()).isEqualTo(saved);

        assertThat(saved1.getOrderIndex()).isEqualTo(1);
        assertThat(saved2.getOrderIndex()).isEqualTo(5);
    }

    @Test
    void getRoadMapForUser_shouldThrow_whenGeneratedStepsEmpty() {
        Long userId = 1L;
        User user = createUser(userId);
        Questionnaire questionnaire = createQuestionnaire(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(questionnaireRepository.findByUserId(userId)).thenReturn(Optional.of(questionnaire));
        when(roadMapRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(ruleRepository.findAll()).thenReturn(List.of());
        when(roadMapGenerator.generateSteps(any(), anyList())).thenReturn(List.of());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> roadMapService.getRoadMapForUser(userId, false)
        );

        assertThat(ex.getMessage())
                .contains("Невозможно составить дорожную карту");
    }

    // ---------- updateStepsStatus ----------

    @Test
    void updateStepsStatus_shouldThrow_whenUserIdIsNull() {
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> roadMapService.updateStepsStatus(null, List.of())
        );

        assertThat(ex.getMessage()).isEqualTo("Пользователь должен быть авторизован");
    }

    @Test
    void updateStepsStatus_shouldThrow_whenRoadMapNotFound() {
        Long userId = 1L;
        when(roadMapRepository.findByUserId(userId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> roadMapService.updateStepsStatus(userId, List.of())
        );

        assertThat(ex.getMessage())
                .contains("Дорожная карта для пользователя с id 1 не найдена");
    }

    @Test
    void updateStepsStatus_shouldReturnCurrentRoadMap_whenUpdatesEmpty() {
        Long userId = 1L;
        RoadMap roadMap = RoadMap.builder()
                .id(100L)
                .user(createUser(userId))
                .build();

        RoadMapResponseDto dto = new RoadMapResponseDto(
                roadMap.getId(),
                roadMap.getUser().getLogin(),
                List.of()
        );

        when(roadMapRepository.findByUserId(userId)).thenReturn(Optional.of(roadMap));
        when(roadMapMapper.toDto(roadMap)).thenReturn(dto);

        RoadMapResponseDto result = roadMapService.updateStepsStatus(userId, List.of());

        assertThat(result).isEqualTo(dto);
        verify(roadMapStepRepository, never()).saveAll(anyList());
    }

    @Test
    void updateStepsStatus_shouldUpdateStatuses_whenAllValid() {
        Long userId = 1L;
        RoadMap roadMap = RoadMap.builder()
                .id(100L)
                .user(createUser(userId))
                .build();

        RoadMapStep step1 = RoadMapStep.builder()
                .id(1L)
                .roadMap(roadMap)
                .status(StepStatus.PLANNED)
                .build();

        RoadMapStep step2 = RoadMapStep.builder()
                .id(2L)
                .roadMap(roadMap)
                .status(StepStatus.PLANNED)
                .build();

        List<StepStatusUpdateDto> updates = List.of(
                new StepStatusUpdateDto(1L, StepStatus.DONE),
                new StepStatusUpdateDto(2L, StepStatus.IN_PROGRESS)
        );

        RoadMapResponseDto dto = new RoadMapResponseDto(
                roadMap.getId(),
                roadMap.getUser().getLogin(),
                List.of()
        );

        when(roadMapRepository.findByUserId(userId)).thenReturn(Optional.of(roadMap));
        when(roadMapStepRepository.findByIdIn(List.of(1L, 2L)))
                .thenReturn(List.of(step1, step2));
        when(roadMapMapper.toDto(roadMap)).thenReturn(dto);

        RoadMapResponseDto result = roadMapService.updateStepsStatus(userId, updates);

        assertThat(result).isEqualTo(dto);

        assertThat(step1.getStatus()).isEqualTo(StepStatus.DONE);
        assertThat(step2.getStatus()).isEqualTo(StepStatus.IN_PROGRESS);

        verify(roadMapStepRepository).saveAll(List.of(step1, step2));
    }

    @Test
    void updateStepsStatus_shouldThrow_whenStepNotFound() {
        Long userId = 1L;
        RoadMap roadMap = RoadMap.builder()
                .id(100L)
                .user(createUser(userId))
                .build();

        List<StepStatusUpdateDto> updates = List.of(
                new StepStatusUpdateDto(1L, StepStatus.DONE)
        );

        when(roadMapRepository.findByUserId(userId)).thenReturn(Optional.of(roadMap));
        when(roadMapStepRepository.findByIdIn(List.of(1L)))
                .thenReturn(List.of());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> roadMapService.updateStepsStatus(userId, updates)
        );

        assertThat(ex.getMessage())
                .contains("Шаг с id 1 не найден");
    }

    @Test
    void updateStepsStatus_shouldThrow_whenStepBelongsToAnotherRoadMap() {
        Long userId = 1L;
        RoadMap roadMap = RoadMap.builder()
                .id(100L)
                .user(createUser(userId))
                .build();

        RoadMap other = RoadMap.builder()
                .id(200L)
                .user(createUser(2L))
                .build();

        RoadMapStep alienStep = RoadMapStep.builder()
                .id(1L)
                .roadMap(other)
                .status(StepStatus.PLANNED)
                .build();

        List<StepStatusUpdateDto> updates = List.of(
                new StepStatusUpdateDto(1L, StepStatus.DONE)
        );

        when(roadMapRepository.findByUserId(userId)).thenReturn(Optional.of(roadMap));
        when(roadMapStepRepository.findByIdIn(List.of(1L)))
                .thenReturn(List.of(alienStep));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> roadMapService.updateStepsStatus(userId, updates)
        );

        assertThat(ex.getMessage())
                .contains("Шаг с id 1 не принадлежит дорожной карте данного пользователя");
    }

    // ---------- helpers ----------

    private User createUser(Long id) {
        return User.builder()
                .id(id)
                .login("user" + id)
                .password("pwd")
                .build();
    }

    private Questionnaire createQuestionnaire(User user) {
        Questionnaire q = new Questionnaire();
        q.setId(50L);
        q.setUser(user);
        q.setEntryDate(LocalDate.of(2024, 10, 1));
        q.setPurpose(VisitPurpose.WORK);
        return q;
    }
}
