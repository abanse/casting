package com.hydro.casting.server.ejb.sap.cleaner;

import com.hydro.casting.server.ejb.main.cleaner.BaseCleaner;
import com.hydro.casting.server.ejb.main.replication.BaseCacheReplicator;
import com.hydro.casting.server.ejb.stock.service.SlabService;
import com.hydro.casting.server.ejb.stock.service.StockMaterialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;

@Singleton
public class StockMaterialCleaner extends BaseCleaner
{
    private final static Logger log = LoggerFactory.getLogger( StockMaterialCleaner.class );

    @EJB
    private StockMaterialService stockMaterialService;

    @Override
    protected String getCleanerName()
    {
        return "stockMaterial";
    }

    @Lock( LockType.READ )
    @Schedule( second = "0", minute = "15,45", hour = "*", persistent = false )
    public void replicate() throws InterruptedException
    {
        if ( !shouldRun() )
        {
            return;
        }

        long start = System.currentTimeMillis();
        log.debug( "cleaner start for " + getCleanerName() );

        stockMaterialService.consumeOldStocks();

        log.debug( "cleaner end for " + getCleanerName() + " duration " + ( System.currentTimeMillis() - start ) + "ms" );
    }
}