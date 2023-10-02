package com.hydro.casting.gui.model;

import com.hydro.casting.gui.model.calc.TransferMaterialWeightBinding;
import com.hydro.casting.gui.model.undo.IUndoable;
import com.hydro.casting.gui.model.undo.UndoDoubleProperty;
import com.hydro.casting.gui.model.undo.UndoObjectProperty;
import com.hydro.casting.gui.model.undo.UndoStringProperty;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import org.apache.activemq.artemis.api.core.management.Attribute;

public class TransferMaterial extends NamedModelElement implements IUndoable
{
    private static final long serialVersionUID = 1L;
    private final UndoDoubleProperty weight;
    private final UndoStringProperty type;
    private final UndoObjectProperty<Material> source;
    private final TransferMaterialWeightBinding weightBinding;
    private final IUndoable[] undoables;
    private Transfer transfer;

    public TransferMaterial()
    {
        weight = new UndoDoubleProperty();
        type = new UndoStringProperty();
        source = new UndoObjectProperty<>();

        weightBinding = new TransferMaterialWeightBinding( this );

        undoables = new IUndoable[] { weight, type, source };
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

    public Transfer getTransfer()
    {
        return transfer;
    }

    public void setTransfer( Transfer transfer )
    {
        if ( this.transfer == null && transfer != null && transfer.getTargetWeight() != 0 )
        {
            weight.set( weight.get() / transfer.getTargetWeight() );
        }
        this.transfer = transfer;
    }

    @Attribute
    public double getWeight()
    {
        if ( transfer == null )
        {
            return weight.get();
        }
        else
        {
            return transfer.getTargetWeight() * weight.get();
        }
    }

    @Attribute
    public void setWeight( double weight )
    {
        if ( transfer == null )
        {
            this.weight.set( weight );
        }
        else
        {
            if ( transfer.getTargetWeight() == 0 )
            {
                this.weight.set( 0 );
            }
            else
            {
                this.weight.set( weight / transfer.getTargetWeight() );
            }
        }
    }

    public void addWeight( double weightToAdd )
    {
        setWeight( getWeight() + weightToAdd );
    }

    public void subtractWeight( double weightToSubtract )
    {
        setWeight( getWeight() - weightToSubtract );
    }

    public DoubleBinding weightProperty()
    {
        return weightBinding;
    }

    public Material getSource()
    {
        return sourceProperty().get();
    }

    public void setSource( Material source )
    {
        sourceProperty().set( source );
    }

    public ObjectProperty<Material> sourceProperty()
    {
        return source;
    }

    public final SimpleStringProperty typeProperty()
    {
        return this.type;
    }

    @Attribute
    public final String getType()
    {
        return this.typeProperty().get();
    }

    @Attribute
    public final void setType( final String type )
    {
        this.typeProperty().set( type );
    }

    @Override
    public String toString()
    {
        return "TransferMaterial [name=" + getName() + ", weight=" + getWeight() + ", source=" + getSource() + ", objid=" + getObjid() + "]";
    }

    public Analysis getAnalysis()
    {
        if ( getSource() != null )
        {
            return getSource().getAnalysis();
        }
        return null;
    }

    @Override
    public void invalidate()
    {
    }
}
