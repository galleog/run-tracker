package com.bit.galleog.runtracker.controller;

import com.bit.galleog.runtracker.domain.Run;
import com.bit.galleog.runtracker.domain.RunStats;
import com.bit.galleog.runtracker.model.FinishRun;
import com.bit.galleog.runtracker.model.StartRun;
import com.bit.galleog.runtracker.service.RunService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller to work with user's runs.
 *
 * @author Oleg_Galkin
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/runs")
public class RunController {
    private final RunService service;

    /**
     * Starts a new run.
     *
     * @param run the attributes of the run to be started
     * @return the newly created run
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public StartRun startRun(@RequestBody StartRun run) {
        var started = service.startRun(run.getUserId(), run.getPoint());
        return StartRun.builder()
                .id(started.getId())
                .userId(started.getUserId())
                .datetime(started.getStartPoint().getDatetime())
                .latitude(started.getStartPoint().getLatitude())
                .longitude(started.getStartPoint().getLongitude())
                .build();
    }

    /**
     * Finishes the run specified by the given identifier.
     *
     * @param id  the run identifier
     * @param run the information to finish the run
     * @return the finished ru or {@link HttpStatus#NOT_FOUND}
     * if there is no started run with the specified identifier
     */
    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Run> finishRun(@PathVariable("id") long id, @RequestBody FinishRun run) {
        try {
            return service.finishRun(id, run.getPoint(), run.getDistance())
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.error("Finish run request failed", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
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
    @GetMapping
    public List<Run> getRunsByUser(
            @RequestParam("userId") long userId,
            @RequestParam(name = "fromDatetime", required = false) LocalDateTime fromDatetime,
            @RequestParam(name = "toDatetime", required = false) LocalDateTime toDatetime
    ) {
        return service.getRunsByUser(userId, fromDatetime, toDatetime);
    }

    /**
     * Gets statistics fromDatetimeincluding runs of the user specified by their identifier.
     *
     * @param userId       the user's identifier
     * @param fromDatetime the optional datetime to cover only runs started after
     * @param toDatetime   the optional datetime to cover only runs started before
     * @return the statistics for all runs started from {@code fromDatetime} to {@code toDatetime}
     */
    @GetMapping(path = "/stats")
    public RunStats getStatsByUser(
            @RequestParam("userId") long userId,
            @RequestParam(name = "fromDatetime", required = false) LocalDateTime fromDatetime,
            @RequestParam(name = "toDatetime", required = false) LocalDateTime toDatetime
    ) {
        return service.getStatsByUser(userId, fromDatetime, toDatetime);
    }
}
