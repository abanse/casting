package com.hydro.casting.server.model.sched;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Entity
@DiscriminatorValue( "batch" )
public class Batch extends Operation
{
    @OneToMany( mappedBy = "batch" )
    @OrderBy( "executingSequenceIndex" )
    private List<Schedulable> members;
    @OneToMany( mappedBy = "batch" )
    private List<ProcessStep> processSteps;

    public List<Schedulable> getMembers()
    {
        if ( members == null )
        {
            members = new ArrayList<Schedulable>();
        }
        return members;
    }

    public Schedulable getFromMembers( int idx )
    {
        return getMembers().get( idx );
    }

    public boolean containsInMembers( Schedulable schedulable )
    {
        return ( this.members != null ) && this.members.contains( schedulable );
    }

    public int numberOfMembers()
    {
        return ( members == null ) ? 0 : members.size();
    }

    public void flushMembers()
    {
        if ( members != null )
        {
            Iterator<Schedulable> iter = members.iterator();
            while ( iter.hasNext() )
            {
                iter.next().oneSided_setBatch( null );
            }
            members = null;
        }
    }

    public void addToMembers( Schedulable schedulable )
    {
        getMembers().add( schedulable );
        Batch oldBatch = schedulable.getBatch();
        if ( ( oldBatch != this ) && ( oldBatch != null ) )
        {
            oldBatch.oneSided_removeFromMembers( schedulable );
        }
        schedulable.oneSided_setBatch( this );
    }

    public void addToMembers( int idx, Schedulable schedulable )
    {
        getMembers().add( idx, schedulable );
        Batch oldBatch = schedulable.getBatch();
        if ( ( oldBatch != this ) && ( oldBatch != null ) )
        {
            oldBatch.oneSided_removeFromMembers( schedulable );
        }
        schedulable.oneSided_setBatch( this );
    }

    public void removeFromMembers( Schedulable schedulable )
    {
        getMembers().remove( schedulable );
        schedulable.oneSided_setBatch( null );
    }

    public void oneSided_addToMembers( Schedulable schedulable )
    {
        getMembers().add( schedulable );
    }

    public void oneSided_removeFromMembers( Schedulable schedulable )
    {
        getMembers().remove( schedulable );
    }

    public List<ProcessStep> getProcessSteps()
    {
        if ( processSteps == null )
        {
            processSteps = new ArrayList<>();
        }
        return processSteps;
    }

    public void addToProcessSteps( ProcessStep processStep )
    {
        getProcessSteps().add( processStep );
        Batch oldBatch = processStep.getBatch();
        if ( oldBatch != null && oldBatch != this )
        {
            oldBatch.oneSided_removeFromProcessSteps( processStep );
        }
        processStep.oneSided_setBatch( this );
    }

    public void oneSided_addToProcessSteps( ProcessStep processStep )
    {
        getProcessSteps().add( processStep );
    }

    public void removeFromProcessSteps( ProcessStep processStep )
    {
        getProcessSteps().remove( processStep );
        processStep.oneSided_setBatch( null );
    }

    public void oneSided_removeFromProcessSteps( ProcessStep processStep )
    {
        getProcessSteps().remove( processStep );
    }

    public void flushProcessSteps()
    {
        for ( ProcessStep processStep : getProcessSteps() )
        {
            processStep.oneSided_setBatch( null );
        }
        processSteps = null;
    }

    @Override
    public void removeAllAssociations()
    {
        super.removeAllAssociations();
        flushMembers();
        flushProcessSteps();
    }
}

