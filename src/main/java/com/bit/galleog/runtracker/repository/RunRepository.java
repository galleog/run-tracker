package com.bit.galleog.runtracker.repository;

import com.bit.galleog.runtracker.domain.Run;
import com.bit.galleog.runtracker.domain.RunPoint;
import com.bit.galleog.runtracker.domain.RunStats;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Run}.
 *
 * @author Oleg_Galkin
 */
public interface RunRepository {
    /**
     * Starts a new run.
     *
     * @param userId the user who runs
     * @param point  the datetime and coordinates where the user started running
     * @return the newly created run
     */
    Run startRun(long userId, @NonNull RunPoint point);

    /**
     * Finishes the run specified by its identifier.
     *
     * @param id       the run identifier
     * @param point    the point when and where the run was finished
     * @param distance the distance the user ran
     * @return the updated run or {@link Optional#empty()}
     * if there is no run with the specified identifier
     */
    Optional<Run> finishRun(long id, @NonNull RunPoint point, int distance);

    /**
     * Gets a run by its identifier and locks it pessimistically (SELECT... FOR UPDATE).
     *
     * @param id the run identifier
     * @return the run with the given identifier, or {@link Optional#empty()}
     * if there is no run with that identifier
     */
    Optional<Run> getByIdForUpdate(long id);

    /**
     * Gets filtered finished runs of the specified user.
     *
     * @param userId       the user's identifier
     * @param fromDatetime the optional datetime to cover only runs started after
     * @param toDatetime   the optional datetime to cover only runs started before
     * @return all finished runs of the user started from {@code fromDatetime}
     * to {@code toDatetime}
     */
    List<Run> getRunsByUser(long userId, @Nullable LocalDateTime fromDatetime,
                            @Nullable LocalDateTime toDatetime);

    /**
     * Gets statistics including runs of the user specified by their identifier.
     *
     * @param userId       the user's identifier
     * @param fromDatetime the optional datetime to cover only runs started after
     * @param toDatetime   the optional datetime to cover only runs started before
     * @return the statistics for all runs started from {@code fromDatetime} to {@code toDatetime}
     */
    RunStats getStatsByUser(long userId, @Nullable LocalDateTime fromDatetime,
                            @Nullable LocalDateTime toDatetime);
}
