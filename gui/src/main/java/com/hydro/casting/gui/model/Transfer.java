package com.hydro.casting.gui.model;

import com.hydro.casting.gui.model.calc.*;
import com.hydro.casting.gui.model.common.AnalysisCalculator;
import com.hydro.casting.gui.model.common.ETransferMaterialType;
import com.hydro.casting.gui.model.undo.IUndoable;
import com.hydro.casting.gui.model.undo.UndoBooleanProperty;
import com.hydro.casting.gui.model.undo.UndoDoubleProperty;
import com.hydro.casting.gui.model.undo.UndoObservableList;
import com.hydro.casting.gui.model.validation.IValidationable;
import com.hydro.casting.gui.model.validation.ValidationResult;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.apache.activemq.artemis.api.core.management.Attribute;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;

public class Transfer extends NamedModelElement implements IUndoable, IValidationable
{
    public final static String DND_IDENT = "Transfer:";
    private static final long serialVersionUID = 1L;
    private final UndoObservableList<TransferMaterial> transferMaterials;

    private final SimpleObjectProperty<Furnace> from;

    private final UndoDoubleProperty weight;

    private final UndoDoubleProperty targetWeight;

    private final UndoBooleanProperty makeEmpty;

    private final UndoBooleanProperty removed;

    private final UndoBooleanProperty noInput;

    //private final UndoBooleanProperty useStandardSpec;

    private Batch batch;

    private final SimpleObjectProperty<FurnaceTransferMaterial> furnaceTransferMaterial;

    private String versatzApk;

    private final TransferBottomWeightBinding bottomWeight;
    private final TransferSpecConformBinding specConform;
    private final TransferRemoveAllowedBinding removeAllowed;
    private final TransferNoInputAllowedBinding noInputAllowed;
    private final TransferMakeEmptyEditableBinding makeEmptyEditable;
    private final TransferFillmentTransferMaterialBinding transferFillmentTransferMaterialBinding;

    private int transferProcessIndex;

    private final IUndoable[] undoables;

    private final BooleanProperty ignoreWarning;

    private Material specMaterial;
    private Material fillMaterial;

    public Transfer()
    {
        transferMaterials = new UndoObservableList<>( "transfer.transferMaterials" );
        from = new SimpleObjectProperty<Furnace>();
        weight = new UndoDoubleProperty();
        targetWeight = new UndoDoubleProperty();
        makeEmpty = new UndoBooleanProperty();
        removed = new UndoBooleanProperty();
        noInput = new UndoBooleanProperty();
        furnaceTransferMaterial = new SimpleObjectProperty<>();
        //useStandardSpec = new UndoBooleanProperty();
        ignoreWarning = new SimpleBooleanProperty();

        removeAllowed = new TransferRemoveAllowedBinding( this );
        noInputAllowed = new TransferNoInputAllowedBinding( this );
        bottomWeight = new TransferBottomWeightBinding( this );
        specConform = new TransferSpecConformBinding( this );
        makeEmptyEditable = new TransferMakeEmptyEditableBinding( this );
        transferFillmentTransferMaterialBinding = new TransferFillmentTransferMaterialBinding( this );

        from.addListener( ( p, o, n ) -> {
            if ( o instanceof Furnace )
            {
                o.getTransfers().remove( this );
            }
            if ( n instanceof Furnace )
            {
                n.getTransfers().add( this );
            }
        } );
        transferMaterials.addListener( new ListChangeListener<TransferMaterial>()
        {
            @Override
            public void onChanged( Change<? extends TransferMaterial> changes )
            {
                while ( changes.next() )
                {
                    if ( changes.wasAdded() )
                    {
                        for ( TransferMaterial transfer : changes.getAddedSubList() )
                        {
                            transfer.setTransfer( Transfer.this );
                        }
                    }
                    if ( changes.wasRemoved() )
                    {
                        for ( TransferMaterial transfer : changes.getRemoved() )
                        {
                            transfer.setTransfer( null );
                        }
                    }
                }
            }
        } );

        //undoables = new IUndoable[] { transferMaterials, weight, targetWeight, makeEmpty, removed, noInput, useStandardSpec };
        undoables = new IUndoable[] { transferMaterials, weight, targetWeight, makeEmpty, removed, noInput };
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
        if ( batch == null )
        {
            return "Batch null " + getName();
        }
        return getBatch().getObjectPath() + " " + getName();
    }

