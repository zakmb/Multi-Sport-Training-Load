package com.trainingload.fit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

class FitFileParserTest 
{
    private final FitFileParser parser = new FitFileParser();

    @Test
    void parsesSampleRun()
    {
        List<ParsedSession> sessions = parser.parse(Path.of("sample-data/sample_run.fit"));

        assertFalse(sessions.isEmpty());
        ParsedSession session = sessions.getFirst();
        System.out.println(session.summaryLine());

        assertTrue(session.sport().contains("run"), () -> "sport=" + session.sport());
        assertTrue(session.durationMinutes() > 0);
        assertNotNull(session.avgHeartRate());
        assertNotNull(session.maxHeartRate());
        assertTrue(session.maxHeartRate() >= session.avgHeartRate());
        assertNotNull(session.activityDate());
    }

    @Test
    void parsesSampleBikeAndSwim()
    {
        ParsedSession bike = parser.parse(Path.of("sample-data/sample_bike.fit")).getFirst();
        ParsedSession swim = parser.parse(Path.of("sample-data/sample_swim.fit")).getFirst();

        System.out.println(bike.summaryLine());
        System.out.println(swim.summaryLine());

        assertTrue(bike.sport().contains("cycl") || bike.sport().contains("bike"),
                () -> "bike sport=" + bike.sport());
        assertTrue(swim.sport().contains("swim"), () -> "swim sport=" + swim.sport());
        assertTrue(bike.durationMinutes() > 0);
        assertTrue(swim.durationMinutes() > 0);
    }
}
