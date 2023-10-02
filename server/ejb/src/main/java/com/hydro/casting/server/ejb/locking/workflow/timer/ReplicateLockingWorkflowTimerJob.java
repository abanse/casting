package com.hydro.casting.server.ejb.locking.workflow.timer;

import com.hydro.casting.server.ejb.locking.workflow.replication.LockingWorkflowCacheReplicator;

import javax.ejb.*;

@Singleton
public class ReplicateLockingWorkflowTimerJob
{
    @EJB
    private LockingWorkflowCacheReplicator lockingWorkflowCacheReplicator;

    @Lock( LockType.READ )
    @Schedule( second = "0", minute = "*", hour = "*", persistent = false )
    public void doWork() throws InterruptedException
    {
        final String replicationDisabledS = System.getProperty( "mes.locking.workflow.cache.replication.disabled", "true" );
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
            lockingWorkflowCacheReplicator.replicate();
        }
    }

}