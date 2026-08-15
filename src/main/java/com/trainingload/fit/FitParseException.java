package com.trainingload.fit;

public class FitParseException extends RuntimeException 
{
    public FitParseException(String message)
    {
        super(message);
    }

    public FitParseException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
