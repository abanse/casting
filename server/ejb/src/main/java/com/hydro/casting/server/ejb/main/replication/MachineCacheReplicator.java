package com.hydro.casting.server.ejb.main.replication;

import com.hydro.casting.server.ejb.main.service.MachineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;

@Singleton
public class MachineCacheReplicator extends BaseCacheReplicator
{
    private final static Logger log = LoggerFactory.getLogger( MachineCacheReplicator.class );

    @EJB
    private MachineService machineService;

    @Override
    protected String getReplicationName()
    {
        return "machine";
    }

    @Lock( LockType.READ )
    @Schedule( second = "5,15,25,35,45,55", minute = "*", hour = "*", persistent = false )
    public void replicate() throws InterruptedException
    {
        if ( !shouldRun() )
        {
            return;
        }

        long start = System.currentTimeMillis();
        log.debug( "replication start for " + getReplicationName() );

        machineService.writeCompleteCache();

        log.debug( "replication end for " + getReplicationName() + " duration " + ( System.currentTimeMillis() - start ) + "ms" );
    }
}
