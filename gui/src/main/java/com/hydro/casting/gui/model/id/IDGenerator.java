package com.hydro.casting.gui.model.id;

public class IDGenerator
{
    private static long last = (long) ( Math.random() * 1000.0 );

    public static synchronized long getNext()
    {
        return last++;
    }
}
