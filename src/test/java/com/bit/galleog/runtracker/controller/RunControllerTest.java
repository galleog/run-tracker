package com.bit.galleog.runtracker.controller;

import static com.bit.galleog.runtracker.TestFixtures.DISTANCE;
import static com.bit.galleog.runtracker.TestFixtures.FINISHED_RUN_JOHN_SMITH_1;
import static com.bit.galleog.runtracker.TestFixtures.FINISHED_RUN_JOHN_SMITH_2;
import static com.bit.galleog.runtracker.TestFixtures.FINISHED_RUN_JOHN_SMITH_3;
import static com.bit.galleog.runtracker.TestFixtures.FINISH_POINT_1;
import static com.bit.galleog.runtracker.TestFixtures.FINISH_STARTED_RUN;
import static com.bit.galleog.runtracker.TestFixtures.FROM_DATETIME;
import static com.bit.galleog.runtracker.TestFixtures.JOHN_SMITH;
import static com.bit.galleog.runtracker.TestFixtures.RUN_STATS;
import static com.bit.galleog.runtracker.TestFixtures.STARTED_RUN_JOHN_SMITH_1;
import static com.bit.galleog.runtracker.TestFixtures.START_POINT_1;
import static com.bit.galleog.runtracker.TestFixtures.TO_DATETIME;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bit.galleog.runtracker.model.FinishRun;
import com.bit.galleog.runtracker.model.StartRun;
import com.bit.galleog.runtracker.service.RunService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

/**
 * Tests for {@link RunController}
 *
 * @author Oleg_Galkin
 */
@ActiveProfiles("test")
@WebMvcTest(RunController.class)
@ExtendWith({
        SpringExtension.class,
        MockitoExtension.class
})
class RunControllerTest {
    private static final String BASE_URL = "/runs";

    private static final FinishRun FINISH_RUN = FinishRun.builder()
            .datetime(FINISH_POINT_1.getDatetime())
            .latitude(FINISH_POINT_1.getLatitude())
            .longitude(FINISH_POINT_1.getLongitude())
            .distance(DISTANCE)
            .build();

