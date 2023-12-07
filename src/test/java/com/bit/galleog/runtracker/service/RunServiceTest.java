package com.bit.galleog.runtracker.service;

import static com.bit.galleog.runtracker.TestFixtures.DISTANCE;
import static com.bit.galleog.runtracker.TestFixtures.FINISHED_RUN_JOHN_SMITH_1;
import static com.bit.galleog.runtracker.TestFixtures.FINISHED_RUN_JOHN_SMITH_2;
import static com.bit.galleog.runtracker.TestFixtures.FINISH_POINT_1;
import static com.bit.galleog.runtracker.TestFixtures.FINISH_POINT_2;
import static com.bit.galleog.runtracker.TestFixtures.FINISH_STARTED_RUN;
import static com.bit.galleog.runtracker.TestFixtures.FROM_DATETIME;
import static com.bit.galleog.runtracker.TestFixtures.JOHN_SMITH;
import static com.bit.galleog.runtracker.TestFixtures.RUN_STATS;
import static com.bit.galleog.runtracker.TestFixtures.STARTED_RUN_JOHN_SMITH_1;
import static com.bit.galleog.runtracker.TestFixtures.START_POINT_1;
import static com.bit.galleog.runtracker.TestFixtures.TO_DATETIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bit.galleog.runtracker.domain.Run;
import com.bit.galleog.runtracker.domain.RunPoint;
import com.bit.galleog.runtracker.domain.RunStats;
import com.bit.galleog.runtracker.repository.RunRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

/**
 * Tests for {@link RunService}.
 *
 * @author Oleg_Galkin
 */
@ExtendWith(MockitoExtension.class)
class RunServiceTest {
    @Mock
    private RunRepository repository;
    private RunService service;

    @BeforeEach
    void setUp() {
        service = new RunService(repository);
    }

    @Test
    void shouldStartRun() {
        var run = Run.builder()
                .id(1L)
                .userId(JOHN_SMITH.getId())
                .startPoint(START_POINT_1)
                .build();

        when(repository.startRun(JOHN_SMITH.getId(), START_POINT_1)).thenReturn(run);

        var created = service.startRun(JOHN_SMITH.getId(), START_POINT_1);

        assertThat(created).extracting(
                Run::getId,
                Run::getUserId,
                Run::getStartPoint,
                Run::getFinishPoint,
                Run::getDistance
        ).containsExactly(
                run.getId(),
                JOHN_SMITH.getId(),
                START_POINT_1,
                null,
                null
        );

        verify(repository).startRun(JOHN_SMITH.getId(), START_POINT_1);
    }

    @Test
    void shouldFinishRun() {
        when(repository.getByIdForUpdate(STARTED_RUN_JOHN_SMITH_1.getId()))
                .thenReturn(Optional.of(STARTED_RUN_JOHN_SMITH_1));
        when(repository.finishRun(STARTED_RUN_JOHN_SMITH_1.getUserId(), FINISH_POINT_1, DISTANCE))
                .thenReturn(Optional.of(FINISH_STARTED_RUN));

        var optional =
                service.finishRun(STARTED_RUN_JOHN_SMITH_1.getId(), FINISH_POINT_1, DISTANCE);

        assertThat(optional).isPresent();
        assertThat(optional.get()).extracting(
                Run::getId,
                Run::getUserId,
                Run::getStartPoint,
                Run::getFinishPoint,
                Run::getDistance
        ).containsExactly(
                FINISH_STARTED_RUN.getId(),
                FINISH_STARTED_RUN.getUserId(),
                FINISH_STARTED_RUN.getStartPoint(),
                FINISH_STARTED_RUN.getFinishPoint(),
                FINISH_STARTED_RUN.getDistance()
        );

        verify(repository).getByIdForUpdate(STARTED_RUN_JOHN_SMITH_1.getId());
        verify(repository).finishRun(STARTED_RUN_JOHN_SMITH_1.getUserId(), FINISH_POINT_1, DISTANCE);
    }

