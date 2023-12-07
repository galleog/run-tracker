package com.bit.galleog.runtracker.model;

import com.bit.galleog.runtracker.domain.RunPoint;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Information to start a run.
 *
 * @author Oleg_Galkin
 */
@Getter
@JsonIgnoreProperties("point")
@JsonDeserialize(builder = StartRun.StartRunBuilder.class)
public final class StartRun {
    /**
     * Optional run's identifier.
     */
    private final Long id;

    /**
     * Identifier of the User who ran.
     */
    private final long userId;

    /**
     * Datetime and coordinates where the user started running.
     */
    private final RunPoint point;

    @Builder
    private StartRun(@Nullable Long id, long userId, @NonNull LocalDateTime datetime,
                     @NonNull BigDecimal latitude, @NonNull BigDecimal longitude) {
        this.id = id;
        this.userId = userId;
        this.point = RunPoint.builder()
                .datetime(datetime)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }

    /**
     * Gets the datetime when the user started running.
     */
    public LocalDateTime getDatetime() {
        return this.point.getDatetime();
    }

    /**
     * Gets the latitude where the user started running.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public BigDecimal getLatitude() {
        return this.point.getLatitude();
    }

    /**
     * Gets the longitude where the user started running.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public BigDecimal getLongitude() {
        return this.point.getLongitude();
    }

    @JsonPOJOBuilder(withPrefix = StringUtils.EMPTY)
    public static final class StartRunBuilder {
    }
}
