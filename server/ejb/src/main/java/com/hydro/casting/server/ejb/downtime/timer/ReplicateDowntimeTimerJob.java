package com.hydro.casting.server.ejb.downtime.timer;

import com.hydro.casting.common.downtime.DowntimeConstants;
import com.hydro.casting.server.ejb.downtime.replication.DowntimeCacheReplicator;

import javax.ejb.*;

/**
 * This timer runs every five seconds and DowntimeDTOs are replicated into the bde cache.
 */
@Singleton
public class ReplicateDowntimeTimerJob
{
    private int index = 0;

    @EJB
    private DowntimeCacheReplicator downtimeCacheReplicator;

    @Lock( LockType.READ )
    @Schedule( second = "*/5", minute = "*", hour = "*", persistent = false )
    public void doWork() throws InterruptedException
    {
        final String replicationDisabledS = System.getProperty( "mes.downtime.cache.replication.disabled" );
        final boolean replicationDisabled;
        if ( replicationDisabledS != null && replicationDisabledS.equalsIgnoreCase( "true" ) )
        {
            replicationDisabled = true;
        }
        else
        {
            replicationDisabled = false;
        }
        if ( !replicationDisabled )
        {
            downtimeCacheReplicator.replicate( DowntimeConstants.REPLICATION.REPLICATED_COST_CENTERS[index] );
        }

        index++;
        if ( index >= DowntimeConstants.REPLICATION.REPLICATED_COST_CENTERS.length )
        {
            index = 0;
        }
    }

}