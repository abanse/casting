package com.hydro.casting.server.ejb.sap.consumer;

import com.hydro.casting.server.ejb.main.service.ProductionOrderService;
import com.hydro.casting.server.model.po.ProductionOrder;
import com.hydro.casting.server.model.po.WorkStep;
import com.hydro.casting.server.model.po.dao.ProductionOrderHome;
import com.hydro.casting.server.model.po.dao.WorkStepHome;
import com.hydro.casting.server.model.res.Plant;
import com.hydro.casting.server.model.res.dao.PlantHome;
import com.hydro.core.common.exception.BusinessException;
import com.hydro.core.common.exception.BusinessObjectNotFoundException;
import com.hydro.core.common.util.StringTools;
import com.hydro.eai.sap.contract.consumer.SAPMessageListener;
import com.hydro.eai.sap.contract.message.ORDH_OPER;
import com.hydro.eai.sap.contract.message.SAPMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Map;

@Stateless( name = "ORDH_OPER" )
public class ORDH_OPERConsumer implements SAPMessageListener
{
    private final static Logger log = LoggerFactory.getLogger( ORDH_OPERConsumer.class );

    @EJB
    private ProductionOrderHome productionOrderHome;

    @EJB
    private WorkStepHome workStepHome;

    @EJB
    private PlantHome plantHome;

    @EJB
    private ProductionOrderService productionOrderService;

    @Override
    public String getConsumerName()
    {
        return "ORDH_OPER";
    }

    @Override
    public void consume( Map<String, String> context, SAPMessage message ) throws BusinessException
    {
        log.debug( "ORDH_OPER consumer " + message.getArt() );
        final ORDH_OPER ordhOper = new ORDH_OPER();
        ordhOper.fillMessage( message.getBody().getBytes() );

        final String aufNr = ordhOper.getAufNr();
        ProductionOrder productionOrder = productionOrderHome.findByApk( aufNr );
        if ( productionOrder == null )
        {
            throw new BusinessObjectNotFoundException( "ProductionOrder ", aufNr );
        }

        final String vorgangsNr = ordhOper.getVorNr();
        final String workStepApk = aufNr + "-" + vorgangsNr;
        WorkStep workStep = workStepHome.findByApk( workStepApk );

        if ( workStep == null )
        {
            workStep = new WorkStep();
            workStep.setApk( workStepApk );
            workStepHome.persist( workStep );
        }
        workStep.setProductionOrder( productionOrder );

        workStep.setWorkStepNumber( vorgangsNr );
        workStep.setStartTS( ordhOper.getDateBTS() );
        workStep.setEndTS( ordhOper.getDateETS() );
        workStep.setDescription( StringTools.uncomment( ordhOper.getLtxa1() ) );
        workStep.setWorkPlace( ordhOper.getArbpl() );
        workStep.setWorkPlaceDescription( StringTools.uncomment( ordhOper.getLtxa2() ) );

        final Plant plant = plantHome.findByApk( ordhOper.getWerks() );
        if ( plant == null )
        {
            throw new BusinessObjectNotFoundException( "Plant ", ordhOper.getWerks() );
        }
        workStep.setPlant( plant );

        // Finde zugehörigen geplanten Demand
        if ( "0030".equals( vorgangsNr ) && ordhOper.getArbpl() != null && ordhOper.getArbpl().startsWith( "ASBA" ) )
        {
            productionOrderService.assignDemand( workStep );
        }
    }
}
