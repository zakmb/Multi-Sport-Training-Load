package com.trainingload.fit;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public record ParsedSession
(
        LocalDate activityDate,
        Instant startedAt,
        String sport,
        double durationMinutes,
        Integer avgHeartRate,
        Integer maxHeartRate,
        String sourceFilename
){

    public String summaryLine()
    {
        return "sport=%s date=%s durationMin=%.1f avgHR=%s maxHR=%s file=%s"
                .formatted(sport, activityDate, durationMinutes, avgHeartRate, maxHeartRate, sourceFilename);
    }

    static LocalDate toLocalDate(Instant instant)
    {
        return instant.atZone(ZoneOffset.UTC).toLocalDate();
    }
}
