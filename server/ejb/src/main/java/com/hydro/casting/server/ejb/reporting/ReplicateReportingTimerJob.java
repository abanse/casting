package com.hydro.casting.server.ejb.reporting;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.ejb.reporting.data.ReportingDataProviderDowntime;

import javax.ejb.*;

@Singleton
public class ReplicateReportingTimerJob
{
    //@formatter:off
    private static String[] REPLICATED_COST_CENTERS = new String[] {
            Casting.MACHINE.CASTER_50,
            Casting.MACHINE.CASTER_60,
            Casting.MACHINE.CASTER_70,
            Casting.MACHINE.CASTER_80,
            Casting.MACHINE.MELTING_FURNACE_51,
            Casting.MACHINE.MELTING_FURNACE_52,
            Casting.MACHINE.MELTING_FURNACE_61,
            Casting.MACHINE.MELTING_FURNACE_62,
            Casting.MACHINE.MELTING_FURNACE_71,
            Casting.MACHINE.MELTING_FURNACE_72,
            Casting.MACHINE.MELTING_FURNACE_81,
            Casting.MACHINE.MELTING_FURNACE_82
    };
    //@formatter:on

    @EJB
    private ReportingDowntimeCacheReplicator reportingDowntimeCacheReplicator;

    //    @EJB
    //    private ReportingOutputCacheReplicator reportingOutputCacheReplicator;

    @EJB
    private ReportingSummaryCacheReplicator reportingSummaryCacheReplicator;

    @EJB
    private ReportingDataProviderDowntime reportingDataProviderDowntime;

    //    @EJB
    //    private ReportingDataProviderOutput reportingDataProviderOutput;

    @Lock( LockType.READ )
    @Schedule( second = "12", minute = "*/1", hour = "*", persistent = false )
    public void doWork() throws InterruptedException
    {
        final String replicationDisabledS = System.getProperty( "mes.reporting.cache.replication.disabled" );
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
            reportingDowntimeCacheReplicator.replicateDetailedDays();
            reportingDataProviderDowntime.loadAllDetailDTOsFromCache();
            //reportingDataProviderOutput.loadAllDetailDTOsFromCache();
            for ( String replicatedCostCenter : REPLICATED_COST_CENTERS )
            {
                String costCenter = replicatedCostCenter;
                String machine = "";
                if ( replicatedCostCenter.length() > 2 )
                {
                    costCenter = replicatedCostCenter.substring( 0, 2 );
                    machine = replicatedCostCenter.substring( 2 );
                }
                reportingSummaryCacheReplicator.replicateSummaries( costCenter, machine );
            }
        }
    }

    @Lock( LockType.READ )
    @Schedule( second = "0", minute = "0", hour = "0", persistent = false )
    public void clearCache() throws InterruptedException
    {
        final String replicationDisabledS = System.getProperty( "mes.reporting.cache.replication.disabled" );
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
            reportingDowntimeCacheReplicator.setClearFlag( true );
            //reportingOutputCacheReplicator.setClearFlag( true );
        }
    }

    //    @Lock( LockType.READ )
    //    @Schedule( second = "42", minute = "*/1", hour = "*", persistent = false )
    //    @TransactionTimeout( value = 600, unit = TimeUnit.SECONDS )
    //    public void outputReplication() throws InterruptedException
    //    {
    //        final String replicationDisabledS = System.getProperty( "bde.reporting.cache.replication.disabled" );
    //        final boolean replicationDisabled;
    //        if ( replicationDisabledS != null && replicationDisabledS.equalsIgnoreCase( "true" ) )
    //        {
    //            replicationDisabled = true;
    //        }
    //        else
    //        {
    //            replicationDisabled = false;
    //        }
    //        if ( !replicationDisabled )
    //        {
    //            reportingOutputCacheReplicator.replicateDetailedDays( QueryType.SLITTER );
    //            reportingOutputCacheReplicator.replicateDetailedDays( QueryType.ANNEALING );
    //            reportingOutputCacheReplicator.replicateDetailedDays( QueryType.CUTLENGTH );
    //            reportingOutputCacheReplicator.replicateDetailedDays( QueryType.COLDMILL );
    //            reportingOutputCacheReplicator.replicateDetailedDays( QueryType.HOTMILL );
    //            reportingOutputCacheReplicator.replicateDetailedDays( QueryType.SCALPER );
    //            reportingOutputCacheReplicator.replicateDetailedDays( QueryType.BRAZING );
    //            reportingOutputCacheReplicator.replicateDetailedDays( QueryType.PACKING );
    //            reportingOutputCacheReplicator.setLastDate( DateTimeUtil.asDate( LocalDate.now() ) );
    //        }
    //    }

}