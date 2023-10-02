package com.hydro.casting.server.ejb.sap.consumer;

import com.hydro.casting.server.ejb.main.service.MaterialService;
import com.hydro.casting.server.model.mat.MaterialType;
import com.hydro.casting.server.model.po.ProductionOrder;
import com.hydro.casting.server.model.po.dao.ProductionOrderHome;
import com.hydro.core.common.exception.BusinessException;
import com.hydro.core.common.util.StringTools;
import com.hydro.eai.sap.contract.consumer.SAPMessageListener;
import com.hydro.eai.sap.contract.message.ORDH_DETAIL;
import com.hydro.eai.sap.contract.message.SAPMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Map;

@Stateless( name = "ORDH_DETAIL" )
public class ORDH_DETAILConsumer implements SAPMessageListener
{
    private final static Logger log = LoggerFactory.getLogger( ORDH_DETAILConsumer.class );

    @EJB
    private ProductionOrderHome productionOrderHome;

    @EJB
    private MaterialService materialService;

    @Override
    public String getConsumerName()
    {
        return "ORDH_DETAIL";
    }

    @Override
    public void consume( Map<String, String> context, SAPMessage message ) throws BusinessException
    {
        log.debug( "ORDH_DETAIL consumer " + message.getArt() );
        final ORDH_DETAIL ordhDetail = new ORDH_DETAIL();
        ordhDetail.fillMessage( message.getBody().getBytes() );

        final String aufNr = ordhDetail.getAufNr();
        ProductionOrder productionOrder = productionOrderHome.findByApk( aufNr );

        if ( productionOrder == null )
        {
            productionOrder = new ProductionOrder();
            productionOrder.setApk( aufNr );
            productionOrderHome.persist( productionOrder );
        }
        productionOrder.setKind( ordhDetail.getAuArt() );
        productionOrder.setAmount( ordhDetail.getbMenge() );
        productionOrder.setUnit( ordhDetail.getBemeins() );
        productionOrder.setErpCharge( ordhDetail.getCharge() );
        productionOrder.setStartTS( ordhDetail.getGstrsTS() );
        productionOrder.setEndTS( ordhDetail.getGltrsTS() );
        productionOrder.setDescription( StringTools.uncomment( ordhDetail.getMakTx() ) );

        final MaterialType materialType = materialService.findOrCreateMaterialType( ordhDetail.getStlBez() );
        productionOrder.setMaterialType( materialType );
    }
}
