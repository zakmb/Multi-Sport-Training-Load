package com.trainingload.session;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trainingload.fit.FitFileParser;
import com.trainingload.fit.ParsedSession;

@Service
public class FitIngestionService 
{
    private static final Logger log = LoggerFactory.getLogger(FitIngestionService.class);

    private final FitFileParser fitFileParser;
    private final SessionSummaryRepository sessionSummaryRepository;

    public FitIngestionService(
            FitFileParser fitFileParser, SessionSummaryRepository sessionSummaryRepository)
    {
        this.fitFileParser = fitFileParser;
        this.sessionSummaryRepository = sessionSummaryRepository;
    }

    /**
     * Parse every .fit under {@code directory} and upsert into the DB.
     * Prints a one-line summary per session (handy when running ingest from curl).
     */
    @Transactional
    public List<SessionSummary> ingestDirectory(Path directory)
    {
        if (directory == null || !Files.isDirectory(directory))
        {
            throw new IllegalArgumentException("Not a directory: " + directory);
        }

        List<Path> fitFiles = listFitFiles(directory);
        List<SessionSummary> saved = new ArrayList<>();

        for (Path fitFile : fitFiles)
        {
            List<ParsedSession> parsed = fitFileParser.parse(fitFile);
            for (ParsedSession session : parsed)
            {
                log.info("parsed {}", session.summaryLine());
                saved.add(upsert(session));
            }
        }

        return saved;
    }

    public List<SessionSummary> listAll()
    {
        return sessionSummaryRepository.findAllByOrderByStartedAtAsc();
    }

    private SessionSummary upsert(ParsedSession parsed)
    {
        return sessionSummaryRepository
                .findBySourceFilenameAndStartedAt(parsed.sourceFilename(), parsed.startedAt())
                .orElseGet(() -> sessionSummaryRepository.save(toEntity(parsed)));
    }

    private static SessionSummary toEntity(ParsedSession parsed)
    {
        return new SessionSummary(
                parsed.activityDate(),
                parsed.startedAt(),
                parsed.sport(),
                parsed.durationMinutes(),
                parsed.avgHeartRate(),
                parsed.maxHeartRate(),
                parsed.sourceFilename());
    }

    private static List<Path> listFitFiles(Path directory)
    {
        try (Stream<Path> stream = Files.list(directory))
        {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".fit"))
                    .sorted(Comparator.comparing(p -> p.getFileName().toString().toLowerCase(Locale.ROOT)))
                    .toList();
        }
        catch (IOException e)
        {
            throw new IllegalStateException("Could not list FIT files in " + directory, e);
        }
    }
}
