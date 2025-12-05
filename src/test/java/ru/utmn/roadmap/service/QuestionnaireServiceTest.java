package ru.utmn.roadmap.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.utmn.roadmap.domain.entity.Country;
import ru.utmn.roadmap.domain.entity.Questionnaire;
import ru.utmn.roadmap.domain.entity.User;
import ru.utmn.roadmap.domain.entity.enam.ProgramStatus;
import ru.utmn.roadmap.domain.entity.enam.VisitPurpose;
import ru.utmn.roadmap.domain.repository.CountryRepository;
import ru.utmn.roadmap.domain.repository.QuestionnaireRepository;
import ru.utmn.roadmap.domain.repository.UserRepository;
import ru.utmn.roadmap.web.dto.QuestionnaireRequestDto;
import ru.utmn.roadmap.web.dto.QuestionnaireResponseDto;
import ru.utmn.roadmap.web.mapper.QuestionnaireRequestMapper;
import ru.utmn.roadmap.web.mapper.QuestionnaireResponseMapper;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionnaireServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private QuestionnaireRepository questionnaireRepository;

    @Mock
    private QuestionnaireRequestMapper requestMapper;

    @Mock
    private QuestionnaireResponseMapper responseMapper;

    @InjectMocks
    private QuestionnaireServiceImpl questionnaireService;

    // ---------- getQuestionnaireForUser ----------

    @Test
    void getQuestionnaireForUser_shouldThrow_whenUserIdIsNull() {
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> questionnaireService.getQuestionnaireForUser(null)
        );

        assertThat(ex.getMessage()).isEqualTo("Пользователь должен быть авторизован");
    }

    @Test
    void getQuestionnaireForUser_shouldThrow_whenUserNotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> questionnaireService.getQuestionnaireForUser(userId)
        );

        assertThat(ex.getMessage())
                .contains("Пользователь с id 1 не найден");
    }

    @Test
    void getQuestionnaireForUser_shouldReturnDto_whenQuestionnaireExists() {
        Long userId = 1L;

        User user = User.builder()
                .id(userId)
                .login("demo")
                .password("pwd")
                .build();

        Questionnaire questionnaire = Questionnaire.builder()
                .id(10L)
                .user(user)
                .build();

        QuestionnaireResponseDto dto = new QuestionnaireResponseDto(
                questionnaire.getId(),
                LocalDate.of(2024, 10, 1),
                "Россия",
                "WORK",
                "NONE",
                "NONE"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(questionnaireRepository.findByUserId(userId)).thenReturn(Optional.of(questionnaire));
        when(responseMapper.toDto(questionnaire)).thenReturn(dto);

        QuestionnaireResponseDto result = questionnaireService.getQuestionnaireForUser(userId);

        assertThat(result).isEqualTo(dto);
        verify(userRepository).findById(userId);
        verify(questionnaireRepository).findByUserId(userId);
        verify(responseMapper).toDto(questionnaire);
    }

    @Test
    void getQuestionnaireForUser_shouldReturnNull_whenQuestionnaireNotExists() {
        Long userId = 1L;

        User user = User.builder()
                .id(userId)
                .login("demo")
                .password("pwd")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(questionnaireRepository.findByUserId(userId)).thenReturn(Optional.empty());

        QuestionnaireResponseDto result = questionnaireService.getQuestionnaireForUser(userId);

        assertThat(result).isNull();
        verify(userRepository).findById(userId);
        verify(questionnaireRepository).findByUserId(userId);
        verifyNoInteractions(responseMapper);
    }

    // ---------- saveQuestionnaireForUser ----------

    @Test
    void saveQuestionnaireForUser_shouldThrow_whenUserIdIsNull() {
        QuestionnaireRequestDto request = createSampleRequestDto();

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> questionnaireService.saveQuestionnaireForUser(null, request)
        );

        assertThat(ex.getMessage()).isEqualTo("Пользователь должен быть авторизован");
    }

    @Test
    void saveQuestionnaireForUser_shouldThrow_whenUserNotFound() {
        Long userId = 1L;
        QuestionnaireRequestDto request = createSampleRequestDto();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> questionnaireService.saveQuestionnaireForUser(userId, request)
        );

        assertThat(ex.getMessage()).contains("Пользователь с id 1 не найден");
    }

    @Test
    void saveQuestionnaireForUser_shouldThrow_whenCountryNotFound() {
        Long userId = 1L;
        QuestionnaireRequestDto request = createSampleRequestDto();

        User user = User.builder()
                .id(userId)
                .login("demo")
                .password("pwd")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(countryRepository.findById(request.countryId())).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> questionnaireService.saveQuestionnaireForUser(userId, request)
        );

        assertThat(ex.getMessage()).contains("Страна с id");
    }

    @Test
    void saveQuestionnaireForUser_shouldCreateNewQuestionnaire_whenNotExists() {
        Long userId = 1L;
        QuestionnaireRequestDto request = createSampleRequestDto();

        User user = User.builder()
                .id(userId)
                .login("demo")
                .password("pwd")
                .build();

        Country country = Country.builder()
                .id(request.countryId())
                .name("Россия")
                .build();

        Questionnaire newQuestionnaire = Questionnaire.builder()
                .id(null)
                .user(user)
                .citizenship(country)
                .build();

        Questionnaire savedQuestionnaire = Questionnaire.builder()
                .id(10L)
                .user(user)
                .citizenship(country)
                .build();

        QuestionnaireResponseDto dto = new QuestionnaireResponseDto(
                10L,
                request.entryDate(),
                country.getName(),
                request.purpose().name(),
                request.resettlementProgramStatus().name(),
                request.hqSpecialistStatus().name()
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(countryRepository.findById(request.countryId())).thenReturn(Optional.of(country));
        when(questionnaireRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(requestMapper.toEntity(request, country, user)).thenReturn(newQuestionnaire);
        when(questionnaireRepository.save(newQuestionnaire)).thenReturn(savedQuestionnaire);
        when(responseMapper.toDto(savedQuestionnaire)).thenReturn(dto);

        QuestionnaireResponseDto result = questionnaireService.saveQuestionnaireForUser(userId, request);

        assertThat(result).isEqualTo(dto);
        assertThat(user.getQuestionnaire()).isEqualTo(newQuestionnaire); // проверяем, что setQuestionnaire сработал
        verify(requestMapper).toEntity(request, country, user);
        verify(questionnaireRepository).save(newQuestionnaire);
    }

    @Test
    void saveQuestionnaireForUser_shouldUpdateExistingQuestionnaire_whenExists() {
        Long userId = 1L;
        QuestionnaireRequestDto request = createSampleRequestDto();

        User user = User.builder()
                .id(userId)
                .login("demo")
                .password("pwd")
                .build();

        Country country = Country.builder()
                .id(request.countryId())
                .name("Россия")
                .build();

        Questionnaire existing = Questionnaire.builder()
                .id(10L)
                .user(user)
                .citizenship(country)
                .build();

        QuestionnaireResponseDto dto = new QuestionnaireResponseDto(
                existing.getId(),
                request.entryDate(),
                country.getName(),
                request.purpose().name(),
                request.resettlementProgramStatus().name(),
                request.hqSpecialistStatus().name()
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(countryRepository.findById(request.countryId())).thenReturn(Optional.of(country));
        when(questionnaireRepository.findByUserId(userId)).thenReturn(Optional.of(existing));
        when(questionnaireRepository.save(existing)).thenReturn(existing);
        when(responseMapper.toDto(existing)).thenReturn(dto);

        QuestionnaireResponseDto result = questionnaireService.saveQuestionnaireForUser(userId, request);

        assertThat(result).isEqualTo(dto);

        // проверяем, что поля анкеты переписаны из DTO
        assertThat(existing.getEntryDate()).isEqualTo(request.entryDate());
        assertThat(existing.getCitizenship()).isEqualTo(country);
        assertThat(existing.getPurpose()).isEqualTo(request.purpose());
        assertThat(existing.getResettlementProgramStatus()).isEqualTo(request.resettlementProgramStatus());
        assertThat(existing.getHqSpecialistStatus()).isEqualTo(request.hqSpecialistStatus());

        verify(requestMapper, never()).toEntity(any(), any(), any()); // новый не создавался
        verify(questionnaireRepository).save(existing);
    }

    private QuestionnaireRequestDto createSampleRequestDto() {
        return new QuestionnaireRequestDto(
                LocalDate.of(2024, 10, 1),
                1L,
                VisitPurpose.WORK,
                ProgramStatus.NONE,
                ProgramStatus.NONE
        );
    }
}
