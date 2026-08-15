package com.trainingload.session;

import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table
(
    name = "session_summary",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_session_source_start",
        columnNames = {"source_filename", "started_at"}
    )
)
public class SessionSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Calendar day of the session (for daily load aggregation later). */
    @Column(nullable = false)
    private LocalDate activityDate;

    @Column(nullable = false)
    private Instant startedAt;

    @Column(nullable = false, length = 64)
    private String sport;

    /** Session timer time in minutes (TRIMP uses minutes). */
    @Column(nullable = false)
    private double durationMinutes;

    private Integer avgHeartRate;
    private Integer maxHeartRate;

    @Column(name = "source_filename", nullable = false, length = 512)
    private String sourceFilename;

    protected SessionSummary()
    {
        // JPA requires a no-arg constructor
    }

    public SessionSummary(
            LocalDate activityDate,
            Instant startedAt,
            String sport,
            double durationMinutes,
            Integer avgHeartRate,
            Integer maxHeartRate,
            String sourceFilename)
    {
        this.activityDate = activityDate;
        this.startedAt = startedAt;
        this.sport = sport;
        this.durationMinutes = durationMinutes;
        this.avgHeartRate = avgHeartRate;
        this.maxHeartRate = maxHeartRate;
        this.sourceFilename = sourceFilename;
    }

    public Long getId()
    {
        return id;
    }

    public LocalDate getActivityDate()
    {
        return activityDate;
    }

    public Instant getStartedAt()
    {
        return startedAt;
    }

    public String getSport()
    {
        return sport;
    }

    public double getDurationMinutes()
    {
        return durationMinutes;
    }

    public Integer getAvgHeartRate()
    {
        return avgHeartRate;
    }

    public Integer getMaxHeartRate()
    {
        return maxHeartRate;
    }

    public String getSourceFilename()
    {
        return sourceFilename;
    }

    @Override
    public String toString()
    {
        return "SessionSummary{id=%d, date=%s, sport=%s, durationMin=%.1f, avgHR=%s, maxHR=%s, file=%s}"
                .formatted(id, activityDate, sport, durationMinutes, avgHeartRate, maxHeartRate, sourceFilename);
    }
}
