package com.trainingload.fit;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.garmin.fit.Decode;
import com.garmin.fit.FitRuntimeException;
import com.garmin.fit.MesgBroadcaster;
import com.garmin.fit.SessionMesg;
import com.garmin.fit.SessionMesgListener;
import com.garmin.fit.Sport;

/**
 * Decodes a .fit activity using the SDK's MesgBroadcaster + SessionMesgListener
 * pattern (same idea as Garmin's "decoding activity files" cookbook).
 */
@Component
public class FitFileParser
{
    public List<ParsedSession> parse(Path fitFile)
    {
        if (fitFile == null || !Files.isRegularFile(fitFile))
        {
            throw new FitParseException("Not a readable FIT file: " + fitFile);
        }

        String filename = fitFile.getFileName().toString();
        List<SessionMesg> sessions = new ArrayList<>();

        Decode decode = new Decode();
        MesgBroadcaster broadcaster = new MesgBroadcaster(decode);
        SessionMesgListener listener = sessions::add;
        broadcaster.addListener(listener);

        try (InputStream in = new BufferedInputStream(Files.newInputStream(fitFile)))
        {
            if (!decode.read(in, broadcaster, broadcaster))
            {
                throw new FitParseException("FIT decode failed for " + filename);
            }
        }
        catch (FitRuntimeException | IOException e)
        {
            throw new FitParseException("Could not parse " + filename + ": " + e.getMessage(), e);
        }

        if (sessions.isEmpty())
        {
            throw new FitParseException("No session message in " + filename);
        }

        List<ParsedSession> parsed = new ArrayList<>(sessions.size());
        for (SessionMesg session : sessions)
        {
            parsed.add(toParsed(session, filename));
        }
        return parsed;
    }

    private static ParsedSession toParsed(SessionMesg session, String filename)
    {
        Instant startedAt = toInstant(session);
        double durationMinutes = toDurationMinutes(session);
        String sport = toSportName(session.getSport());
        Integer avgHr = toInteger(session.getAvgHeartRate());
        Integer maxHr = toInteger(session.getMaxHeartRate());

        return new ParsedSession(
                ParsedSession.toLocalDate(startedAt),
                startedAt,
                sport,
                durationMinutes,
                avgHr,
                maxHr,
                filename);
    }

    private static Instant toInstant(SessionMesg session)
    {
        Date date = null;
        if (session.getStartTime() != null)
        {
            date = session.getStartTime().getDate();
        }
        else if (session.getTimestamp() != null)
        {
            date = session.getTimestamp().getDate();
        }
        if (date == null)
        {
            throw new FitParseException("Session has no start time / timestamp");
        }
        return date.toInstant();
    }

    private static double toDurationMinutes(SessionMesg session)
    {
        Float seconds = session.getTotalTimerTime();
        if (seconds == null || seconds <= 0f)
        {
            throw new FitParseException("Session has no usable total_timer_time");
        }
        return seconds / 60.0;
    }

    private static String toSportName(Sport sport)
    {
        if (sport == null)
        {
            return "unknown";
        }
        return sport.name().toLowerCase(Locale.ROOT);
    }

    private static Integer toInteger(Short value)
    {
        return value == null ? null : value.intValue();
    }
}
