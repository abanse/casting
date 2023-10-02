package com.hydro.casting.gui.planning.view.content;

import com.google.inject.Inject;
import com.hydro.casting.common.Casting;
import com.hydro.casting.gui.model.*;
import com.hydro.casting.gui.gantt.BaseGanttChart;
import com.hydro.casting.gui.planning.gantt.model.CGDependency;
import com.hydro.casting.gui.planning.gantt.model.CGElement;
import com.hydro.casting.gui.planning.gantt.model.CGResource;
import com.hydro.casting.gui.planning.gantt.solver.CastingGanttSolver;
import com.hydro.casting.gui.planning.view.content.vo.MoldChange;
import com.hydro.core.common.util.StringTools;
import com.hydro.core.gui.ClientModelManager;
import com.hydro.core.gui.model.ClientModel;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
public class MoldScheduleController
{
    @FXML
    public GridPane contentPane;

    @Inject
    private ClientModelManager clientModelManager;

    private final Map<String, CGResource> resources = new HashMap<>();
    private final CastingGanttSolver solver = new CastingGanttSolver( resources );
    private final Map<Transfer, CGElement> transferIndex = new HashMap<>();
    private final Map<Batch, CGElement> batchIndex = new HashMap<>();

    @FXML
    private void initialize()
    {
        contentPane.setPadding( new Insets( 10.0 ) );
    }

    public void load( String currentCasterName )
    {
        contentPane.getColumnConstraints().clear();
        contentPane.getChildren().clear();

        resources.clear();
        transferIndex.clear();
        batchIndex.clear();

        final ClientModel casterModel = clientModelManager.getClientModel( CastingClientModel.ID );
        doLoadModel( casterModel );

        solver.solve();

        final List<MoldChange> moldChanges = new ArrayList<>();
        MoldChange lastMoldChange = null;

        // List casting resources
        final List<CGResource> casterResources = new ArrayList<>();
        casterResources.add( getResource( Casting.MACHINE.CASTER_50 ) );
        casterResources.add( getResource( Casting.MACHINE.CASTER_60 ) );
        casterResources.add( getResource( Casting.MACHINE.CASTER_70 ) );
        casterResources.add( getResource( Casting.MACHINE.CASTER_80 ) );
        for ( CGResource casterResource : casterResources )
        {
            final List<CGElement> batches = casterResource.getElements();
            for ( CGElement batchCG : batches )
            {
                ModelElement batchElement = batchCG.getElement();
                if ( batchElement instanceof Batch )
                {
                    final Batch batch = (Batch) batchElement;
                    if ( lastMoldChange == null || !lastMoldChange.posWidthsEquals( batch.getPosWidths() ) )
                    {
                        lastMoldChange = new MoldChange();
                        lastMoldChange.setCaster( casterResource.getName() );
                        lastMoldChange.setNeededTS( batchCG.getStart() );
                        lastMoldChange.setPosWidths( batch.getPosWidths() );
                        moldChanges.add( lastMoldChange );
                    }
                }
            }
        }

        Collections.sort( moldChanges, Comparator.comparing( MoldChange::getNeededTS ) );

        final ColumnConstraints labelCC = new ColumnConstraints( 100 );
        contentPane.getColumnConstraints().add( labelCC );
        int row = 1;
        for ( MoldChange moldChange : moldChanges )
        {
            if ( row % 2 == 0 )
            {
                final Region rowRegion = new Region();
                rowRegion.setStyle( "-fx-background-color: #f1f1f1" );
                contentPane.add( rowRegion, 0, row );
            }
            row++;
        }

        int startColumnIndex = 1;
        startColumnIndex = createGUI( startColumnIndex, moldChanges, currentCasterName, true );
        if ( Casting.MACHINE.CASTER_50.equals( currentCasterName ) )
        {
            startColumnIndex = createGUI( startColumnIndex, moldChanges, Casting.MACHINE.CASTER_60, false );
            startColumnIndex = createGUI( startColumnIndex, moldChanges, Casting.MACHINE.CASTER_70, false );
            startColumnIndex = createGUI( startColumnIndex, moldChanges, Casting.MACHINE.CASTER_80, false );
        }
        else if ( Casting.MACHINE.CASTER_60.equals( currentCasterName ) )
        {
            startColumnIndex = createGUI( startColumnIndex, moldChanges, Casting.MACHINE.CASTER_50, false );
            startColumnIndex = createGUI( startColumnIndex, moldChanges, Casting.MACHINE.CASTER_70, false );
            startColumnIndex = createGUI( startColumnIndex, moldChanges, Casting.MACHINE.CASTER_80, false );
        }
        else if ( Casting.MACHINE.CASTER_70.equals( currentCasterName ) )
        {
            startColumnIndex = createGUI( startColumnIndex, moldChanges, Casting.MACHINE.CASTER_50, false );
            startColumnIndex = createGUI( startColumnIndex, moldChanges, Casting.MACHINE.CASTER_60, false );
            startColumnIndex = createGUI( startColumnIndex, moldChanges, Casting.MACHINE.CASTER_80, false );
        }
        else if ( Casting.MACHINE.CASTER_80.equals( currentCasterName ) )
        {
            startColumnIndex = createGUI( startColumnIndex, moldChanges, Casting.MACHINE.CASTER_50, false );
            startColumnIndex = createGUI( startColumnIndex, moldChanges, Casting.MACHINE.CASTER_60, false );
            startColumnIndex = createGUI( startColumnIndex, moldChanges, Casting.MACHINE.CASTER_70, false );
        }
    }

