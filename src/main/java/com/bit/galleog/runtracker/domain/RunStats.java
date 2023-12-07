package com.bit.galleog.runtracker.domain;

import lombok.Builder;
import lombok.Getter;

/**
 * Model class to return statistics for runs.
 *
 * @author Oleg_Galkin
 */
@Builder
@Getter
public class RunStats {
    /**
     * Overall count of runs.
     */
    private final int count;

    /**
     * Overall distance a user ran.
     */
    private final int distance;

    /**
     * Average speed in km/h.
     */
    private final double avgSpeed;
}
