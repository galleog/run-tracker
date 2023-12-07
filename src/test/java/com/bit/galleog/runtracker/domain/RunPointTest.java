package com.bit.galleog.runtracker.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Tests for {@link RunPoint}.
 *
 * @author Oleg_Galkin
 */
class RunPointTest {
    private static final BigDecimal LATITUDE = new BigDecimal("37.9087303");
    private static final BigDecimal SCALED_LATITUDE = new BigDecimal("37.908730");
    private static final BigDecimal LONGITUDE = new BigDecimal("-71.3862015");
    private static final BigDecimal SCALED_LONGITUDE = new BigDecimal("-71.386202");

    @Test
    void shouldSetScaleLatitudeAndLongitude() {
        var point = RunPoint.builder()
                .datetime(LocalDateTime.now())
                .latitude(LATITUDE)
                .longitude(LONGITUDE)
                .build();

        assertThat(point).extracting(
                RunPoint::getLatitude, RunPoint::getLongitude
        ).containsExactly(
                SCALED_LATITUDE, SCALED_LONGITUDE
        );
    }

    @Test
    void shouldCheckLatitudeRange() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
                RunPoint.builder()
                        .datetime(LocalDateTime.now())
                        .latitude(new BigDecimal("-91.902987"))
                        .longitude(SCALED_LONGITUDE)
                        .build()
        );

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
                RunPoint.builder()
                        .datetime(LocalDateTime.now())
                        .latitude(new BigDecimal("90.345000"))
                        .longitude(SCALED_LONGITUDE)
                        .build()
        );
    }

    @Test
    void shouldCheckLongitudeRange() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
                RunPoint.builder()
                        .datetime(LocalDateTime.now())
                        .latitude(SCALED_LATITUDE)
                        .longitude(new BigDecimal("-181.0002"))
                        .build()
        );

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
                RunPoint.builder()
                        .datetime(LocalDateTime.now())
                        .latitude(SCALED_LATITUDE)
                        .longitude(new BigDecimal("180.000001"))
                        .build()
        );
    }

    @Test
    void shouldCalculateDistanceToAnotherPoint() {
        var latNY = new BigDecimal("40.714268");   // New York
        var lngNY = new BigDecimal("-74.005974");
        var pointNY = RunPoint.builder()
                .datetime(LocalDateTime.now())
                .latitude(latNY)
                .longitude(lngNY)
                .build();

        assertThat(pointNY.calcDistanceTo(pointNY)).isEqualTo(0);

        var latLA = new BigDecimal("34.0522");     // Los Angeles
        var lngLA = new BigDecimal("-118.2437");
        var pointLA = RunPoint.builder()
                .datetime(LocalDateTime.now())
                .latitude(latLA)
                .longitude(lngLA)
                .build();

        var expected = 3935740;
        assertThat(pointNY.calcDistanceTo(pointLA)).isCloseTo(expected, Offset.offset(1000));
    }
}