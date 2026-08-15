package com.trainingload.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FitIngestionServiceTest 
{
    @Autowired
    private FitIngestionService fitIngestionService;

    @Autowired
    private SessionSummaryRepository sessionSummaryRepository;

    @Test
    void ingestSampleDataPersistsEveryFile()
    {
        sessionSummaryRepository.deleteAll();

        List<SessionSummary> saved = fitIngestionService.ingestDirectory(Path.of("sample-data"));

        assertEquals(3, saved.size(), "expected one session per sample FIT file");
        assertEquals(3, sessionSummaryRepository.count());

        // re-ingest should not duplicate
        List<SessionSummary> again = fitIngestionService.ingestDirectory(Path.of("sample-data"));
        assertEquals(3, again.size());
        assertEquals(3, sessionSummaryRepository.count());

        List<SessionSummary> all = fitIngestionService.listAll();
        assertFalse(all.isEmpty());
        all.forEach(s ->
        {
            System.out.println(s);
            assertTrue(s.getDurationMinutes() > 0);
            assertNotBlank(s.getSport());
            assertNotBlank(s.getSourceFilename());
        });
    }

    private static void assertNotBlank(String value)
    {
        assertTrue(value != null && !value.isBlank(), "expected non-blank value");
    }
}
