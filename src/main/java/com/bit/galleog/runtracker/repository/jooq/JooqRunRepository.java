package com.bit.galleog.runtracker.repository.jooq;

import static com.bit.galleog.runtracker.domain.Sequences.RUNS_SEQ;
import static com.bit.galleog.runtracker.domain.Tables.RUNS;
import static org.jooq.impl.DSL.avg;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.localDateTimeDiff;
import static org.jooq.impl.DSL.sum;
import static org.jooq.impl.DSL.val;

import com.bit.galleog.runtracker.domain.Run;
import com.bit.galleog.runtracker.domain.RunPoint;
import com.bit.galleog.runtracker.domain.RunStats;
import com.bit.galleog.runtracker.domain.tables.records.RunsRecord;
import com.bit.galleog.runtracker.repository.RunRepository;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.DatePart;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link RunRepository}.
 *
 * @author Oleg_Galkin
 */
@Repository
@RequiredArgsConstructor
public class JooqRunRepository implements RunRepository {
    private final DSLContext ctx;

    @Override
    @Transactional
    public Run startRun(long userId, @NonNull RunPoint point) {
        var record = ctx.insertInto(RUNS)
                .columns(
                        RUNS.ID,
                        RUNS.USER_ID,
                        RUNS.START_DATETIME,
                        RUNS.START_LATITUDE,
                        RUNS.START_LONGITUDE
                ).values(
                        RUNS_SEQ.nextval(),
                        val(userId),
                        val(point.getDatetime()),
                        val(point.getLatitude()),
                        val(point.getLongitude())
                ).returning()
                .fetchOneInto(RunsRecord.class);

        return toRun(record);
    }

    @Override
    @Transactional
    public Optional<Run> finishRun(long id, @NonNull RunPoint point, int distance) {
        return ctx.update(RUNS)
                .set(RUNS.FINISH_DATETIME, point.getDatetime())
                .set(RUNS.FINISH_LATITUDE, point.getLatitude())
                .set(RUNS.FINISH_LONGITUDE, point.getLongitude())
                .set(RUNS.DISTANCE, distance)
                .where(RUNS.ID.eq(id))
                .returning()
                .fetchOptionalInto(RunsRecord.class)
                .map(this::toRun);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Run> getByIdForUpdate(long id) {
        return ctx.selectFrom(RUNS)
                .where(RUNS.ID.eq(id))
                .forUpdate()
                .fetchOptionalInto(RunsRecord.class)
                .map(this::toRun);
    }

    @Override
    public List<Run> getRunsByUser(long userId, LocalDateTime fromDatetime,
                                   LocalDateTime toDatetime) {
        var condition = RUNS.USER_ID.eq(userId).and(RUNS.FINISH_DATETIME.isNotNull());
        if (fromDatetime != null) {
            condition = condition.and(RUNS.START_DATETIME.ge(fromDatetime));
        }
        if (toDatetime != null) {
            condition = condition.and(RUNS.START_DATETIME.le(toDatetime));
        }

        return ctx.selectFrom(RUNS)
                .where(condition)
                .orderBy(RUNS.START_DATETIME)
                .fetchInto(RunsRecord.class)
                .stream()
                .map(this::toRun)
                .toList();
    }

    @Override
    public RunStats getStatsByUser(long userId, LocalDateTime fromDatetime,
                                   LocalDateTime toDatetime) {
        var condition = RUNS.USER_ID.eq(userId).and(RUNS.FINISH_DATETIME.isNotNull());
        if (fromDatetime != null) {
            condition = condition.and(RUNS.START_DATETIME.ge(fromDatetime));
        }
        if (toDatetime != null) {
            condition = condition.and(RUNS.START_DATETIME.le(toDatetime));
        }

        var record = ctx.select(
                        count(RUNS.ID),
                        sum(RUNS.DISTANCE),
                        avg(RUNS.DISTANCE.divide(localDateTimeDiff(
                                DatePart.SECOND, RUNS.START_DATETIME, RUNS.FINISH_DATETIME
                        )))
                ).from(RUNS)
                .where(condition)
                .fetchOne();

        return RunStats.builder()
                .count(record != null && record.get(0) != null ? (int) record.get(0) : 0)
                .distance(record != null && record.get(1) != null ?
                        ((BigDecimal) record.get(1)).intValue() : 0
                ).avgSpeed(
                        record != null && record.get(2) != null ?
                                ((BigDecimal) record.get(2)).multiply(BigDecimal.valueOf(3.6D))
                                        .setScale(2, RoundingMode.HALF_UP).doubleValue()
                                : 0.00D
                ).build();
    }

    private Run toRun(RunsRecord record) {
        var startPoint = RunPoint.builder()
                .datetime(record.getStartDatetime())
                .latitude(record.getStartLatitude())
                .longitude(record.getStartLongitude())
                .build();

        var finishPoint = record.getFinishDatetime() != null ?
                RunPoint.builder()
                        .datetime(record.getFinishDatetime())
                        .latitude(record.getFinishLatitude())
                        .longitude(record.getFinishLongitude())
                        .build() :
                null;

        return Run.builder()
                .id(record.getId())
                .userId(record.getUserId())
                .startPoint(startPoint)
                .finishPoint(finishPoint)
                .distance(record.getDistance())
                .build();
    }
}
