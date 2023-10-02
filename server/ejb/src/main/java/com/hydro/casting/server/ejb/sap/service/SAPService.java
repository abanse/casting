package com.hydro.casting.server.ejb.sap.service;

import com.hydro.casting.server.model.mat.Alloy;
import com.hydro.casting.server.model.mat.MaterialType;
import com.hydro.casting.server.model.mat.dao.MaterialTypeHome;
import com.hydro.casting.server.model.sched.CastingBatch;
import com.hydro.casting.server.model.sched.CastingOperation;
import com.hydro.casting.server.model.sched.Demand;
import com.hydro.casting.server.model.sched.Schedulable;
import com.hydro.core.common.exception.BusinessException;
import com.hydro.core.common.exception.BusinessMessageException;
import com.hydro.eai.sap.contract.SAPSender;
import com.hydro.eai.sap.contract.message.PLAN;
import com.hydro.eai.sap.contract.message.SAPMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class SAPService
{
    private final static Logger log = LoggerFactory.getLogger( SAPService.class );

    @EJB
    private SAPSender sapSender;

    @EJB
    private MaterialTypeHome materialTypeHome;

    public void sendPLAN( CastingBatch castingBatch ) throws BusinessException
    {
        if ( castingBatch == null )
        {
            log.warn( "castingBatch is null" );
            return;
        }
        final String casterApk = castingBatch.getExecutingMachine().getApk();
        final boolean planSendEnabled = isPLANSendEnabled( casterApk );
        if ( !planSendEnabled )
        {
            log.info( "send PLAN disabled for " + casterApk );
            return;
        }

        final List<SAPMessage> planMessages = new ArrayList<>();
        // Für jede Operation ein PLAN mit Rohbarren senden
        final List<Schedulable> members = castingBatch.getMembers();
        for ( Schedulable member : members )
        {
            if ( !( member instanceof CastingOperation ) )
            {
                continue;
            }
            final CastingOperation castingOperation = (CastingOperation) member;
            final Demand demand = castingOperation.getDemand();
            final MaterialType materialType = demand.getMaterialType();
            // Suche passenden Rohbarren
            final Alloy alloy = materialType.getAlloy();
            final double height = materialType.getHeight();
            final String quality = materialType.getQuality();
            final String qualityCode = materialType.getQualityCode();
            final double width = materialType.getWidth();
            final String category = "ROH";
            // Nur an Anlage 80 doppel lange Barren
            int amount = 1;
            if ( casterApk != null && casterApk.startsWith( "8" ) )
            {
                amount = materialType.getAmount();
            }
            final double length = ( materialType.getLength() * amount ) + 500;

            final List<MaterialType> rohbarrenMaterialTypes = materialTypeHome.findByCategoryAttributes( category, quality, qualityCode, height, width, length, alloy.getName() );
            if ( rohbarrenMaterialTypes.isEmpty() )
            {
                throw new BusinessMessageException( "Es wurde kein Rohbarren gefunden für " + materialType.getDescription() );
            }
            final MaterialType rohbarrenMaterialType = rohbarrenMaterialTypes.get( 0 );
            demand.setIntermediateType( rohbarrenMaterialType );

            final PLAN castingPlan = new PLAN();
            castingPlan.setOrderType( "P113" );
            castingPlan.setMaterial( rohbarrenMaterialType.getApk() );
            castingPlan.setQuantity( 1 );
            castingPlan.setUnitOfMeasurement( "ST" );
            castingPlan.setStartTS( castingBatch.getPlannedSuccessTs() );
//            castingPlan.setGroup( castingBatch.getCharge().substring( 2 ) );

            long castingDuration = 180;
            if ( castingBatch.getPlannedCastingDuration() != null )
            {
                castingDuration = castingBatch.getPlannedCastingDuration();
            }
            castingPlan.setEndTS( castingBatch.getPlannedSuccessTs().plusMinutes( castingDuration ) );
            // ASBA60_2
            final StringBuilder resourceBuilder = new StringBuilder( "ASBA" );
            resourceBuilder.append( castingBatch.getExecutingMachine().getApk() );
            resourceBuilder.append( "_" );
            resourceBuilder.append( castingOperation.getPosition() );

            castingPlan.setResource( resourceBuilder.toString() );
            castingPlan.setCustomerOrder( null );
            castingPlan.setCustomerOrderPosition( null );
            castingPlan.setOrderDetermination( "PC" );

            planMessages.add( castingPlan );

            // Sägeplan erzeugen
            final PLAN sawingPlan = new PLAN();
            sawingPlan.setOrderType( "P115" );
            sawingPlan.setMaterial( materialType.getApk() );
            sawingPlan.setQuantity( amount );
            sawingPlan.setUnitOfMeasurement( "ST" );
            sawingPlan.setStartTS( castingPlan.getEndTS() );
            sawingPlan.setEndTS( castingPlan.getEndTS().plusMinutes( 30 ) );
            sawingPlan.setResource( null );
            sawingPlan.setCustomerOrder( null );
            sawingPlan.setCustomerOrderPosition( null );
            sawingPlan.setOrderDetermination( "PC" );

            planMessages.add( sawingPlan );
        }
        sapSender.sendMessages( planMessages );
    }

    private boolean isPLANSendEnabled( String casterApk )
    {
        final String enabledS = System.getProperty( "casting.sap.plan.enabled." + casterApk, "false" );
        if ( enabledS != null && enabledS.equalsIgnoreCase( "true" ) )
        {
            return true;
        }
        final String allDisabled = System.getProperty( "casting.sap.plan.disabled", "true" );
        if ( allDisabled != null && allDisabled.equalsIgnoreCase( "true" ) )
        {
            return false;
        }
        final String thisDisabled = System.getProperty( "casting.sap.plan.disabled." + casterApk, "true" );
        if ( thisDisabled != null && thisDisabled.equalsIgnoreCase( "true" ) )
        {
            return false;
        }
        return true;
    }
}