    @Override
    public void validate( ValidationResult validationResult )
    {
        if ( isRemoved() )
        {
            return;
        }
        if ( Double.isInfinite( weight.get() ) || Double.isNaN( weight.get() ) )
        {
            validationResult.addError( this, getObjectPath() + ": Ungültige Werte fürs Gewicht" );
        }
        if ( Double.isInfinite( bottomWeight.get() ) || Double.isNaN( bottomWeight.get() ) )
        {
            validationResult.addError( this, getObjectPath() + ": Ungültige Werte für den Sumpf" );
        }
        if ( weight.get() < 0 )
        {
            validationResult.addError( this, getObjectPath() + ": Negative Abgußmenge" );
        }
        if ( weight.get() > targetWeight.get() )
        {
            validationResult.addError( this, getObjectPath() + ": Abgußmenge zu groß" );
        }
        if ( bottomWeight.get() > targetWeight.get() )
        {
            validationResult.addError( this, getObjectPath() + ": Sumpfgewicht zu groß" );
        }
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

    public Batch getBatch()
    {
        return batch;
    }

    public void setBatch( Batch batch )
    {
        this.batch = batch;
    }

    @Attribute
    public double getWeight()
    {
        return weight.get();
    }

    @Attribute
    public void setWeight( double weight )
    {
        if ( weight < 0.1 )
        {
            this.weight.set( 0.1 );
            return;
        }
        this.weight.set( weight );
    }

    public void addWeight( double weightToAdd )
    {
        if ( furnaceTransferMaterial.get() != null )
        {
            furnaceTransferMaterial.get().setWeight( furnaceTransferMaterial.get().getWeight() + weightToAdd );
            return;
        }
        setWeight( weight.get() + weightToAdd );
    }

    public void subtractWeight( double weightToSubtract )
    {
        if ( furnaceTransferMaterial.get() != null )
        {
            furnaceTransferMaterial.get().setWeight( furnaceTransferMaterial.get().getWeight() - weightToSubtract );
            return;
        }
        setWeight( weight.get() - weightToSubtract );
    }

    public int getTransferProcessIndex()
    {
        return transferProcessIndex;
    }

    public void setTransferProcessIndex( int transferProcessIndex )
    {
        this.transferProcessIndex = transferProcessIndex;
    }

    public SimpleDoubleProperty weightProperty()
    {
        return weight;
    }

    public SimpleDoubleProperty targetWeightProperty()
    {
        return this.targetWeight;
    }

    public double getTargetWeight()
    {
        return this.targetWeightProperty().get();
    }

    public void setTargetWeight( final double targetWeight )
    {
        this.targetWeightProperty().set( targetWeight );
    }

    public DoubleBinding bottomWeightProperty()
    {
        return this.bottomWeight;
    }

    public double getBottomWeight()
    {
        return this.bottomWeightProperty().get();
    }

    public boolean isMakeEmpty()
    {
        return makeEmpty.get();
    }

    public void setMakeEmpty( boolean makeEmpty )
    {
        this.makeEmpty.set( makeEmpty );
    }

    public final BooleanBinding makeEmptyEditableProperty()
    {
        return this.makeEmptyEditable;
    }

    public final boolean isMakeEmptyEditable()
    {
        return this.makeEmptyEditableProperty().get();
    }

    public SimpleBooleanProperty makeEmptyProperty()
    {
        return makeEmpty;
    }

    public final SimpleBooleanProperty removedProperty()
    {
        return this.removed;
    }

    public final boolean isRemoved()
    {
        return this.removedProperty().get();
    }

    public final void setRemoved( final boolean removed )
    {
        this.removedProperty().set( removed );
    }

    public final SimpleBooleanProperty noInputProperty()
    {
        return this.noInput;
    }

    public final boolean isNoInput()
    {
        return this.noInputProperty().get();
    }

    public final void setNoInput( final boolean noInput )
    {
        this.noInputProperty().set( noInput );
    }

    public final BooleanBinding removeAllowedProperty()
    {
        return this.removeAllowed;
    }

    public final boolean isRemoveAllowed()
    {
        return this.removeAllowedProperty().get();
    }

    public final BooleanBinding noInputAllowedProperty()
    {
        return this.noInputAllowed;
    }

    public final boolean isNoInputAllowed()
    {
        return this.noInputAllowedProperty().get();
    }

    /*
    public final UndoBooleanProperty useStandardSpecProperty()
    {
        return this.useStandardSpec;
    }

    public final boolean isUseStandardSpec()
    {
        return this.useStandardSpecProperty().get();
    }

    public final void setUseStandardSpec( final boolean useStandardSpec )
    {
        this.useStandardSpecProperty().set( useStandardSpec );
    }
     */

    public final SimpleObjectProperty<FurnaceTransferMaterial> furnaceTransferMaterialProperty()
    {
        return this.furnaceTransferMaterial;
    }

    public final FurnaceTransferMaterial getFurnaceTransferMaterial()
    {
        return this.furnaceTransferMaterialProperty().get();
    }

    public final void setFurnaceTransferMaterial( final FurnaceTransferMaterial furnaceTransferMaterial )
    {
        this.furnaceTransferMaterialProperty().set( furnaceTransferMaterial );
    }

    public BooleanProperty ignoreWarningProperty()
    {
        return ignoreWarning;
    }

    public boolean isIgnoreWarning()
    {
        return ignoreWarningProperty().get();
    }

    public void setIgnoreWarning( boolean ignoreWarning )
    {
        ignoreWarningProperty().set( ignoreWarning );
    }

    public Analysis getBottomAnalysis()
    {
        final int processIndex = getTransferProcessIndex();
        if ( processIndex > 0 )
        {
            List<Transfer> prevTransfers = getBatch().getTransfers( processIndex - 1 );
            if ( prevTransfers != null )
            {
                for ( Transfer prevTransfer : prevTransfers )
                {
                    if ( prevTransfer.getFrom().equals( getFrom() ) )
                    {
                        return AnalysisCalculator.createAverage( "Sumpf", prevTransfer, false );
                    }
                }
            }
        }

        if ( batch.getCaster() != null && batch.getCaster().getBatches() != null && batch.getCaster().getBatches().indexOf( getBatch() ) == 0 && getFrom() instanceof Furnace )
        {
            return getFrom().getAnalysis();
        }

        return batch.getBottomAnalysis();
    }

    protected String getSpecConform()
    {
        return StringEscapeUtils.escapeJava( specConform.get() );
    }

    protected void setSpecConform( String specConform )
    {
        // do nothing
    }

    public TransferSpecConformBinding specConformProperty()
    {
        return specConform;
    }

    public double getFreeWeight( boolean withVariableType )
    {
        // AS Weight
        double freeWeight = 0;
        if ( getFillmentTransferMaterial() != null )
        {
            freeWeight = getFillmentTransferMaterial().getWeight();
        }
        if ( getTransferMaterials().isEmpty() == false )
        {
            // add variable materials weight
            if ( withVariableType )
            {
                List<TransferMaterial> transferMaterials = getTransferMaterials();
                for ( TransferMaterial transferMaterial : transferMaterials )
                {
                    ETransferMaterialType type = ETransferMaterialType.findType( transferMaterial.getType() );
                    if ( type == ETransferMaterialType.VARIABLE )
                    {
                        freeWeight = freeWeight + transferMaterial.getWeight();
                    }
                }
            }
        }
        return freeWeight;
    }

    //    public Material getSpecMaterial()
    //    {
    //        // TODO muss umgestellt werden AS TransferMaterial separat ausweisen
    //        if ( getTransferMaterials().isEmpty() == false && getTransferMaterials().get( 0 ) != null )
    //        {
    //            // AS Weight
    //            return getTransferMaterials().get( 0 ).getSource();
    //        }
    //        return null;
    //    }

    public TransferMaterial getFillmentTransferMaterial()
    {
        return transferFillmentTransferMaterialBinding.get();
    }

    public String getVersatzApk()
    {
        return versatzApk;
    }

    public void setVersatzApk( String versatzApk )
    {
        this.versatzApk = versatzApk;
    }

    public Material getSpecMaterial()
    {
        return specMaterial;
    }

    public void setSpecMaterial( Material specMaterial )
    {
        this.specMaterial = specMaterial;
    }

    public Material getFillMaterial()
    {
        return fillMaterial;
    }

    public void setFillMaterial( Material fillMaterial )
    {
        this.fillMaterial = fillMaterial;
    }

    @Override
    public void invalidate()
    {
        bottomWeight.invalidate();
        specConform.invalidate();
        removeAllowed.invalidate();
        noInputAllowed.invalidate();
        makeEmptyEditable.invalidate();
        transferFillmentTransferMaterialBinding.invalidate();

        if ( isNoInput() )
        {
            setTargetWeight( getBottomWeight() + 0.1 );
        }
    }

    @Override
    public String toString()
    {
        return "Transfer [transferMaterials=" + transferMaterials + ", from=" + from + ", weight=" + weight.get() + ", objid=" + getObjid() + "]";
    }

    public void remove()
    {
        for ( TransferMaterial transferMaterial : new ArrayList<>( transferMaterials ) )
        {
            // Löschen des erzeugenden FurnaceTransfers
            if ( transferMaterial instanceof FurnaceTransferMaterial )
            {
                final FurnaceTransferMaterial furnaceTransferMaterial = (FurnaceTransferMaterial) transferMaterial;

                if ( furnaceTransferMaterial != null && furnaceTransferMaterial.getSourceTransfer() != null && furnaceTransferMaterial.getSourceTransfer().getBatch() != null )
                {

                    final Batch batch = furnaceTransferMaterial.getSourceTransfer().getBatch();

                    int currentIndexOfTransferProcess = furnaceTransferMaterial.getSourceTransfer().getTransferProcessIndex();
                    List<Transfer> transfersToDelete = batch.getTransfers( currentIndexOfTransferProcess );

                    for ( Transfer transfer : new ArrayList<>( transfersToDelete ) )
                    {
                        batch.removeTransfer( transfer );
                    }
                }
            }
        }
    }
}
