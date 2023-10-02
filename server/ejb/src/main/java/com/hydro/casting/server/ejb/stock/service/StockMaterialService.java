package com.hydro.casting.server.ejb.stock.service;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.ejb.main.service.MaterialService;
import com.hydro.casting.server.model.mat.StockMaterial;
import com.hydro.casting.server.model.mat.dao.StockMaterialHome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class StockMaterialService //extends BaseMaterialService<StockMaterial, MaterialDTO>
{
    private final static Logger log = LoggerFactory.getLogger( StockMaterialService.class );

    @EJB
    private StockMaterialHome stockMaterialHome;

    @EJB
    private MaterialService materialService;

    public void consumeOldStocks()
    {
        log.info( "consumeOldStocks" );
        final LocalDateTime newestReplicationTS = stockMaterialHome.findNewestReplicationTS();
        if ( newestReplicationTS == null )
        {
            log.info( "no stocks present" );
            return;
        }

        final List<StockMaterial> consumedMaterials = stockMaterialHome.findConsumedStockMaterials( newestReplicationTS.minusMinutes( 15 ) );
        log.info( "found " + consumedMaterials.size() + " consumed Materials" );
        for ( StockMaterial consumedMaterial : consumedMaterials )
        {
            consumedMaterial.setHandlingUnit( null );
            materialService.setConsumptionState( consumedMaterial, Casting.SCHEDULABLE_STATE.SUCCESS );
        }
    }
}