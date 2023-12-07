package com.bit.galleog.runtracker.service;

import com.bit.galleog.runtracker.domain.Run;
import com.bit.galleog.runtracker.domain.RunPoint;
import com.bit.galleog.runtracker.domain.RunStats;
import com.bit.galleog.runtracker.repository.RunRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service to work with user's runs.
 *
 * @author Oleg_Galkin
 */
@Service
@RequiredArgsConstructor
public class RunService {
    private final RunRepository repository;

    /**
     * Starts a new run.
     *
     * @param userId the user who runs
     * @param point  the datetime and coordinates where the user started running
     * @return the newly created run
     */
    public Run startRun(long userId, @NonNull RunPoint point) {
        Validate.notNull(point);
        return repository.startRun(userId, point);
    }

    /**
     * Finishes the run specified by its identifier.
     *
     * @param id       the run identifier
     * @param point    the point when and where the run was finished
     * @param distance the optional distance the user ran
     * @return the updated run or {@link Optional#empty()}
     * if there is no run with the specified identifier
     */
    @Transactional
    public Optional<Run> finishRun(long id, @NonNull RunPoint point, @Nullable Integer distance) {
        Validate.notNull(point);

        return repository.getByIdForUpdate(id)
                .flatMap(run -> {
                    if (run.getFinishPoint() != null) {
                        throw new IllegalStateException(
                                "Run with id=" + id + " has been finished"
                        );
                    }
                    if (!point.getDatetime().isAfter(run.getStartPoint().getDatetime())) {
                        throw new IllegalArgumentException(
                                "Finish datetime must be after start datetime"
                        );
                    }

                    // if the distance isn't specified, calculate it
                    var dist = distance != null ?
                            distance : run.getStartPoint().calcDistanceTo(point);
                    return repository.finishRun(id, point, dist);
                });
    }

    /**
     * Gets filtered finished runs of the specified user.
     *
     * @param userId       the user's identifier
     * @param fromDatetime the optional datetime to cover only runs started after
     * @param toDatetime   the optional datetime to cover only runs started before
     * @return all finished runs of the user started from {@code fromDatetime}
     * to {@code toDatetime}
     */
    public List<Run> getRunsByUser(long userId, @Nullable LocalDateTime fromDatetime,
                                   @Nullable LocalDateTime toDatetime) {
        return repository.getRunsByUser(userId, fromDatetime, toDatetime);
    }

    /**
     * Gets statistics including runs of the user specified by their identifier.
     *
     * @param userId       the user's identifier
     * @param fromDatetime the optional datetime to cover only runs started after
     * @param toDatetime   the optional datetime to cover only runs started before
     * @return the statistics for all runs started from {@code fromDatetime} to {@code toDatetime}
     */
    public RunStats getStatsByUser(long userId, @Nullable LocalDateTime fromDatetime,
                                   @Nullable LocalDateTime toDatetime) {
        return repository.getStatsByUser(userId, fromDatetime, toDatetime);
    }
}