    private int createGUI( int startColumnIndex, List<MoldChange> moldChanges, String casterName, boolean currentCaster )
    {
        final ColumnConstraints labelCC = new ColumnConstraints( 100 );
        labelCC.setPrefWidth( 100 );
        labelCC.setHalignment( HPos.RIGHT );
        final ColumnConstraints posCC = new ColumnConstraints( 50 );
        posCC.setPrefWidth( 50 );
        posCC.setHalignment( HPos.CENTER );
        contentPane.getColumnConstraints().add( posCC );
        contentPane.getColumnConstraints().add( posCC );
        contentPane.getColumnConstraints().add( posCC );
        contentPane.getColumnConstraints().add( posCC );
        if ( Casting.MACHINE.CASTER_80.equals( casterName ) )
        {
            contentPane.getColumnConstraints().add( posCC );
        }

        final Label casterLBL = new Label( casterName );
        casterLBL.setAlignment( Pos.CENTER );
        casterLBL.setMaxWidth( Double.MAX_VALUE );
        if ( currentCaster )
        {
            casterLBL.setStyle( "-fx-background-color: #fde5c8; -fx-border-width: 1; -fx-border-color: black" );
        }
        else
        {
            casterLBL.setStyle( "-fx-background-color: #edfdc8; -fx-border-width: 1; -fx-border-color: black" );
        }
        casterLBL.setFont( Font.font( 20.0 ) );
        int columnSpan = 4;
        if ( Casting.MACHINE.CASTER_80.equals( casterName ) )
        {
            columnSpan = 5;
        }
        contentPane.add( casterLBL, startColumnIndex, 0, columnSpan, 1 );

        int row = 1;
        for ( MoldChange moldChange : moldChanges )
        {
            final Region rowRegion = new Region();
            if ( row % 2 == 0 )
            {
                rowRegion.setStyle( "-fx-background-color: #f1f1f1" );
            }
            else
            {
                rowRegion.setStyle( "-fx-background-color: transparent" );
            }
            contentPane.add( rowRegion, startColumnIndex, row, columnSpan, 1 );
            if ( moldChange.getCaster().equals( casterName ) )
            {
                final Label timeLBL = new Label( moldChange.getNeededTS().format( DateTimeFormatter.ofPattern( "dd.MM HH:mm:ss" ) ) );
                contentPane.add( timeLBL, 0, row );

                final Double[] posWidths = moldChange.getPosWidths();
                int posIndex = 0;
                for ( Double posWidth : posWidths )
                {
                    if ( posWidth != null )
                    {
                        final Label posLBL = new Label( StringTools.N1F.format( posWidth ) );
                        posLBL.setAlignment( Pos.CENTER );
                        posLBL.setMaxWidth( Double.MAX_VALUE );
                        posLBL.setStyle( "-fx-border-width: 1; -fx-border-color: gray" );
                        contentPane.add( posLBL, startColumnIndex + posIndex, row );
                    }
                    else
                    {
                        final Region posRegion = new Region();
                        posRegion.setMaxWidth( Double.MAX_VALUE );
                        posRegion.setStyle( "-fx-border-width: 1; -fx-border-color: gray" );
                        contentPane.add( posRegion, startColumnIndex + posIndex, row );
                    }
                    posIndex++;
                }
            }
            row++;
        }

        if ( Casting.MACHINE.CASTER_80.equals( casterName ) )
        {
            return startColumnIndex + 5;
        }
        else
        {
            return startColumnIndex + 4;
        }
    }

