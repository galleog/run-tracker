package com.bit.galleog.runtracker.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.Validate;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Datetime and coordinates of a run.
 *
 * @author Oleg_Galkin
 */
@Getter
@ToString
@EqualsAndHashCode
public final class RunPoint {
    private static final int LATLNG_SCALE = 6;        // scale for latitude and longitude
    private static final int EARTH_RADIUS = 6371000;  // radius of the earth in meters

    /**
     * Datetime of the run.
     */
    private LocalDateTime datetime;

    /**
     * Latitude and longitude of the run.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal latitude;

    /**
     * Longitude of the run.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal longitude;

    @Builder
    private RunPoint(@NonNull LocalDateTime datetime, @NonNull BigDecimal latitude,
                     @NonNull BigDecimal longitude) {
        setDatetime(datetime);
        setLatitude(latitude);
        setLongitude(longitude);
    }

    private void setDatetime(LocalDateTime datetime) {
        Validate.notNull(datetime);
        this.datetime = datetime;
    }

    private void setLatitude(BigDecimal latitude) {
        Validate.notNull(latitude);
        Validate.inclusiveBetween(-90, 90, latitude.doubleValue());

        this.latitude = latitude.setScale(LATLNG_SCALE, RoundingMode.HALF_UP);
    }

    private void setLongitude(BigDecimal longitude) {
        Validate.notNull(longitude);
        Validate.inclusiveBetween(-180, 180, longitude.doubleValue());

        this.longitude = longitude.setScale(LATLNG_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Calculates distance to another point using Haversine method.
     *
     * @param other the point to calculate distance to
     * @return the calculated distance in meters
     */
    public int calcDistanceTo(@NonNull RunPoint other) {
        Validate.notNull(other);

        var lat1 = this.getLatitude().doubleValue();
        var lat2 = other.getLatitude().doubleValue();
        var lng1 = this.getLongitude().doubleValue();
        var lng2 = other.getLongitude().doubleValue();

        var latDistance = Math.toRadians(lat2 - lat1);
        var lngDistance = Math.toRadians(lng2 - lng1);

        var a = haversine(latDistance) + Math.cos(Math.toRadians(lat1)) *
                Math.cos(Math.toRadians(lat2)) * haversine(lngDistance);
        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (int) (EARTH_RADIUS * c);
    }

    private static double haversine(double val) {
        var sin = Math.sin(val / 2);
        return sin * sin;
    }
}
