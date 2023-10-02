package com.hydro.casting.gui.planning.gantt;

import com.google.inject.Inject;
import com.hydro.casting.common.Casting;
import com.hydro.casting.gui.gantt.BaseGanttChart;
import com.hydro.casting.gui.model.*;
import com.hydro.casting.gui.planning.gantt.model.CGDependency;
import com.hydro.casting.gui.planning.gantt.model.CGElement;
import com.hydro.casting.gui.planning.gantt.model.CGResource;
import com.hydro.casting.gui.planning.gantt.node.*;
import com.hydro.casting.gui.planning.task.CreatePlannedContinuousTransferTask;
import com.hydro.casting.gui.planning.task.DeletePlannedContinuousTransferTask;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.core.gui.model.ClientModel;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class FurnaceGanttChart extends BaseGanttChart
{
    private final CGElement line5060 = new CGElement();
    private final CGElement line6070 = new CGElement();
    private final CGElement line7080 = new CGElement();
    private final CGElement lineMF = new CGElement();

    private final CGElement continousMeltingUBCElement = new CGElement();
    private final CGElement continousMeltingS2Element = new CGElement();
    private final CGElement continousMeltingS1Element = new CGElement();
    private final CGElement continousMeltingRealAlloyElement = new CGElement();
    private final CGElement continousMeltingElektrolyseElement = new CGElement();

    @Inject
    private CreatePlannedContinuousTransferTask createPlannedContinuousTransferTask;

    @Inject
    private DeletePlannedContinuousTransferTask deletePlannedContinuousTransferTask;

    public FurnaceGanttChart()
    {
        super( new String[] { "82", "81", "", "72", "71", " ", "62", "61", "  ", "52", "51", "   ", Casting.ALLOY_SOURCES.REAL_ALLOY, Casting.ALLOY_SOURCES.ELEKTROLYSE, Casting.ALLOY_SOURCES.UBC_S3,
                Casting.ALLOY_SOURCES.MELTING_FURNACE_S2, Casting.ALLOY_SOURCES.MELTING_FURNACE_S1 } );
    }

    protected void doRefreshExtTimeLine( final LocalDateTime horizontStart, final LocalDateTime horizontEnd )
    {
        continousMeltingUBCElement.setName( Casting.ALLOY_SOURCES.UBC_S3 );
        continousMeltingUBCElement.setStart( horizontStart );
        continousMeltingUBCElement.setEnd( horizontEnd );
        continousMeltingUBCElement.setDuration( Duration.between( horizontStart, horizontEnd ) );
        continousMeltingS1Element.setName( Casting.ALLOY_SOURCES.MELTING_FURNACE_S1 );
        continousMeltingS1Element.setStart( horizontStart );
        continousMeltingS1Element.setEnd( horizontEnd );
        continousMeltingS1Element.setDuration( Duration.between( horizontStart, horizontEnd ) );
        continousMeltingS2Element.setName( Casting.ALLOY_SOURCES.MELTING_FURNACE_S2 );
        continousMeltingS2Element.setStart( horizontStart );
        continousMeltingS2Element.setEnd( horizontEnd );
        continousMeltingS2Element.setDuration( Duration.between( horizontStart, horizontEnd ) );
        continousMeltingRealAlloyElement.setName( Casting.ALLOY_SOURCES.REAL_ALLOY );
        continousMeltingRealAlloyElement.setStart( horizontStart );
        continousMeltingRealAlloyElement.setEnd( horizontEnd );
        continousMeltingRealAlloyElement.setDuration( Duration.between( horizontStart, horizontEnd ) );
        continousMeltingElektrolyseElement.setName( Casting.ALLOY_SOURCES.ELEKTROLYSE );
        continousMeltingElektrolyseElement.setStart( horizontStart );
        continousMeltingElektrolyseElement.setEnd( horizontEnd );
        continousMeltingElektrolyseElement.setDuration( Duration.between( horizontStart, horizontEnd ) );

        line5060.setStart( horizontStart );
        line5060.setEnd( horizontEnd );
        line5060.setDuration( Duration.between( horizontStart, horizontEnd ) );
        line6070.setStart( horizontStart );
        line6070.setEnd( horizontEnd );
        line6070.setDuration( Duration.between( horizontStart, horizontEnd ) );
        line7080.setStart( horizontStart );
        line7080.setEnd( horizontEnd );
        line7080.setDuration( Duration.between( horizontStart, horizontEnd ) );
        lineMF.setStart( horizontStart );
        lineMF.setEnd( horizontEnd );
        lineMF.setDuration( Duration.between( horizontStart, horizontEnd ) );
    }

    protected void doLoadModel( XYChart.Series<LocalDateTime, String> chart, ClientModel casterModel )
    {
        line5060.setStart( LocalDateTime.now() );
        line5060.setEnd( LocalDateTime.now() );
        line5060.setDuration( Duration.ZERO );
        line5060.setSetupAfter( Duration.ZERO );
        line5060.setWaitDuration( Duration.ZERO );
        line6070.setStart( LocalDateTime.now() );
        line6070.setEnd( LocalDateTime.now() );
        line6070.setDuration( Duration.ZERO );
        line6070.setSetupAfter( Duration.ZERO );
        line6070.setWaitDuration( Duration.ZERO );
        line7080.setStart( LocalDateTime.now() );
        line7080.setEnd( LocalDateTime.now() );
        line7080.setDuration( Duration.ZERO );
        line7080.setSetupAfter( Duration.ZERO );
        line7080.setWaitDuration( Duration.ZERO );
        lineMF.setStart( LocalDateTime.now() );
        lineMF.setEnd( LocalDateTime.now() );
        lineMF.setDuration( Duration.ZERO );
        lineMF.setSetupAfter( Duration.ZERO );
        lineMF.setWaitDuration( Duration.ZERO );

        Data<LocalDateTime, String> lineDataMF = new Data<>( null, "   ", lineMF );
        lineDataMF.setNode( new LineNode( this, lineMF ) );
        chart.getData().add( lineDataMF );
        Data<LocalDateTime, String> lineData5060 = new Data<>( null, "  ", line5060 );
        lineData5060.setNode( new LineNode( this, line5060 ) );
        chart.getData().add( lineData5060 );
        Data<LocalDateTime, String> lineData6070 = new Data<>( null, " ", line6070 );
        lineData6070.setNode( new LineNode( this, line6070 ) );
        chart.getData().add( lineData6070 );
        Data<LocalDateTime, String> lineData7080 = new Data<>( null, "", line7080 );
        lineData7080.setNode( new LineNode( this, line7080 ) );
        chart.getData().add( lineData7080 );

        continousMeltingUBCElement.setStart( LocalDateTime.now() );
        continousMeltingUBCElement.setEnd( LocalDateTime.now() );
        continousMeltingUBCElement.setDuration( Duration.ZERO );
        continousMeltingUBCElement.setSetupAfter( Duration.ZERO );
        continousMeltingUBCElement.setWaitDuration( Duration.ZERO );

        Data<LocalDateTime, String> continousMeltingUBCData = new Data<>( null, Casting.ALLOY_SOURCES.UBC_S3, continousMeltingUBCElement );
        continousMeltingUBCData.setNode( new ContinuousResourceNode( this, continousMeltingUBCElement ) );
        chart.getData().add( continousMeltingUBCData );

        continousMeltingS1Element.setStart( LocalDateTime.now() );
        continousMeltingS1Element.setEnd( LocalDateTime.now() );
        continousMeltingS1Element.setDuration( Duration.ZERO );
        continousMeltingS1Element.setSetupAfter( Duration.ZERO );
        continousMeltingS1Element.setWaitDuration( Duration.ZERO );

        Data<LocalDateTime, String> continousMeltingS1Data = new Data<>( null, Casting.ALLOY_SOURCES.MELTING_FURNACE_S1, continousMeltingS1Element );
        continousMeltingS1Data.setNode( new ContinuousResourceNode( this, continousMeltingS1Element ) );
        chart.getData().add( continousMeltingS1Data );

        continousMeltingS2Element.setStart( LocalDateTime.now() );
        continousMeltingS2Element.setEnd( LocalDateTime.now() );
        continousMeltingS2Element.setDuration( Duration.ZERO );
        continousMeltingS2Element.setSetupAfter( Duration.ZERO );
        continousMeltingS2Element.setWaitDuration( Duration.ZERO );

        Data<LocalDateTime, String> continousMeltingS2Data = new Data<>( null, Casting.ALLOY_SOURCES.MELTING_FURNACE_S2, continousMeltingS2Element );
        continousMeltingS2Data.setNode( new ContinuousResourceNode( this, continousMeltingS2Element ) );
        chart.getData().add( continousMeltingS2Data );

        continousMeltingRealAlloyElement.setStart( LocalDateTime.now() );
        continousMeltingRealAlloyElement.setEnd( LocalDateTime.now() );
        continousMeltingRealAlloyElement.setDuration( Duration.ZERO );
        continousMeltingRealAlloyElement.setSetupAfter( Duration.ZERO );
        continousMeltingRealAlloyElement.setWaitDuration( Duration.ZERO );

        Data<LocalDateTime, String> continousMeltingRealAlloyData = new Data<>( null, Casting.ALLOY_SOURCES.REAL_ALLOY, continousMeltingRealAlloyElement );
        continousMeltingRealAlloyData.setNode( new ContinuousResourceNode( this, continousMeltingRealAlloyElement ) );
        chart.getData().add( continousMeltingRealAlloyData );

        continousMeltingElektrolyseElement.setStart( LocalDateTime.now() );
        continousMeltingElektrolyseElement.setEnd( LocalDateTime.now() );
        continousMeltingElektrolyseElement.setDuration( Duration.ZERO );
        continousMeltingElektrolyseElement.setSetupAfter( Duration.ZERO );
        continousMeltingElektrolyseElement.setWaitDuration( Duration.ZERO );

        Data<LocalDateTime, String> continousMeltingElektrolyseData = new Data<>( null, Casting.ALLOY_SOURCES.ELEKTROLYSE, continousMeltingElektrolyseElement );
        continousMeltingElektrolyseData.setNode( new ContinuousResourceNode( this, continousMeltingElektrolyseElement ) );
        chart.getData().add( continousMeltingElektrolyseData );

        addCaster( chart, casterModel, Casting.MACHINE.CASTER_50 );
        addCaster( chart, casterModel, Casting.MACHINE.CASTER_60 );
        addCaster( chart, casterModel, Casting.MACHINE.CASTER_70 );
        addCaster( chart, casterModel, Casting.MACHINE.CASTER_80 );

        solve();
    }

    private void addCaster( XYChart.Series<LocalDateTime, String> chart, ClientModel casterModel, String casterName )
    {
        final Caster caster = casterModel.getEntity( Caster.class, casterName );

        final List<Resource> resourceCalendarResources = new ArrayList<>();
        resourceCalendarResources.add( caster );
        final String[] meltingFurnaces = Casting.getMeltingFurnacesForCaster( casterName );
        for ( String meltingFurnace : meltingFurnaces )
        {
            resourceCalendarResources.add( casterModel.getEntity( Furnace.class, meltingFurnace ) );
        }

        for ( Resource resourceCalendarResource : resourceCalendarResources )
        {
            final CGResource cgResource = getResource( resourceCalendarResource.getName() );
            for ( ResourceCalendarEntry resourceCalendarEntry : resourceCalendarResource.getResourceCalendarEntries() )
            {

                final CGElement resourceCalendarEntryGE = new CGElement();
                resourceCalendarEntryGE.setName( resourceCalendarEntry.getName() );
                resourceCalendarEntryGE.setElement( resourceCalendarEntry );
                resourceCalendarEntryGE.setStart( resourceCalendarEntry.getStartTS() );
                resourceCalendarEntryGE.setDuration( Duration.ofMinutes( resourceCalendarEntry.getDuration() ) );
                resourceCalendarEntryGE.setEnd( resourceCalendarEntry.getStartTS().plusMinutes( resourceCalendarEntry.getDuration() ) );
                resourceCalendarEntryGE.setFixedTimeRange( true );
                resourceCalendarEntryGE.setSetupAfter( Duration.ZERO );
                resourceCalendarEntryGE.setWaitDuration( Duration.ZERO );
                resourceCalendarEntryGE.setResource( cgResource );

                Data<LocalDateTime, String> data = new Data<>( null, resourceCalendarResource.getName(), resourceCalendarEntryGE );
                data.setNode( new ResourceCalendarNode( this, resourceCalendarEntryGE ) );
                chart.getData().add( data );
            }
        }

        final CGResource casterResource = getResource( casterName );

        List<Batch> allBatches = caster.getBatches();
        Map<String, CGElement> furnaceCastingGE = new HashMap<>();
        int index = 0;
        for ( Batch batch : allBatches )
        {
            if ( index > BATCHES_TO_LOAD )
            {
                break;
            }

            final List<Transfer> transfers = batch.getTransfers();
            for ( Transfer transfer : transfers )
            {
                if ( transfer.isRemoved() )
                {
                    continue;
                }

                Resource fromFurnace = transfer.getFrom();

                final CGResource furnaceResource = getResource( fromFurnace.getName() );

                CGElement castingGE = furnaceCastingGE.get( fromFurnace.getName() );
                CGDependency meltingSequenceCGD = null;
                if ( castingGE != null )
                {
                    meltingSequenceCGD = new CGDependency();
                    meltingSequenceCGD.setDependencyType( CGDependency.DependencyType.START_IMMEDIATE_AFTER );
                    meltingSequenceCGD.setOtherSide( castingGE );
                }
                CGElement meltingGE = new CGElement();
                meltingGE.setName( transfer.getBatch().getDescription() + " - " + transfer.getName() );
                meltingGE.setElement( transfer );
                if ( transfer.getFurnaceTransferMaterial() != null )
                {
                    meltingGE.setDuration( getDuration( "melting", meltingGE, 180 / 2, ChronoUnit.MINUTES ) );
                }
                else
                {
                    meltingGE.setDuration( getDuration( "melting", meltingGE, 180, ChronoUnit.MINUTES ) );
                }
                meltingGE.setSetupAfter( getDuration( "melting.busy", meltingGE, 30, ChronoUnit.MINUTES ) );
                meltingGE.setResource( furnaceResource );
                if ( meltingSequenceCGD != null )
                {
                    meltingGE.addDependency( meltingSequenceCGD );
                }
                addTransferToIndex( transfer, meltingGE );

                Data<LocalDateTime, String> data = new Data<>( null, fromFurnace.getName(), meltingGE );
                data.setNode( new MeltingFurnaceNode( this, meltingGE ) );
                chart.getData().add( data );

                castingGE = new CGElement();
                castingGE.setName( transfer.getBatch().getDescription() + " - " + transfer.getName() );
                castingGE.setElement( transfer.getBatch() );
                castingGE.setDuration( getDuration( "casting", castingGE, 30, ChronoUnit.MINUTES ) );
                castingGE.setSetupAfter( getDuration( "casting.busy", castingGE, 90, ChronoUnit.MINUTES ) );
                castingGE.setResource( casterResource );

                CGDependency castingMeltingCGD = new CGDependency();
                castingMeltingCGD.setDependencyType( CGDependency.DependencyType.START_AFTER );
                castingMeltingCGD.setOtherSide( meltingGE );
                castingGE.addDependency( castingMeltingCGD );

                furnaceCastingGE.put( fromFurnace.getName(), castingGE );

                data = new Data<>( null, casterName, castingGE );
                data.setNode( new CastingNode( this, castingGE ) );
                chart.getData().add( data );

                // Prüfen ob CR Material eingesetzt wurde
                List<TransferMaterial> transferMaterials = transfer.getTransferMaterials();
                Map<String, Double> sourceWeights = new HashMap<>();
                for ( TransferMaterial transferMaterial : transferMaterials )
                {
                    if ( transferMaterial.getType() == null || Arrays.stream( Casting.ALLOY_SOURCES.ALL ).noneMatch( s -> Objects.equals( s, transferMaterial.getType() ) ) )
                    {
                        continue;
                    }
                    addContinuousTransferTo( chart, meltingGE, transferMaterial, transferMaterial.getType(), transferMaterial.getWeight(), false );
                    Double currentWeight = sourceWeights.get( transferMaterial.getType() );
                    if ( currentWeight == null )
                    {
                        currentWeight = transferMaterial.getWeight();
                    }
                    else
                    {
                        currentWeight = currentWeight + transferMaterial.getWeight();
                    }
                    sourceWeights.put( transferMaterial.getType(), currentWeight );
                }
                // Prozent-Materialien hinzugügen
                final CasterScheduleDTO casterScheduleDTO = casterModel.getEntity( CasterScheduleDTO.class, batch.getObjid() );
                for ( String source : Casting.ALLOY_SOURCES.ALL )
                {
                    Double concreteValue = null;
                    if ( casterScheduleDTO != null )
                    {
                        if ( Casting.ALLOY_SOURCES.MELTING_FURNACE_S1.equals( source ) )
                        {
                            concreteValue = casterScheduleDTO.getPercentageS1();
                        }
                        else if ( Casting.ALLOY_SOURCES.MELTING_FURNACE_S2.equals( source ) )
                        {
                            concreteValue = casterScheduleDTO.getPercentageS2();
                        }
                        else if ( Casting.ALLOY_SOURCES.UBC_S3.equals( source ) )
                        {
                            concreteValue = casterScheduleDTO.getPercentageS3();
                        }
                        else if ( Casting.ALLOY_SOURCES.ELEKTROLYSE.equals( source ) )
                        {
                            concreteValue = casterScheduleDTO.getPercentageEL();
                        }
                        else if ( Casting.ALLOY_SOURCES.REAL_ALLOY.equals( source ) )
                        {
                            concreteValue = casterScheduleDTO.getPercentageRA();
                        }
                    }
                    double percentage = 0;
                    if ( concreteValue != null )
                    {
                        percentage = concreteValue;
                    }
                    else
                    {
                        final CastingClientModel castingClientModel = (CastingClientModel) casterModel;
                        percentage = castingClientModel.getContentPercentage( casterName, transfer.getFrom().getName(), batch.getAlloy(), source );
                    }

                    final double targetTransferWeight = transfer.getTargetWeight() * ( percentage / 100.0 );
                    double neededTransferWeight = targetTransferWeight;
                    final Double currentSourceWeight = sourceWeights.get( source );
                    if ( currentSourceWeight != null )
                    {
                        neededTransferWeight = neededTransferWeight - currentSourceWeight;
                    }
                    if ( neededTransferWeight > 0 )
                    {
                        addContinuousTransferTo( chart, meltingGE, meltingGE.getElement(), source, neededTransferWeight, true );
                    }
                }
            }

            index++;
        }
    }

    public void createContinuousTransferTo( CGElement targetElement, String source )
    {
        if ( targetElement == null )
        {
            return;
        }

        // Hier das speichern im Model
        // eine OBJID muss im TransferMaterial besetzt werden um die Replikation vom Model
        // im nach hinein zu gewährleisten (1s verzögerte Replikation model)
        createPlannedContinuousTransferTask.setData( targetElement, source.substring( 2 ) );
        taskManager.executeTask( createPlannedContinuousTransferTask );
    }

    private void addContinuousTransferTo( XYChart.Series<LocalDateTime, String> chart, CGElement transferElement, ModelElement transferMaterial, String source, double weight, boolean theoretical )
    {
        CGResource soResource = getResource( source );

        // add SO Element
        CGElement soElement = new CGElement();

        String name = Material.WEIGHT_T_FORMATTER.format( weight / 1000.0 ) + "t";
        soElement.setName( name );
        soElement.setElement( transferMaterial );
        soElement.setResource( soResource );
        soElement.setTheoretical( theoretical );
        if ( Casting.ALLOY_SOURCES.ELEKTROLYSE.equals( source ) )
        {
            soElement.setDuration( Duration.ofMinutes( Casting.GANTT.EL_TRANSFER_TIME ) );
            soElement.setSetupAfter( Duration.ofMinutes( Casting.GANTT.EL_SETUP_AFTER_TIME ) );
        }
        else
        {
            soElement.setDuration( Duration.ofMinutes( Casting.GANTT.ALLOY_SOURCE_TRANSFER_TIME ) );
            soElement.setSetupAfter( Duration.ofMinutes( Casting.GANTT.ALLOY_SOURCE_SETUP_AFTER_TIME ) );
        }

        transferElement.addInnerElement( soElement );

        Data<LocalDateTime, String> data = new XYChart.Data<>( null, source, soElement );
        data.setNode( new ContinuousTransferNode( this, soElement ) );
        chart.getData().add( data );

        List<CGDependency> elementDependencies = transferElement.getDependencies();
        for ( CGDependency elementDependency : elementDependencies )
        {
            CGDependency soDependency = elementDependency.copy();
            soDependency.setDependencyType( CGDependency.DependencyType.FINISHED_BEFORE );
            soElement.addDependency( soDependency );
        }
    }

    public void deleteContinousTransferTo( CGElement element )
    {
        if ( element == null )
        {
            return;
        }

        deletePlannedContinuousTransferTask.setData( element );
        taskManager.executeTask( deletePlannedContinuousTransferTask );
    }

}