    protected void doLoadModel( ClientModel casterModel )
    {
        addCaster( casterModel, Casting.MACHINE.CASTER_50 );
        addCaster( casterModel, Casting.MACHINE.CASTER_60 );
        addCaster( casterModel, Casting.MACHINE.CASTER_70 );
        addCaster( casterModel, Casting.MACHINE.CASTER_80 );
    }

    private void addCaster( ClientModel casterModel, String casterName )
    {
        final Caster caster = casterModel.getEntity( Caster.class, casterName );

        final CGResource casterResource = getResource( casterName );

        List<Batch> allBatches = caster.getBatches();
        Map<String, CGElement> furnaceCastingGE = new HashMap<>();
        int index = 0;
        for ( Batch batch : allBatches )
        {
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
                meltingGE.setName( transfer.getName() );
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

                // Prüfen ob CR Material eingesetzt wurde
                /*
                List<TransferMaterial> transferMaterials = transfer.getTransferMaterials();
                for ( TransferMaterial transferMaterial : transferMaterials )
                {
                    // Schachtofenbehandlung
                    addContinousTransferTo( chart, meltingGE, transferMaterial.getType(), transferMaterial.getWeight() );
                }

                 */
            }

            index++;
        }
    }

    protected CGResource getResource( String name )
    {
        CGResource resource = resources.get( name );
        if ( resource == null )
        {
            resource = new CGResource();
            resource.setName( name );
            resources.put( name, resource );
        }
        return resource;
    }

    protected void addTransferToIndex( Transfer transfer, CGElement cgElement )
    {
        transferIndex.put( transfer, cgElement );
    }

    protected Duration getDuration( String category, CGElement cgElement, int defaultValue, ChronoUnit timeUnit )
    {
        final String name = BaseGanttChart.calcDurationName( category, cgElement );
        if ( category.contains( "busy" ) )
        {
            cgElement.setDurationSetupAfterName( name );
        }
        else
        {
            cgElement.setDurationName( name );
        }
        final CastingClientModel casterModel = (CastingClientModel) clientModelManager.getClientModel( CastingClientModel.ID );

        final Duration dura;
        if ( defaultValue == 0 )
        {
            dura = Duration.ZERO;
        }
        else
        {
            dura = Duration.of( defaultValue, timeUnit );
        }
        final Batch batch = BaseGanttChart.getBatch( cgElement );
        final Transfer transfer = BaseGanttChart.getTransfer( cgElement );
        return Duration.ofMinutes( (long) casterModel.getDuration( batch, transfer, category, dura.toMinutes() ) );
    }

}