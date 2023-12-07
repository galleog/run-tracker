package com.bit.galleog.runtracker;

import static com.bit.galleog.runtracker.domain.Sex.FEMALE;
import static com.bit.galleog.runtracker.domain.Sex.MALE;
import static com.bit.galleog.runtracker.domain.Sex.NONBINARY;

import com.bit.galleog.runtracker.domain.Run;
import com.bit.galleog.runtracker.domain.RunPoint;
import com.bit.galleog.runtracker.domain.RunStats;
import com.bit.galleog.runtracker.domain.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

/**
 * Objects commonly used in all tests.
 *
 * @author Oleg_Galkin
 */
public abstract class TestFixtures {
    public static final User JOHN_SMITH = User.builder()
            .id(100L)
            .firstName("John")
            .lastName("Smith")
            .birthDate(LocalDate.of(1985, Month.AUGUST, 2))
            .sex(MALE)
            .build();
    public static final User BARBARA_MOORE = User.builder()
            .id(200L)
            .firstName("Barbara")
            .lastName("Moore")
            .birthDate(LocalDate.of(1994, Month.DECEMBER, 23))
            .sex(FEMALE)
            .build();
    public static final User ELAINE_JOHNSON = User.builder()
            .id(300L)
            .firstName("Elaine")
            .lastName("Johnson")
            .birthDate(LocalDate.of(1999, Month.FEBRUARY, 12))
            .sex(NONBINARY)
            .build();
    public static final User NEW_USER = User.builder()
            .firstName(JOHN_SMITH.getFirstName())
            .lastName(JOHN_SMITH.getLastName())
            .birthDate(JOHN_SMITH.getBirthDate())
            .sex(JOHN_SMITH.getSex())
            .build();

    public static final int DISTANCE = 6548;

    public static final LocalDateTime FROM_DATETIME = LocalDateTime.of(2023, 11, 1, 0, 0);
    public static final LocalDateTime TO_DATETIME = LocalDateTime.of(2023, 11, 30, 23, 59);

    public static final RunPoint START_POINT_1 = RunPoint.builder()
            .datetime(LocalDateTime.of(2023, 11, 8, 11, 23, 34))
            .latitude(new BigDecimal("41.644035"))
            .longitude(new BigDecimal("41.633785"))
            .build();
    public static final RunPoint START_POINT_3 = RunPoint.builder()
            .datetime(LocalDateTime.of(2023, 11, 1, 10, 11, 12))
            .latitude(new BigDecimal("41.651804"))
            .longitude(new BigDecimal("41.632970"))
            .build();
    public static final RunPoint START_POINT_4 = RunPoint.builder()
            .datetime(LocalDateTime.of(2023, 10, 14, 7, 15, 36))
            .latitude(new BigDecimal("42.358675"))
            .longitude(new BigDecimal("-71.062843"))
            .build();
    public static final RunPoint START_POINT_5 = RunPoint.builder()
            .datetime(LocalDateTime.of(2023, 11, 3, 10, 55, 7))
            .latitude(new BigDecimal("41.6611800"))
            .longitude(new BigDecimal("41.630090"))
            .build();
    public static final RunPoint FINISH_POINT_1 = RunPoint.builder()
            .datetime(START_POINT_1.getDatetime().plusHours(2))
            .latitude(new BigDecimal("41.626743"))
            .longitude(new BigDecimal("41.586784"))
            .build();
    public static final RunPoint FINISH_POINT_3 = RunPoint.builder()
            .datetime(START_POINT_3.getDatetime().plusHours(1))
            .latitude(new BigDecimal("41.642879"))
            .longitude(new BigDecimal("41.616279"))
            .build();
    public static final Run FINISHED_RUN_JOHN_SMITH_2 = Run.builder()
            .id(300L)
            .userId(JOHN_SMITH.getId())
            .startPoint(START_POINT_3)
            .finishPoint(FINISH_POINT_3)
            .distance(3632)
            .build();
    public static final RunPoint FINISH_POINT_4 = RunPoint.builder()
            .datetime(START_POINT_4.getDatetime().plusHours(1))
            .latitude(new BigDecimal("42.353308"))
            .longitude(new BigDecimal("-71.069003"))
            .build();
    public static final Run FINISHED_RUN_BARBARA_MOORE_1 = Run.builder()
            .id(400L)
            .userId(BARBARA_MOORE.getId())
            .startPoint(START_POINT_4)
            .finishPoint(FINISH_POINT_4)
            .distance(1961)
            .build();
    public static final RunPoint FINISH_POINT_5 = RunPoint.builder()
            .datetime(START_POINT_5.getDatetime().plusHours(1))
            .latitude(new BigDecimal("41.656782"))
            .longitude(new BigDecimal("41.638923"))
            .build();
    public static final Run FINISHED_RUN_JOHN_SMITH_3 = Run.builder()
            .id(500L)
            .userId(JOHN_SMITH.getId())
            .startPoint(START_POINT_5)
            .finishPoint(FINISH_POINT_5)
            .distance(1587)
            .build();
    public static final Run STARTED_RUN_JOHN_SMITH_1 = Run.builder()
            .id(100L)
            .userId(JOHN_SMITH.getId())
            .startPoint(START_POINT_1)
            .build();
    public static final RunStats RUN_STATS = RunStats.builder()
            .count(187)
            .distance(1023)
            .avgSpeed(4.3)
            .build();
    private static final RunPoint START_POINT_2 = RunPoint.builder()
            .datetime(LocalDateTime.of(2023, 9, 30, 10, 17, 54))
            .latitude(new BigDecimal("41.649239"))
            .longitude(new BigDecimal("41.632172"))
            .build();
    public static final RunPoint FINISH_POINT_2 = RunPoint.builder()
            .datetime(START_POINT_2.getDatetime().plusHours(2))
            .latitude(new BigDecimal("41.620070"))
            .longitude(new BigDecimal("41.589219"))
            .build();
    public static final Run FINISHED_RUN_JOHN_SMITH_1 = Run.builder()
            .id(200L)
            .userId(JOHN_SMITH.getId())
            .startPoint(START_POINT_2)
            .finishPoint(FINISH_POINT_2)
            .distance(DISTANCE)
            .build();

    public static final Run FINISH_STARTED_RUN = Run.builder()
            .id(STARTED_RUN_JOHN_SMITH_1.getId())
            .userId(STARTED_RUN_JOHN_SMITH_1.getUserId())
            .startPoint(STARTED_RUN_JOHN_SMITH_1.getStartPoint())
            .finishPoint(FINISH_POINT_1)
            .distance(DISTANCE)
            .build();
}
