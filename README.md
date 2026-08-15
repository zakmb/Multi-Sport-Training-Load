# Multi-Sport Training Load

Personal project: one fitness/fatigue curve across all sport. Built because COROS already does a decent job unifying endurance sports via TRIMP, but their own docs admit that pipeline falls over on anaerobic/neuromuscular work — and hockey sits squarely in that gap.

---

## Design note — which load formula, for which sport, and why

I need a single daily load number that can feed Banister-style CTL / ATL / TSB.
Different sports get that number different ways.

### Swim / bike / run → Banister TRIMP (HR-based)

For anything with a usable heart-rate stream in the FIT file I use Banister
TRIMP:

```
%HRR = (HRavg − HRrest) / (HRmax − HRrest)
y     = 0.64 × e^(k × %HRR)     # k = 1.92 male / 1.67 female
TRIMP = duration_min × %HRR × y
```

It's the same family of metric COROS / TrainingPeaks lean on for endurance
load, so the numbers should feel familiar when I compare against EvoLab later.
This may later be fine tuned by calculating per lap to account for intervals etc.

### Field hockey → Foster session-RPE

Hockey is intermittent, shift-based, a lot of anaerobic punch. Session-average
HR (even if I wore a strap) would miss most of that. Initial development will use:

```
load = RPE (0–10) × duration_minutes
```

This will need fine tuning, may end up using a combination of this and the TRIMP score. 

### Daily aggregation → CTL / ATL / TSB

Once every session has a load score, sum (rest day = 0) and
roll the TrainingPeaks-style EWMA:

```
CTL_today = CTL_yesterday + (load_today − CTL_yesterday) / 42   # fitness
ATL_today = ATL_yesterday + (load_today − ATL_yesterday) / 7    # fatigue
TSB_today = CTL_yesterday − ATL_yesterday                       # form
```



---

## Extra Notes

Initial development will look into aerobic sport values, this will allow me to compare to what Coros states my values are and ensure these are very similar

## Run it
```bash
mvn spring-boot:run
```

# parse + persist everything in sample-data/
curl -u admin:changeme -X POST http://localhost:8080/sessions/ingest

# list what landed in the DB
curl -u admin:changeme http://localhost:8080/sessions
```

Then open http://localhost:8080/h2-console — JDBC URL `jdbc:h2:file:./data/trainingload`, user `sa`, password same as `TRAININGLOAD_DB_PASSWORD` (default `changeme`).

```bash
mvn test
```

Peek at a FIT file:

```bash
java -jar tools/FitCSVTool.jar sample-data/sample_run.fit
```

## Status
Phase 0 — Spring Boot + FIT SDK + design note  
Phase 1 — FIT ingest → `SessionSummary` in H2 (`FitFileParser`, `/sessions/ingest`)

## Sources I'm working from

- Banister TRIMP — [trainingimpulse.com](https://www.trainingimpulse.com/banisters-trimp-0)
- Foster sRPE review — [Frontiers / Haddad et al. 2017](https://www.frontiersin.org/journals/neuroscience/articles/10.3389/fnins.2017.00612/full)
- CTL/ATL/TSB — [paincave.io](https://www.paincave.io/blog/ctl-atl-tsb-explained)
- Garmin FIT SDK — [developer.garmin.com/fit](https://developer.garmin.com/fit), [fit-java-sdk](https://github.com/garmin/fit-java-sdk)
- COROS on why HR load breaks down for anaerobic work — [coros.com training load](https://coros.com/stories/coros-metrics/c/training-load)
