package com.bit.galleog.runtracker.domain;

import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;

/**
 * Entity for user's runs.
 *
 * @author Oleg_Galkin
 */
@Getter
public final class Run {
    /**
     * Run's identifier.
     */
    private final long id;

    /**
     * User who ran.
     */
    private final long userId;
    /**
     * Distance in meters the user ran.
     */
    private final Integer distance;
    /**
     * Datetime and coordinates where the user started running.
     */
    private RunPoint startPoint;
    /**
     * Datetime and coordinates where the user finished the run.
     */
    private RunPoint finishPoint;

    @Builder
    private Run(long id, long userId, @NonNull RunPoint startPoint,
                @Nullable RunPoint finishPoint, @Nullable Integer distance) {
        this.id = id;
        this.userId = userId;
        setStartPoint(startPoint);
        setFinishPoint(finishPoint);
        this.distance = distance;
    }

    /**
     * Gets the average speed of the finished run in km/h.
     */
    public Double getAvgSpeed() {
        if (this.finishPoint != null) {
            var speed = (double) this.distance / (double) ChronoUnit.SECONDS.between(
                    this.startPoint.getDatetime(), this.finishPoint.getDatetime()
            ) * 3.6;
            return BigDecimal.valueOf(speed).setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        } else {
            return null;
        }
    }

    private void setStartPoint(RunPoint point) {
        Validate.notNull(point);
        this.startPoint = point;
    }

    private void setFinishPoint(RunPoint point) {
        if (point != null && !point.getDatetime().isAfter(startPoint.getDatetime())) {
            throw new IllegalArgumentException("Finish datetime must be after start datetime");
        }

        this.finishPoint = point;
    }

    @Override
    public String toString() {
        var builder = new ToStringBuilder(this)
                .append("id", id)
                .append("user", getUserId())
                .append("started", getStartPoint().getDatetime());

        if (finishPoint != null) {
            builder.append("finished", getFinishPoint().getDatetime());
        }

        return builder.build();
    }
}
