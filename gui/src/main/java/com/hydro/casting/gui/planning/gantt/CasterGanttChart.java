package com.hydro.casting.gui.planning.gantt;

import com.hydro.casting.common.Casting;
import com.hydro.casting.gui.gantt.BaseGanttChart;
import com.hydro.casting.gui.model.*;
import com.hydro.casting.gui.planning.gantt.model.CGDependency;
import com.hydro.casting.gui.planning.gantt.model.CGElement;
import com.hydro.casting.gui.planning.gantt.model.CGResource;
import com.hydro.casting.gui.planning.gantt.node.CastingNode;
import com.hydro.casting.gui.planning.gantt.node.LineNode;
import com.hydro.casting.gui.planning.gantt.node.MeltingNode;
import com.hydro.casting.gui.planning.gantt.node.ResourceCalendarNode;
import com.hydro.core.gui.model.ClientModel;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CasterGanttChart extends BaseGanttChart
{
    private final CGElement line5060 = new CGElement();
    private final CGElement line6070 = new CGElement();
    private final CGElement line7080 = new CGElement();

    public CasterGanttChart()
    {
        super( new String[] { "82", "80", "81", "", "72", "70", "71", " ", "62", "60", "61", "  ", "52", "50", "51" } );
    }

    protected void doRefreshExtTimeLine( final LocalDateTime horizontStart, final LocalDateTime horizontEnd )
    {
        line5060.setStart( horizontStart );
        line5060.setEnd( horizontEnd );
        line5060.setDuration( Duration.between( horizontStart, horizontEnd ) );
        line6070.setStart( horizontStart );
        line6070.setEnd( horizontEnd );
        line6070.setDuration( Duration.between( horizontStart, horizontEnd ) );
        line7080.setStart( horizontStart );
        line7080.setEnd( horizontEnd );
        line7080.setDuration( Duration.between( horizontStart, horizontEnd ) );
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

        Data<LocalDateTime, String> lineData5060 = new Data<>( null, "  ", line5060 );
        lineData5060.setNode( new LineNode( this, line5060 ) );
        chart.getData().add( lineData5060 );
        Data<LocalDateTime, String> lineData6070 = new Data<>( null, " ", line6070 );
        lineData6070.setNode( new LineNode( this, line6070 ) );
        chart.getData().add( lineData6070 );
        Data<LocalDateTime, String> lineData7080 = new Data<>( null, "", line7080 );
        lineData7080.setNode( new LineNode( this, line7080 ) );
        chart.getData().add( lineData7080 );

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
                data.setNode( new MeltingNode( this, meltingGE ) );
                chart.getData().add( data );

                castingGE = new CGElement();
                castingGE.setName( transfer.getName() );
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

            }

            index++;
        }
    }
}