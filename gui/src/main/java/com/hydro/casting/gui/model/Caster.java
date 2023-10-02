package com.hydro.casting.gui.model;

import com.hydro.casting.gui.model.calc.CasterLengthSumBinding;
import com.hydro.casting.gui.model.undo.IUndoable;
import com.hydro.casting.gui.model.undo.UndoObservableList;
import com.hydro.casting.gui.model.validation.IValidationable;
import com.hydro.casting.gui.model.validation.ValidationResult;
import com.hydro.core.gui.model.ClientModel;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Caster extends Resource implements IUndoable, IValidationable
{
    private static final long serialVersionUID = 1L;

    private final UndoObservableList<Batch> batches;

    private final Map<Long, Batch> batchIndex = new HashMap<>();

    private final CasterLengthSumBinding lengthSum;

    private final IUndoable[] undoables;

    private final ObjectProperty<Transfer> selectedTransfer;

    private List<Furnace> furnaces = new ArrayList<>();

    private ClientModel clientModel;

    public Caster(ClientModel clientModel)
    {
        this.clientModel = clientModel;

        batches = new UndoObservableList<>( "batches" );

        selectedTransfer = new SimpleObjectProperty<>();

        lengthSum = new CasterLengthSumBinding( batches );

        undoables = new IUndoable[] { batches };
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

    @Override
    public void validate( ValidationResult validationResult )
    {
        for ( Batch batch : batches )
        {
            batch.validate( validationResult );
        }
    }

    public ObservableList<Batch> getBatches()
    {
        return batches;
    }

    public Batch getNext( Batch batch )
    {
        int batchIndex = batches.indexOf( batch );
        if ( batches.size() > ( batchIndex + 1 ) )
        {
            return batches.get( batchIndex + 1 );
        }
        return null;
    }

    public Batch getPrevious( Batch batch )
    {
        int batchIndex = batches.indexOf( batch );
        if ( batchIndex > 0 )
        {
            return batches.get( batchIndex - 1 );
        }
        return null;
    }

    public void addBatch( Batch batch )
    {
        batch.setCaster( this );
        batches.add( batch );
        batchIndex.put( batch.getObjid(), batch );
    }

    public void removeBatch( Batch batch )
    {
        batch.setCaster( null );
        batches.remove( batch );
        batchIndex.remove( batch.getObjid() );
    }

    public List<Furnace> getFurnaces()
    {
        return furnaces;
    }

    public void addFurnace( Furnace furnace )
    {
        furnace.setCaster( this );
        furnaces.add( furnace );
    }

    public Batch findBatch( long batchObjid )
    {
        return batchIndex.get( batchObjid );
    }

    public void clear()
    {
        setSelectedTransfer( null );
        for ( Batch batch : new ArrayList<Batch>( batches ) )
        {
            removeBatch( batch );
        }
    }

    public DoubleBinding lengthSumProperty()
    {
        return lengthSum;
    }

    public double getLengthSum()
    {
        return lengthSumProperty().get();
    }

    public final ObjectProperty<Transfer> selectedTransferProperty()
    {
        return this.selectedTransfer;
    }

    public final Transfer getSelectedTransfer()
    {
        return this.selectedTransferProperty().get();
    }

    public final void setSelectedTransfer( final Transfer selectedTransfer )
    {
        this.selectedTransferProperty().set( selectedTransfer );
    }

    public ClientModel getClientModel()
    {
        return clientModel;
    }

    public void setClientModel( ClientModel clientModel )
    {
        this.clientModel = clientModel;
    }

    @Override
    public String toString()
    {
        return "Caster [name=" + getName() + ", objid=" + getObjid() + "]";
    }

    @Override
    public void invalidate()
    {
        lengthSum.invalidate();

        for ( Batch batch : batches )
        {
            batch.invalidate();
        }
        for ( Furnace furnace : furnaces )
        {
            furnace.invalidate();
        }
    }
}
