package com.hydro.casting.gui.model.calc;

import com.hydro.casting.gui.model.*;
import com.hydro.casting.gui.model.common.AnalysisCalculator;
import com.hydro.casting.gui.model.common.EMaterialColors;
import com.hydro.casting.gui.model.common.EMaterialGroup;
import com.hydro.casting.gui.model.common.ETransferMaterialType;
import com.hydro.core.gui.model.ClientModel;
import javafx.beans.binding.ObjectBinding;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransferFillmentTransferMaterialBinding extends ObjectBinding<TransferMaterial>
{
    private final static Logger log = LoggerFactory.getLogger( TransferFillmentTransferMaterialBinding.class );

    private final Transfer transfer;

    private Material fillmentMaterial = null;
    private Analysis specAnalysis = null;
    private TransferMaterial fillementTransferMaterial = null;

    public TransferFillmentTransferMaterialBinding( Transfer transfer )
    {
        this.transfer = transfer;
    }

    @Override
    protected TransferMaterial computeValue()
    {
        log.trace( "compute TransferFillmentTransferMaterialBinding for " + transfer.getName() + " batch " + transfer.getBatch().getName() );
        final Batch batch = transfer.getBatch();

        if ( fillmentMaterial == null )
        {
            fillmentMaterial = createFillmentMaterial( batch );
        }
        if ( fillementTransferMaterial == null )
        {
            fillementTransferMaterial = new TransferMaterial();
            fillementTransferMaterial.setSource( fillmentMaterial );
        }

        if ( transfer.getSpecMaterial() != null )
        {
            final Analysis asAnalysis = transfer.getSpecMaterial().getAnalysis().clone();
            asAnalysis.setName( "AS [" + asAnalysis.getName() + "]" );
            fillmentMaterial.setPresentationColor( Color.web( EMaterialColors.SPEC_MATERIAL.getColor() ) );
            fillmentMaterial.setAnalysis( asAnalysis );

            fillementTransferMaterial.setType( ETransferMaterialType.SPEC_MATERIAL.getApk() );
            fillementTransferMaterial.setRefOID( transfer.getSpecMaterial().getRefOID() );
            fillementTransferMaterial.setName( "AS" );
            log.info( "transferSM" );
        }
        else if ( transfer.getFillMaterial() != null )
        {
            final Analysis fmAnalysis = transfer.getFillMaterial().getAnalysis().clone();
            fmAnalysis.setName( "FM [" + fmAnalysis.getName() + "]" );
            fillmentMaterial.setPresentationColor( Color.web( EMaterialColors.FILL_MATERIAL.getColor() ) );
            fillmentMaterial.setAnalysis( fmAnalysis );

            fillementTransferMaterial.setType( ETransferMaterialType.FILL_MATERIAL.getApk() );
            fillementTransferMaterial.setRefOID( transfer.getFillMaterial().getRefOID() );
            fillementTransferMaterial.setName( "FM" );
            log.info( "transferFM" );
        }
        else if ( batch.getSpecMaterial() != null )
        {
            final Analysis asAnalysis = batch.getSpecMaterial().getAnalysis().clone();
            asAnalysis.setName( "AS [" + asAnalysis.getName() + "]" );
            fillmentMaterial.setPresentationColor( Color.web( EMaterialColors.SPEC_MATERIAL.getColor() ) );
            fillmentMaterial.setAnalysis( asAnalysis );

            fillementTransferMaterial.setType( ETransferMaterialType.SPEC_MATERIAL.getApk() );
            fillementTransferMaterial.setRefOID( batch.getSpecMaterial().getRefOID() );
            fillementTransferMaterial.setName( "AS" );
            log.info( "batchSM" );
        }
        else if ( batch.getFillMaterial() != null )
        {
            final Analysis fmAnalysis = batch.getFillMaterial().getAnalysis().clone();
            fmAnalysis.setName( "FM [" + fmAnalysis.getName() + "]" );
            fillmentMaterial.setPresentationColor( Color.web( EMaterialColors.FILL_MATERIAL.getColor() ) );
            fillmentMaterial.setAnalysis( fmAnalysis );

            fillementTransferMaterial.setType( ETransferMaterialType.FILL_MATERIAL.getApk() );
            fillementTransferMaterial.setRefOID( batch.getFillMaterial().getRefOID() );
            fillementTransferMaterial.setName( "FM" );
            log.info( "batchFM" );
        }
        else
        {
            fillmentMaterial.setPresentationColor( Color.web( EMaterialColors.SPEC_MATERIAL_AS_UNDEFINED.getColor() ) );
            fillmentMaterial.setAnalysis( specAnalysis );

            fillementTransferMaterial.setType( ETransferMaterialType.NONE_SPEC_MATERIAL.getApk() );
            fillementTransferMaterial.setRefOID( null );
            fillementTransferMaterial.setName( String.valueOf( batch.getRefOID() ) );
        }

        double transferMaterialWeights = 0;
        for ( TransferMaterial transferMaterial : transfer.getTransferMaterials() )
        {
            transferMaterialWeights = transferMaterialWeights + transferMaterial.getWeight();
        }
        fillementTransferMaterial.setWeight( transfer.getTargetWeight() - transfer.getBottomWeight() - transferMaterialWeights );

        return fillementTransferMaterial;
    }

    private Material createFillmentMaterial( Batch batch )
    {
        final ClientModel clientModel = batch.getCaster().getClientModel();
        MaterialGroup materialGroup = clientModel.getEntity( MaterialGroup.class, EMaterialGroup.SPEC_MATERIAL.getApk() );

        Material mat = new Material();
        mat.setMaterialGroup( materialGroup );
        mat.setName( String.valueOf( batch.getRefOID() ) );
        mat.setWeight( 0 );

        Analysis matAnalysis = new Analysis();
        matAnalysis.setName( "AS [???]" );
        mat.setAnalysis( matAnalysis );

        Specification spec = batch.getSpecification();

        if ( spec != null )
        {
            AnalysisCalculator.fillAnalysisWithSpecification( matAnalysis, spec );
        }
        mat.setPresentationColor( Color.web( EMaterialColors.SPEC_MATERIAL_AS_UNDEFINED.getColor() ) );

        specAnalysis = mat.getAnalysis();

        return mat;
    }
}