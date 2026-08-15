package com.trainingload.web;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trainingload.session.FitIngestionService;
import com.trainingload.session.SessionSummary;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    private final FitIngestionService fitIngestionService;
    private final Path defaultFitDir;

    public SessionController(
            FitIngestionService fitIngestionService,
            @Value("${trainingload.fit.training-data-dir:training-data}") String defaultFitDir)
    {
        this.fitIngestionService = fitIngestionService;
        this.defaultFitDir = Path.of(defaultFitDir);
    }

    /** Ingest all .fit files from a folder (defaults to training-data/). */
    @PostMapping("/ingest")
    public Map<String, Object> ingest(
            @RequestParam(value = "dir", required = false) String dir)
    {
        Path folder = (dir == null || dir.isBlank()) ? defaultFitDir : Path.of(dir);
        List<SessionSummary> saved = fitIngestionService.ingestDirectory(folder);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("directory", folder.toAbsolutePath().normalize().toString());
        body.put("count", saved.size());
        body.put("sessions", saved);
        return body;
    }

    @GetMapping
    public List<SessionSummary> list()
    {
        return fitIngestionService.listAll();
    }
}
