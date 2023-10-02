package com.hydro.casting.server.model.sched;

import com.hydro.casting.server.model.mat.Material;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Entity
@DiscriminatorValue( "operation" )
public class Operation extends Schedulable
{
    @OneToMany( mappedBy = "generatingOperation" )
    private Set<Material> generatedMaterials;
    @OneToMany( mappedBy = "consumingOperation" )
    private Set<Material> consumedMaterials;

    public Set<Material> getGeneratedMaterials()
    {
        // lazy evaluation; the field might still be null
        if ( generatedMaterials == null )
        {
            generatedMaterials = new HashSet<Material>();
        }
        return generatedMaterials;
    }

    public boolean containsInGeneratedMaterials( Material materialUnit )
    {
        return ( this.generatedMaterials != null ) && this.generatedMaterials.contains( materialUnit );
    }

    public int numberOfGeneratedMaterials()
    {
        // handle uninitialized set, but do not initialize it now
        return ( generatedMaterials == null ) ? 0 : generatedMaterials.size();
    }

    public void flushGeneratedMaterials()
    {
        if ( generatedMaterials != null )
        {
            Iterator<Material> iter = generatedMaterials.iterator();
            while ( iter.hasNext() )
            {
                iter.next().oneSided_setGeneratingOperation( null );
            }
            generatedMaterials = null;  // no concurrent modification
        }
    }

    public void addToGeneratedMaterials( Material materialUnit )
    {
        getGeneratedMaterials().add( materialUnit );
        Operation oldGeneratingOperation = materialUnit.getGeneratingOperation();
        if ( ( oldGeneratingOperation != this ) && ( oldGeneratingOperation != null ) )
        {
            oldGeneratingOperation.oneSided_removeFromGeneratedMaterials( materialUnit );
        }
        materialUnit.oneSided_setGeneratingOperation( this );
    }

    public void removeFromGeneratedMaterials( Material materialUnit )
    {
        getGeneratedMaterials().remove( materialUnit );
        materialUnit.oneSided_setGeneratingOperation( null );
    }

    public void oneSided_addToGeneratedMaterials( Material materialUnit )
    {
        getGeneratedMaterials().add( materialUnit );
    }

    public void oneSided_removeFromGeneratedMaterials( Material materialUnit )
    {
        getGeneratedMaterials().remove( materialUnit );
    }

    public Set<Material> getConsumedMaterials()
    {
        // lazy evaluation; the field might still be null
        if ( consumedMaterials == null )
        {
            consumedMaterials = new HashSet<Material>();
        }
        return consumedMaterials;
    }

    public boolean containsInConsumedMaterials( Material materialUnit )
    {
        return ( this.consumedMaterials != null ) && this.consumedMaterials.contains( materialUnit );
    }

    public int numberOfConsumedMaterials()
    {
        // handle uninitialized set, but do not initialize it now
        return ( consumedMaterials == null ) ? 0 : consumedMaterials.size();
    }

    public void flushConsumedMaterials()
    {
        if ( consumedMaterials != null )
        {
            Iterator<Material> iter = consumedMaterials.iterator();
            while ( iter.hasNext() )
            {
                iter.next().oneSided_setConsumingOperation( null );
            }
            consumedMaterials = null;  // no concurrent modification
        }
    }

    public void addToConsumedMaterials( Material materialUnit )
    {
        getConsumedMaterials().add( materialUnit );
        Operation oldConsumingOperation = materialUnit.getConsumingOperation();
        if ( ( oldConsumingOperation != this ) && ( oldConsumingOperation != null ) )
        {
            oldConsumingOperation.oneSided_removeFromConsumedMaterials( materialUnit );
        }
        materialUnit.oneSided_setConsumingOperation( this );
    }

    public void removeFromConsumedMaterials( Material materialUnit )
    {
        getConsumedMaterials().remove( materialUnit );
        materialUnit.oneSided_setConsumingOperation( null );
    }

    public void oneSided_addToConsumedMaterials( Material materialUnit )
    {
        getConsumedMaterials().add( materialUnit );
    }

    public void oneSided_removeFromConsumedMaterials( Material materialUnit )
    {
        getConsumedMaterials().remove( materialUnit );
    }

    @Override
    public void removeAllAssociations()
    {
        super.removeAllAssociations();
        flushGeneratedMaterials();
        flushConsumedMaterials();
    }
}