    @MockBean
    private RunService service;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldStartRun() throws Exception {
        var run = StartRun.builder()
                .userId(JOHN_SMITH.getId())
                .datetime(START_POINT_1.getDatetime())
                .latitude(START_POINT_1.getLatitude())
                .longitude(START_POINT_1.getLongitude())
                .build();

        when(service.startRun(JOHN_SMITH.getId(), STARTED_RUN_JOHN_SMITH_1.getStartPoint()))
                .thenReturn(STARTED_RUN_JOHN_SMITH_1);

        mockMvc.perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(run))
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(STARTED_RUN_JOHN_SMITH_1.getId()))
                .andExpect(jsonPath("$.userId")
                        .value(STARTED_RUN_JOHN_SMITH_1.getUserId()))
                .andExpect(jsonPath("$.datetime")
                        .value(STARTED_RUN_JOHN_SMITH_1.getStartPoint().getDatetime().toString()))
                .andExpect(jsonPath("$.latitude")
                        .value(STARTED_RUN_JOHN_SMITH_1.getStartPoint().getLatitude().toString()))
                .andExpect(jsonPath("$.longitude")
                        .value(STARTED_RUN_JOHN_SMITH_1.getStartPoint().getLongitude().toString()));

        verify(service).startRun(JOHN_SMITH.getId(), STARTED_RUN_JOHN_SMITH_1.getStartPoint());
    }

    @Test
    void shouldFinishRun() throws Exception {
        when(service.finishRun(STARTED_RUN_JOHN_SMITH_1.getId(), FINISH_POINT_1, DISTANCE))
                .thenReturn(Optional.of(FINISH_STARTED_RUN));

        mockMvc.perform(
                        patch(BASE_URL + "/" + STARTED_RUN_JOHN_SMITH_1.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(FINISH_RUN))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(STARTED_RUN_JOHN_SMITH_1.getId()))
                .andExpect(jsonPath("$.userId").value(FINISH_STARTED_RUN.getUserId()))
                .andExpect(jsonPath("$.startPoint.datetime")
                        .value(FINISH_STARTED_RUN.getStartPoint().getDatetime().toString()))
                .andExpect(jsonPath("$.startPoint.latitude")
                        .value(FINISH_STARTED_RUN.getStartPoint().getLatitude().toString()))
                .andExpect(jsonPath("$.startPoint.longitude")
                        .value(FINISH_STARTED_RUN.getStartPoint().getLongitude().toString()))
                .andExpect(jsonPath("$.finishPoint.datetime")
                        .value(FINISH_STARTED_RUN.getFinishPoint().getDatetime().toString()))
                .andExpect(jsonPath("$.finishPoint.latitude")
                        .value(FINISH_STARTED_RUN.getFinishPoint().getLatitude().toString()))
                .andExpect(jsonPath("$.finishPoint.longitude")
                        .value(FINISH_STARTED_RUN.getFinishPoint().getLongitude().toString()))
                .andExpect(jsonPath("$.distance")
                        .value(FINISH_STARTED_RUN.getDistance()));

        verify(service).finishRun(STARTED_RUN_JOHN_SMITH_1.getId(), FINISH_POINT_1, DISTANCE);
    }

    @Test
    void shouldNotFinishRunIfItDoesNotExist() throws Exception {
        var id = 23L;

        when(service.finishRun(id, FINISH_POINT_1, DISTANCE))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                patch(BASE_URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(FINISH_RUN))
        ).andExpect(status().isNotFound());

        verify(service).finishRun(id, FINISH_POINT_1, DISTANCE);
    }

    @Test
    void shouldNotFinishAlreadyFinishedRun() throws Exception {
        when(service.finishRun(FINISHED_RUN_JOHN_SMITH_1.getId(), FINISH_POINT_1, DISTANCE))
                .thenThrow(IllegalStateException.class);

        mockMvc.perform(
                patch(BASE_URL + "/" + FINISHED_RUN_JOHN_SMITH_1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(FINISH_RUN))
        ).andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotFinishRunWithInvalidFinishPoint() throws Exception {
        when(service.finishRun(STARTED_RUN_JOHN_SMITH_1.getId(), FINISH_POINT_1, DISTANCE))
                .thenThrow(IllegalArgumentException.class);

        mockMvc.perform(
                patch(BASE_URL + "/" + STARTED_RUN_JOHN_SMITH_1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(FINISH_RUN))
        ).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnRunsByUser() throws Exception {
        when(service.getRunsByUser(JOHN_SMITH.getId(), FROM_DATETIME, TO_DATETIME))
                .thenReturn(List.of(FINISHED_RUN_JOHN_SMITH_2, FINISHED_RUN_JOHN_SMITH_3));

        mockMvc.perform(
                        get(BASE_URL)
                                .param("userId", String.valueOf(JOHN_SMITH.getId()))
                                .param("fromDatetime", FROM_DATETIME.toString())
                                .param("toDatetime", TO_DATETIME.toString())
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(FINISHED_RUN_JOHN_SMITH_2.getId()))
                .andExpect(jsonPath("$[0].userId")
                        .value(FINISHED_RUN_JOHN_SMITH_2.getUserId()))
                .andExpect(jsonPath("$[0].startPoint.datetime")
                        .value(FINISHED_RUN_JOHN_SMITH_2.getStartPoint().getDatetime().toString()))
                .andExpect(jsonPath("$[0].startPoint.latitude")
                        .value(FINISHED_RUN_JOHN_SMITH_2.getStartPoint().getLatitude().toString()))
                .andExpect(jsonPath("$[0].startPoint.longitude")
                        .value(FINISHED_RUN_JOHN_SMITH_2.getStartPoint().getLongitude().toString()))
                .andExpect(jsonPath("$[0].finishPoint.datetime")
                        .value(FINISHED_RUN_JOHN_SMITH_2.getFinishPoint().getDatetime().toString()))
                .andExpect(jsonPath("$[0].finishPoint.latitude")
                        .value(FINISHED_RUN_JOHN_SMITH_2.getFinishPoint().getLatitude().toString()))
                .andExpect(jsonPath("$[0].finishPoint.longitude")
                        .value(FINISHED_RUN_JOHN_SMITH_2.getFinishPoint().getLongitude().toString()))
                .andExpect(jsonPath("$[0].distance")
                        .value(FINISHED_RUN_JOHN_SMITH_2.getDistance()))
                .andExpect(jsonPath("$[0].avgSpeed")
                        .value(FINISHED_RUN_JOHN_SMITH_2.getAvgSpeed()))
                .andExpect(jsonPath("$[1].id").value(FINISHED_RUN_JOHN_SMITH_3.getId()))
                .andExpect(jsonPath("$[1].userId")
                        .value(FINISHED_RUN_JOHN_SMITH_3.getUserId()))
                .andExpect(jsonPath("$[1].startPoint.datetime")
                        .value(FINISHED_RUN_JOHN_SMITH_3.getStartPoint().getDatetime().toString()))
                .andExpect(jsonPath("$[1].startPoint.latitude")
                        .value(FINISHED_RUN_JOHN_SMITH_3.getStartPoint().getLatitude().toString()))
                .andExpect(jsonPath("$[1].startPoint.longitude")
                        .value(FINISHED_RUN_JOHN_SMITH_3.getStartPoint().getLongitude().toString()))
                .andExpect(jsonPath("$[1].finishPoint.datetime")
                        .value(FINISHED_RUN_JOHN_SMITH_3.getFinishPoint().getDatetime().toString()))
                .andExpect(jsonPath("$[1].finishPoint.latitude")
                        .value(FINISHED_RUN_JOHN_SMITH_3.getFinishPoint().getLatitude().toString()))
                .andExpect(jsonPath("$[1].finishPoint.longitude")
                        .value(FINISHED_RUN_JOHN_SMITH_3.getFinishPoint().getLongitude().toString()))
                .andExpect(jsonPath("$[1].distance")
                        .value(FINISHED_RUN_JOHN_SMITH_3.getDistance()))
                .andExpect(jsonPath("$[1].avgSpeed")
                        .value(FINISHED_RUN_JOHN_SMITH_3.getAvgSpeed()));

        verify(service).getRunsByUser(JOHN_SMITH.getId(), FROM_DATETIME, TO_DATETIME);
    }

    @Test
    void shouldReturnStatsByUser() throws Exception {
        when(service.getStatsByUser(JOHN_SMITH.getId(), FROM_DATETIME, TO_DATETIME))
                .thenReturn(RUN_STATS);

        mockMvc.perform(
                        get(BASE_URL + "/stats")
                                .param("userId", String.valueOf(JOHN_SMITH.getId()))
                                .param("fromDatetime", FROM_DATETIME.toString())
                                .param("toDatetime", TO_DATETIME.toString())
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(RUN_STATS.getCount()))
                .andExpect(jsonPath("$.distance").value(RUN_STATS.getDistance()))
                .andExpect(jsonPath("$.avgSpeed").value(RUN_STATS.getAvgSpeed()));

        verify(service).getStatsByUser(JOHN_SMITH.getId(), FROM_DATETIME, TO_DATETIME);
    }
}