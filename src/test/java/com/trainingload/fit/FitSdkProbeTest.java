package com.trainingload.fit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FitSdkProbeTest 
{
    @Test
    void fitSdkIsOnClasspath()
    {
        String marker = FitSdkProbe.sdkMarker();
        assertTrue(marker.startsWith("fit-sdk-ok"), () -> "unexpected marker: " + marker);
    }
}