    @Test
    void shouldNotFinishRunIfItDoesNotExist() {
        when(repository.getByIdForUpdate(STARTED_RUN_JOHN_SMITH_1.getId()))
                .thenReturn(Optional.empty());

        assertThat(service.finishRun(STARTED_RUN_JOHN_SMITH_1.getId(), FINISH_POINT_1, DISTANCE))
                .isEmpty();

        verify(repository).getByIdForUpdate(STARTED_RUN_JOHN_SMITH_1.getId());
        verify(repository, never()).finishRun(anyLong(), any(RunPoint.class), anyInt());
    }

    @Test
    void shouldNotFinishAlreadyFinishedRun() {
        when(repository.getByIdForUpdate(FINISHED_RUN_JOHN_SMITH_1.getId()))
                .thenReturn(Optional.of(FINISHED_RUN_JOHN_SMITH_1));

        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() ->
                service.finishRun(FINISHED_RUN_JOHN_SMITH_1.getId(), FINISH_POINT_1, null)
        );

        verify(repository).getByIdForUpdate(FINISHED_RUN_JOHN_SMITH_1.getId());
        verify(repository, never()).finishRun(anyLong(), any(RunPoint.class), anyInt());
    }

    @Test
    void shouldNotFinishRunWithInvalidFinishPoint() {
        when(repository.getByIdForUpdate(STARTED_RUN_JOHN_SMITH_1.getId()))
                .thenReturn(Optional.of(STARTED_RUN_JOHN_SMITH_1));

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
                service.finishRun(STARTED_RUN_JOHN_SMITH_1.getId(), FINISH_POINT_2, null)
        );

        verify(repository).getByIdForUpdate(STARTED_RUN_JOHN_SMITH_1.getId());
        verify(repository, never()).finishRun(anyLong(), any(RunPoint.class), anyInt());
    }

    @Test
    void shouldGetRunsByUser() {
        when(repository.getRunsByUser(JOHN_SMITH.getId(), FROM_DATETIME, TO_DATETIME))
                .thenReturn(List.of(FINISHED_RUN_JOHN_SMITH_1, FINISHED_RUN_JOHN_SMITH_2));

        var runs = service.getRunsByUser(JOHN_SMITH.getId(), FROM_DATETIME, TO_DATETIME);

        assertThat(runs).extracting(
                Run::getId,
                Run::getUserId,
                Run::getStartPoint,
                Run::getFinishPoint,
                Run::getDistance
        ).containsExactly(
                tuple(
                        FINISHED_RUN_JOHN_SMITH_1.getId(),
                        FINISHED_RUN_JOHN_SMITH_1.getUserId(),
                        FINISHED_RUN_JOHN_SMITH_1.getStartPoint(),
                        FINISHED_RUN_JOHN_SMITH_1.getFinishPoint(),
                        FINISHED_RUN_JOHN_SMITH_1.getDistance()
                ),
                tuple(
                        FINISHED_RUN_JOHN_SMITH_2.getId(),
                        FINISHED_RUN_JOHN_SMITH_2.getUserId(),
                        FINISHED_RUN_JOHN_SMITH_2.getStartPoint(),
                        FINISHED_RUN_JOHN_SMITH_2.getFinishPoint(),
                        FINISHED_RUN_JOHN_SMITH_2.getDistance()
                )
        );

        verify(repository).getRunsByUser(JOHN_SMITH.getId(), FROM_DATETIME, TO_DATETIME);
    }

    @Test
    void shouldGetStatsByUser() {
        when(repository.getStatsByUser(JOHN_SMITH.getId(), FROM_DATETIME, TO_DATETIME))
                .thenReturn(RUN_STATS);

        var stats = service.getStatsByUser(JOHN_SMITH.getId(), FROM_DATETIME, TO_DATETIME);

        assertThat(stats).extracting(
                RunStats::getCount, RunStats::getDistance, RunStats::getAvgSpeed
        ).containsExactly(
                RUN_STATS.getCount(), RUN_STATS.getDistance(), RUN_STATS.getAvgSpeed()
        );

        verify(repository).getStatsByUser(JOHN_SMITH.getId(), FROM_DATETIME, TO_DATETIME);
    }
}