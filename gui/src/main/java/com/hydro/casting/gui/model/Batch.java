package com.hydro.casting.gui.model;

import com.hydro.casting.gui.model.calc.*;
import com.hydro.casting.gui.model.common.AnalysisCalculator;
import com.hydro.casting.gui.model.undo.*;
import com.hydro.casting.gui.model.validation.IValidationable;
import com.hydro.casting.gui.model.validation.ValidationResult;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.apache.commons.lang3.StringEscapeUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Batch extends NamedModelElement implements IUndoable, IValidationable
{
    private static final long serialVersionUID = 1L;
    private final static NumberFormat N2F = new DecimalFormat( "00" );

    private final UndoDoubleProperty furnaceTargetWeight;
    private final UndoDoubleProperty castingWeight;

    private final SimpleStringProperty detail;

    private final SimpleStringProperty description;

    private final SimpleObjectProperty<Specification> specification;
    private final UndoObjectProperty<Furnace> from;
    private final UndoObservableList<Transfer> transfers;
    private final UndoBooleanProperty makeEmpty;
    private final UndoBooleanProperty alloyChange;
    private final UndoObservableList<TransferMaterial> transferMaterials;
    private final SimpleBooleanProperty editable;
    private final SimpleBooleanProperty toSave;
    private final BatchBottomWeightBinding bottomWeight;
    private final BatchSpecConformBinding specConform;
    private final BatchNumberOfTransferProcessesBinding numberOfTransferProcesses;
    private final SumTransferWeightsBinding sumTransferWeights;
    private final BatchRemovedBinding allTransfersRemoved;
    private final BatchLengthBinding length;
    private final BatchMakeEmptyEditableBinding makeEmptyEditable;
    private final IUndoable[] undoables;
    private Analysis bottomAnalysis;
    private int index;
    private Caster caster;
    private String einsatzApk;
    private String fauf;
    private String customer;
    private String alloy;
    private String alloyNbr;
    private String product;
    private double castingLength;
    private int amount;
    private double width;
    private double height;
    private double density;
    private Material specMaterial;
    private Material fillMaterial;
    private long replicTimeMillis;
    private Double[] posWidths;

    public Batch()
    {
        furnaceTargetWeight = new UndoDoubleProperty();
        castingWeight = new UndoDoubleProperty();
        specification = new SimpleObjectProperty<Specification>();
        detail = new SimpleStringProperty();
        description = new SimpleStringProperty();
        transfers = new UndoObservableList<>( "transfers" );
        from = new UndoObjectProperty<Furnace>();
        makeEmpty = new UndoBooleanProperty();
        alloyChange = new UndoBooleanProperty();
        editable = new SimpleBooleanProperty();
        toSave = new SimpleBooleanProperty();
        toSave.addListener( ( observable, oldValue, newValue ) -> {
            if ( newValue != null && getCaster() != null )
            {
                if ( newValue )
                {
                    boolean savePossible = true;
                    final Batch prev = getCaster().getPrevious( this );
                    if ( prev != null )
                    {
                        if ( prev.getEinsatzApk() == null && prev.isEditable() && !prev.isToSave() )
                        {
                            savePossible = false;
                        }
                    }
                    if ( !savePossible )
                    {
                        Platform.runLater( () -> toSave.set( false ) );
                    }
                }
                else
                {
                    // prüfen ob danach noch Einträge zu speichern sind
                    // wenn ja, darf der Eintrag nicht deselktiert werden
                    boolean unsavePossible = true;
                    final Batch next = getCaster().getNext( this );
                    if ( next != null && getEinsatzApk() == null )
                    {
                        if ( next.getEinsatzApk() == null && next.isEditable() && next.isToSave() )
                        {
                            unsavePossible = false;
                        }
                    }
                    if ( !unsavePossible )
                    {
                        Platform.runLater( () -> toSave.set( true ) );
                    }
                }
            }
        } );
        transferMaterials = new UndoObservableList<>( "batch.transferMaterials" );

        bottomWeight = new BatchBottomWeightBinding( this );
        numberOfTransferProcesses = new BatchNumberOfTransferProcessesBinding( this );
        sumTransferWeights = new SumTransferWeightsBinding( this );
        specConform = new BatchSpecConformBinding( this );
        length = new BatchLengthBinding( this );
        allTransfersRemoved = new BatchRemovedBinding( this );
        makeEmptyEditable = new BatchMakeEmptyEditableBinding( this );

        undoables = new IUndoable[] { furnaceTargetWeight, castingWeight, transfers, transferMaterials, makeEmpty, alloyChange, from };

        transfers.addListener( new ListChangeListener<Transfer>()
        {
            @Override
            public void onChanged( Change<? extends Transfer> changes )
            {
                while ( changes.next() )
                {
                    if ( changes.wasAdded() )
                    {
                        for ( Transfer transfer : changes.getAddedSubList() )
                        {
                            transfer.setBatch( Batch.this );
                        }
                    }
                    if ( changes.wasRemoved() )
                    {
                        for ( Transfer transfer : changes.getRemoved() )
                        {
                            transfer.setBatch( null );
                        }
                    }
                }
            }
        } );
    }

    @Override
    public void saveState()
    {
        for ( IUndoable undoable : undoables )
        {
            undoable.saveState();
        }
    }

    @Override
    public void undo()
    {
        for ( IUndoable undoable : undoables )
        {
            undoable.undo();
        }
    }

    @Override
    public boolean isChanged()
    {
        for ( IUndoable undoable : undoables )
        {
            if ( undoable.isChanged() )
            {
                return true;
            }
        }
        return false;
    }

    public String getObjectPath()
    {
        return getFrom().getName() + " " + getName();
    }

    @Override
    public void validate( ValidationResult validationResult )
    {
        allTransfersRemoved.invalidate();
        if ( getAllTransfersRemoved() && getBottomWeight() < getCastingWeight() )
        {
            validationResult.addError( this, getObjectPath() + ": Sumpfgewicht nicht ausreichend" );
        }
        if ( getFurnaceTargetWeight() < castingWeight.get() )
        {
            validationResult.addError( this, getObjectPath() + ": Abgussmenge zu groß" );
        }
        for ( Transfer transfer : transfers )
        {
            transfer.validate( validationResult );
        }
    }

    public void addTransfer( List<Transfer> newTransfers, int indexOfTransferProcess, boolean insert )
    {
        if ( insert )
        {
            Collections.reverse( newTransfers );
            newTransfers.stream().forEach( transfer -> {
                transfer.setTransferProcessIndex( indexOfTransferProcess );
                transfers.add( indexOfTransferProcess * 2, transfer );
            } );

            for ( int i = ( getNumberOfTransferProcesses() - 1 ); i >= indexOfTransferProcess; i-- )
            {
                List<Transfer> currentProcessTransfers = getTransfers( i );
                // rename transfers
                for ( Transfer currentProcessTransfer : currentProcessTransfers )
                {
                    currentProcessTransfer.setTransferProcessIndex( i + 1 );
                    currentProcessTransfer.setName( currentProcessTransfer.getFrom().getName() + "." + N2F.format( i + 2 ) );
                }
            }
        }
        else
        {
            transfers.addAll( newTransfers );
        }
        newTransfers.stream().forEach( transfer -> transfer.setTransferProcessIndex( indexOfTransferProcess ) );
    }

    public void removeTransfer( Transfer transfer )
    {
        // Handelt is sich um einen FurnaceTransfer, so muss das Zielmaterial
        // noch gelöscht werden
        if ( transfer.getFurnaceTransferMaterial() != null )
        {
            FurnaceTransferMaterial furnaceTransferMaterial = transfer.getFurnaceTransferMaterial();
            NamedModelElement modelElement = furnaceTransferMaterial.getTarget();
            if ( modelElement instanceof Transfer )
            {
                ( (Transfer) modelElement ).removeTransferMaterial( furnaceTransferMaterial );
            }
            // TODO TNT muss noch
            else if ( modelElement instanceof Batch )
            {

            }
        }
        transfer.remove();
        transfers.remove( transfer );
    }

    public List<Transfer> getTransfers( int indexOfTransferProcess )
    {
        List<Transfer> processTransfers = new ArrayList<>();
        for ( Transfer transfer : transfers )
        {
            if ( transfer.getTransferProcessIndex() == indexOfTransferProcess )
            {
                processTransfers.add( transfer );
            }
        }
        return processTransfers;
    }

    /*
    public List<Transfer> getTransfers( EFurnace furnace )
    {
        List<Transfer> furnaceTransfers = new ArrayList<>();
        for ( Transfer transfer : transfers )
        {
            if ( transfer.getFrom().getName().equals( furnace.getApk() ) )
            {
                furnaceTransfers.add( transfer );
            }
        }

        return furnaceTransfers;
    }
     */

    public Transfer getCorrespondingTransfer( Transfer transfer )
    {
        if ( transfer == null )
        {
            return null;
        }
        List<Transfer> processTransfers = getTransfers( transfer.getTransferProcessIndex() );
        if ( processTransfers.size() == 1 )
        {
            return null;
        }
        if ( processTransfers.contains( transfer ) )
        {
            int index = processTransfers.indexOf( transfer );
            if ( index == 0 )
            {
                return processTransfers.get( 1 );
            }
            else
            {
                return processTransfers.get( 0 );
            }
        }
        return null;
    }

    public Transfer getPrevious( Transfer transfer )
    {
        return findPrevious( this, transfer );
    }

    public Transfer getNext( Transfer transfer )
    {
        return findNext( this, transfer );
    }

    private Transfer findPrevious( Batch batch, Transfer currentTransfer )
    {
        if ( batch == null )
        {
            return null;
        }

        List<Transfer> prevTransfers = null;
        if ( currentTransfer.getBatch().equals( batch ) )
        {
            int processIndex = currentTransfer.getTransferProcessIndex();
            if ( processIndex == 0 )
            {
                Batch prevBatch = batch.getCaster().getPrevious( batch );
                return findPrevious( prevBatch, currentTransfer );
            }
            prevTransfers = batch.getTransfers( processIndex - 1 );
        }
        else
        {
            prevTransfers = batch.getTransfers( batch.getNumberOfTransferProcesses() - 1 );
        }

        if ( prevTransfers != null )
        {
            for ( Transfer prevTransfer : prevTransfers )
            {
                if ( prevTransfer != null && prevTransfer.getFrom() != null && prevTransfer.getFrom().equals( currentTransfer.getFrom() ) )
                {
                    // if (prevTransfer.getWeight() <= 0)
                    // {
                    // return findPrevious(prevTransfer.getBatch(),
                    // prevTransfer);
                    // }
                    return prevTransfer;
                }
            }
            // Den Batch davor suchen
            Batch prevBatch = batch.getCaster().getPrevious( batch );
            return findPrevious( prevBatch, currentTransfer );
        }

        return null;
    }

    private Transfer findNext( Batch batch, Transfer currentTransfer )
    {
        if ( batch == null )
        {
            return null;
        }

        List<Transfer> nextTransfers = null;
        if ( currentTransfer.getBatch().equals( batch ) )
        {
            int processIndex = currentTransfer.getTransferProcessIndex();
            if ( ( processIndex + 1 ) >= batch.getNumberOfTransferProcesses() )
            {
                Batch nextBatch = batch.getCaster().getNext( batch );
                return findNext( nextBatch, currentTransfer );
            }
            nextTransfers = batch.getTransfers( processIndex + 1 );
        }
        else
        {
            nextTransfers = batch.getTransfers( 0 );
        }

        if ( nextTransfers != null )
        {
            for ( Transfer nextTransfer : nextTransfers )
            {
                if ( nextTransfer != null && nextTransfer.getFrom() != null && nextTransfer.getFrom().equals( currentTransfer.getFrom() ) )
                {
                    // if (nextTransfer.getWeight() <= 0)
                    // {
                    // return findNext(nextTransfer.getBatch(), nextTransfer);
                    // }
                    return nextTransfer;
                }
            }
        }

        return null;
    }

    public ObservableList<Transfer> getTransfers()
    {
        return transfers;
    }

    public void clearTransfers()
    {
        for ( Transfer transfer : new ArrayList<Transfer>( transfers ) )
        {
            removeTransfer( transfer );
        }
    }

    public ObservableList<Transfer> transfersProperty()
    {
        return transfers;
    }

    public double getCastingWeight()
    {
        return castingWeight.get();
    }

    public void setCastingWeight( double castingWeight )
    {
        this.castingWeight.set( castingWeight );
    }

    public SimpleDoubleProperty castingWeightProperty()
    {
        return castingWeight;
    }

    public double getFurnaceTargetWeight()
    {
        return furnaceTargetWeight.get();
    }

    public void setFurnaceTargetWeight( double furnaceTargetWeight )
    {
        this.furnaceTargetWeight.set( furnaceTargetWeight );
    }

    public SimpleDoubleProperty furnaceTargetWeightProperty()
    {
        return furnaceTargetWeight;
    }

    public Specification getSpecification()
    {
        return specification.get();
    }

    public void setSpecification( Specification specification )
    {
        this.specification.set( specification );
    }

    public SimpleObjectProperty<Specification> specificationProperty()
    {
        return specification;
    }

    public String getDescription()
    {
        return description.get();
    }

    public void setDescription( String description )
    {
        this.description.set( description );
    }

    public SimpleStringProperty descriptionProperty()
    {
        return description;
    }

    public String getDetail()
    {
        return detail.get();
    }

    public void setDetail( String detail )
    {
        this.detail.set( detail );
    }

    public SimpleStringProperty detailProperty()
    {
        return detail;
    }

    public double getBottomWeight()
    {
        return bottomWeight.get();
    }

    public DoubleBinding bottomWeightProperty()
    {
        return bottomWeight;
    }

    public IntegerBinding numberOfTransferProcessesProperty()
    {
        return this.numberOfTransferProcesses;
    }

    public int getNumberOfTransferProcesses()
    {
        return this.numberOfTransferProcessesProperty().get();
    }

    public final DoubleBinding sumTransferWeightsProperty()
    {
        return this.sumTransferWeights;
    }

    public final double getSumTransferWeights()
    {
        return this.sumTransferWeightsProperty().get();
    }

    public boolean isSumTransferWeightEqualsFurnaceTargetWeight()
    {
        final long sumTransferWeightsValue = Math.round( getSumTransferWeights() / 100. );
        final long furnaceTargetWeightValue = Math.round( getFurnaceTargetWeight() / 100. );
        return sumTransferWeightsValue == furnaceTargetWeightValue;
    }

    public final BooleanBinding allTransfersRemovedProperty()
    {
        return this.allTransfersRemoved;
    }

    public final boolean getAllTransfersRemoved()
    {
        return this.allTransfersRemovedProperty().get();
    }

    public Furnace getFrom()
    {
        return from.get();
    }

    public void setFrom( Furnace from )
    {
        this.from.set( from );
    }

    public SimpleObjectProperty<Furnace> fromProperty()
    {
        return from;
    }

    public boolean isMakeEmpty()
    {
        return makeEmpty.get();
    }

    public void setMakeEmpty( boolean makeEmpty )
    {
        this.makeEmpty.set( makeEmpty );
    }

    public SimpleBooleanProperty makeEmptyProperty()
    {
        return makeEmpty;
    }

    public boolean isAlloyChange()
    {
        return alloyChange.get();
    }

    public void setAlloyChange( boolean alloyChange )
    {
        this.alloyChange.set( alloyChange );
    }

    public SimpleBooleanProperty alloyChangeProperty()
    {
        return alloyChange;
    }

    public final SimpleBooleanProperty editableProperty()
    {
        return this.editable;
    }

    public final boolean isEditable()
    {
        return this.editableProperty().get();
    }

    public final void setEditable( final boolean editable )
    {
        this.editableProperty().set( editable );
    }

    public final SimpleBooleanProperty toSaveProperty()
    {
        return this.toSave;
    }

    public final boolean isToSave()
    {
        return this.toSaveProperty().get();
    }

    public final void setToSave( final boolean toSave )
    {
        this.toSaveProperty().set( toSave );
    }

    public final BooleanBinding makeEmptyEditableProperty()
    {
        return this.makeEmptyEditable;
    }

    public final boolean isMakeEmptyEditable()
    {
        return this.makeEmptyEditableProperty().get();
    }

    public long getReplicTimeMillis()
    {
        return replicTimeMillis;
    }

    public void setReplicTimeMillis( long replicTimeMillis )
    {
        this.replicTimeMillis = replicTimeMillis;
    }

    public ObservableList<TransferMaterial> getTransferMaterials()
    {
        return transferMaterials;
    }

    public void addTransferMaterial( TransferMaterial transferMaterial )
    {
        transferMaterials.add( transferMaterial );
    }

    public void removeTransferMaterial( TransferMaterial transferMaterial )
    {
        transferMaterials.remove( transferMaterial );
    }

    public Caster getCaster()
    {
        return caster;
    }

    void setCaster( Caster caster )
    {
        this.caster = caster;
    }

    protected String getSpecConform()
    {
        return StringEscapeUtils.escapeJava( specConform.get() );
    }

    protected void setSpecConform( String specConform )
    {
        // do nothing
    }

    public StringBinding specConformProperty()
    {
        return specConform;
    }

    public final DoubleBinding lengthProperty()
    {
        return this.length;
    }

    public final double getLength()
    {
        return this.lengthProperty().get();
    }

    public String getFauf()
    {
        return fauf;
    }

    public void setFauf( String fauf )
    {
        this.fauf = fauf;
    }

    public String getCustomer()
    {
        return customer;
    }

    public void setCustomer( String customer )
    {
        this.customer = customer;
    }

    public String getAlloy()
    {
        return alloy;
    }

    public void setAlloy( String alloy )
    {
        this.alloy = alloy;
    }

    public String getAlloyNbr()
    {
        return alloyNbr;
    }

    public void setAlloyNbr( String alloyNbr )
    {
        this.alloyNbr = alloyNbr;
    }

    public String getProduct()
    {
        return product;
    }

    public void setProduct( String product )
    {
        this.product = product;
    }

    public int getIndex()
    {
        return index;
    }

    public void setIndex( int index )
    {
        this.index = index;
    }

    public Analysis getBottomAnalysis()
    {
        if ( bottomAnalysis != null )
        {
            return bottomAnalysis;
        }
        final Batch previousBatch;
        if ( caster != null )
        {
            previousBatch = caster.getPrevious( this );
        }
        else
        {
            previousBatch = null;
        }
        if ( previousBatch != null )
        {
            bottomAnalysis = new Analysis();
            bottomAnalysis.setName( "Sumpf" );
            AnalysisCalculator.fillAnalysisWithSpecification( bottomAnalysis, previousBatch.getSpecification() );
        }
        else if ( from.get() instanceof Furnace )
        {
            bottomAnalysis = from.get().getAnalysis();
        }
        return bottomAnalysis;
    }

    public void setBottomAnalysis( Analysis bottomAnalysis )
    {
        this.bottomAnalysis = bottomAnalysis;
    }

    public double getFurnaceMaterialWeight()
    {
        double transferMaterialWeight = 0;
        for ( TransferMaterial transferMaterial : transferMaterials )
        {
            transferMaterialWeight = transferMaterialWeight + transferMaterial.getWeight();
        }
        return getBottomWeight() + transferMaterialWeight;
    }

    @Override
    public void invalidate()
    {
        bottomWeight.invalidate();
        numberOfTransferProcesses.invalidate();
        sumTransferWeights.invalidate();
        specConform.invalidate();
        length.invalidate();
        allTransfersRemoved.invalidate();
        makeEmptyEditable.invalidate();

        for ( Transfer transfer : transfers )
        {
            transfer.invalidate();
        }
    }

    public final Material getSpecMaterial()
    {
        return this.specMaterial;
    }

    public final void setSpecMaterial( final Material specMaterial )
    {
        this.specMaterial = specMaterial;
    }

    public final Material getFillMaterial()
    {
        return this.fillMaterial;
    }

    public final void setFillMaterial( final Material fillMaterial )
    {
        this.fillMaterial = fillMaterial;
    }

    public String getEinsatzApk()
    {
        return einsatzApk;
    }

    public void setEinsatzApk( String einsatzApk )
    {
        this.einsatzApk = einsatzApk;
    }

    public double getCastingLength()
    {
        return castingLength;
    }

    public void setCastingLength( double castingLength )
    {
        this.castingLength = castingLength;
    }

    public int getAmount()
    {
        return amount;
    }

    public void setAmount( int amount )
    {
        this.amount = amount;
    }

    public double getWidth()
    {
        return width;
    }

    public void setWidth( double width )
    {
        this.width = width;
    }

    public double getHeight()
    {
        return height;
    }

    public void setHeight( double height )
    {
        this.height = height;
    }

    public double getDensity()
    {
        return density;
    }

    public void setDensity( double density )
    {
        this.density = density;
    }

    public Double[] getPosWidths()
    {
        return posWidths;
    }

    public void setPosWidths( Double[] posWidths )
    {
        this.posWidths = posWidths;
    }

    public boolean isSingleFurnace()
    {
        return true;
    }

    public double getTransferIndexWeight( int transferIndex )
    {
        double transferWeight = 0;
        List<Transfer> transfers = getTransfers( transferIndex );
        for ( Transfer transfer : transfers )
        {
            if ( transfer.isRemoved() || transfer.getFurnaceTransferMaterial() != null )
            {
                continue;
            }
            transferWeight = transferWeight + transfer.getWeight();
        }
        return transferWeight;
    }

    public double getMaxWeight()
    {
        double maxWeight = getFurnaceTargetWeight();
        if ( getSumTransferWeights() > maxWeight )
        {
            maxWeight = getSumTransferWeights();
        }
        return maxWeight;
    }

    @Override
    public String toString()
    {
        return "Batch{"+ getName() +"}";
    }
}