package com.hydro.casting.server.ejb.main.replication;

public abstract class BaseCacheReplicator
{
    protected abstract String getReplicationName();

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
        final String replicationDisabledS = System.getProperty( "casting.cache.replication.disabled", "false" );
        return replicationDisabledS != null && replicationDisabledS.equalsIgnoreCase( "true" );
    }

    private boolean isThisDisabled()
    {
        final String replicationDisabledS = System.getProperty( "casting." + getReplicationName() + ".cache.replication.disabled", "false" );
        return replicationDisabledS != null && replicationDisabledS.equalsIgnoreCase( "true" );
    }

    private boolean isThisEnabled()
    {
        final String replicationEnabledS = System.getProperty( "casting." + getReplicationName() + ".cache.replication.enabled", "false" );
        return replicationEnabledS != null && replicationEnabledS.equalsIgnoreCase( "true" );
    }
}