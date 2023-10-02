package com.hydro.casting.server.ejb.main.cleaner;

public abstract class BaseCleaner
{
    protected abstract String getCleanerName();

    protected boolean shouldRun()
    {
        if ( isGlobalDisabled() )
        {
            return isThisEnabled();
        }
        return !isThisDisabled();
    }

    private boolean isGlobalDisabled()
    {
        final String cleanerDisabledS = System.getProperty( "casting.cleaner.disabled", "false" );
        return cleanerDisabledS != null && cleanerDisabledS.equalsIgnoreCase( "true" );
    }

    private boolean isThisDisabled()
    {
        final String cleanerDisabledS = System.getProperty( "casting." + getCleanerName() + ".cleaner.disabled", "false" );
        return cleanerDisabledS != null && cleanerDisabledS.equalsIgnoreCase( "true" );
    }

    private boolean isThisEnabled()
    {
        final String cleanerEnabledS = System.getProperty( "casting." + getCleanerName() + ".cleaner.enabled", "false" );
        return cleanerEnabledS != null && cleanerEnabledS.equalsIgnoreCase( "true" );
    }
}