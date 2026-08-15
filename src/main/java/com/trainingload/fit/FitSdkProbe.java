package com.trainingload.fit;

import com.garmin.fit.Decode;
import com.garmin.fit.MesgNum;

/**
 * this class is just scaffolding.
 */
public final class FitSdkProbe 
{
    private FitSdkProbe()
    {
        // private constructor to prevent instantiation
    }

    /** Confirms Decode + message constants resolve against the FIT SDK jar. */
    public static String sdkMarker()
    {
        Decode decode = new Decode();
        return "fit-sdk-ok decode=" + decode.getClass().getSimpleName()
                + " sessionMsg=" + MesgNum.SESSION;
    }
}
