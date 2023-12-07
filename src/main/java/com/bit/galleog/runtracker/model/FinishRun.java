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
 * Information to finish a run.
 *
 * @author Oleg_Galkin
 */
@Getter
@JsonIgnoreProperties("point")
@JsonDeserialize(builder = FinishRun.FinishRunBuilder.class)
public final class FinishRun {
    /**
     * Datetime and coordinates where the user finished the run.
     */
    private final RunPoint point;

    /**
     * Optional distance in meters the user ran.
     */
    private final Integer distance;

    @Builder
    private FinishRun(@NonNull LocalDateTime datetime, @NonNull BigDecimal latitude,
                      @NonNull BigDecimal longitude, @Nullable Integer distance) {
        this.point = RunPoint.builder()
                .datetime(datetime)
                .latitude(latitude)
                .longitude(longitude)
                .build();
        this.distance = distance;
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
    public static final class FinishRunBuilder {
    }
}
