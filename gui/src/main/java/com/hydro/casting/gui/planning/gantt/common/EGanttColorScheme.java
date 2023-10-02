package com.hydro.casting.gui.planning.gantt.common;

public enum EGanttColorScheme
{
    TRANSFER( "Ofenbezogen" ), BATCH( "Batchbezogen" );

    private final String caption;

    EGanttColorScheme( String caption )
    {
        this.caption = caption;
    }

    public String toString()
    {
        return caption;
    }
}
