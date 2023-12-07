package com.bit.galleog.runtracker.domain;

import static com.bit.galleog.runtracker.TestFixtures.JOHN_SMITH;
import static com.bit.galleog.runtracker.TestFixtures.STARTED_RUN_JOHN_SMITH_1;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Tests for {@link Run}.
 *
 * @author Oleg_Galkin
 */
class RunTest {
    @Test
    void shouldCalcAvgSpeed() {
        assertThat(STARTED_RUN_JOHN_SMITH_1.getAvgSpeed()).isNull();

        var diffSecs = 30;

        var startPoint = RunPoint.builder()
                .datetime(LocalDateTime.now())
                .latitude(BigDecimal.ZERO)
                .longitude(BigDecimal.ZERO)
                .build();
        var finishPoint = RunPoint.builder()
                .datetime(startPoint.getDatetime().plusSeconds(diffSecs))
                .latitude(BigDecimal.ZERO)
                .longitude(BigDecimal.ZERO)
                .build();

        var run = Run.builder()
                .id(1L)
                .userId(JOHN_SMITH.getId())
                .startPoint(startPoint)
                .finishPoint(finishPoint)
                .distance(200)
                .build();

        assertThat(run.getAvgSpeed()).isEqualTo(
                (double) run.getDistance() / (double) diffSecs * 3.6
        );
    }
}