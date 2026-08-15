package com.trainingload.session;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionSummaryRepository extends JpaRepository<SessionSummary, Long> 
{
    Optional<SessionSummary> findBySourceFilenameAndStartedAt(
            String sourceFilename, java.time.Instant startedAt);

    List<SessionSummary> findAllByOrderByStartedAtAsc();
}
