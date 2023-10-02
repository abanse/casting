package com.hydro.casting.gui.model;

import com.hydro.casting.gui.model.calc.FurnaceHeightMaxBinding;
import com.hydro.casting.gui.model.undo.IUndoable;
import com.hydro.casting.gui.model.undo.UndoDoubleProperty;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Furnace extends Resource implements IUndoable
{
    private static final long serialVersionUID = 1L;

    private final SimpleDoubleProperty maxWeight;
    private final UndoDoubleProperty actualWeight;
    private final SimpleObjectProperty<Analysis> analysis;

    private final ObservableList<Transfer> transfers;

    private final FurnaceHeightMaxBinding heightMax;

    private final IUndoable[] undoables;

    private Caster caster;

    public Furnace( String name )
    {
        setName( name );

        maxWeight = new SimpleDoubleProperty();
        actualWeight = new UndoDoubleProperty();
        analysis = new SimpleObjectProperty<Analysis>();

        transfers = FXCollections.observableArrayList();

        heightMax = new FurnaceHeightMaxBinding( this );

        undoables = new IUndoable[] { actualWeight };
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

    public Caster getCaster()
    {
        return caster;
    }

    public void setCaster( Caster caster )
    {
        this.caster = caster;
    }

    public double getMaxWeight()
    {
        return maxWeight.get();
    }

    public void setMaxWeight( double maxWeight )
    {
        this.maxWeight.set( maxWeight );
    }

    public SimpleDoubleProperty maxWeightProperty()
    {
        return maxWeight;
    }

    public double getActualWeight()
    {
        return actualWeight.get();
    }

    public void setActualWeight( double actualWeight )
    {
        this.actualWeight.set( actualWeight );
    }

    public SimpleDoubleProperty actualWeightProperty()
    {
        return actualWeight;
    }

    public SimpleObjectProperty<Analysis> analysisProperty()
    {
        return this.analysis;
    }

    public Analysis getAnalysis()
    {
        return this.analysisProperty().get();
    }

    public void setAnalysis( final Analysis analysis )
    {
        this.analysisProperty().set( analysis );
    }

    public ObservableList<Transfer> getTransfers()
    {
        return transfers;
    }

    public DoubleBinding heightMaxProperty()
    {
        return heightMax;
    }

    public double getHeightMax()
    {
        return heightMaxProperty().get();
    }

    @Override
    public void invalidate()
    {
        heightMax.invalidate();
    }

    @Override
    public String toString()
    {
        return "Furnace [name=" + getName() + ", objid=" + getObjid() + "]";
    }

}
