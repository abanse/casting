package com.hydro.casting.server.model.sched;

import com.hydro.casting.server.model.mat.MaterialType;
import com.hydro.casting.server.model.po.CustomerOrderItem;
import com.hydro.casting.server.model.po.WorkStep;
import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Entity
@Table( name = "demand" )
@NamedQuery( name = "demand.findByPLANValues", query = "select op.demand from CastingOperation op where op.demand.intermediateType = :intermediateType and op.position = :position and op.batch.plannedSuccessTs = :plannedSuccessTs" )
public class Demand extends BaseEntity
{
    @Column( name = "name", length = 50 )
    private String name;
    @Column( name = "amount" )
    private int amount;
    @Column( name = "due_date" )
    private LocalDateTime dueDate;
    @Column( name = "execution_state" )
    private int executionState = 100;
    @ManyToOne
    @JoinColumn( name = "work_step_oid" )
    private WorkStep workStep;
    @OneToMany( mappedBy = "demand" )
    private Set<Schedulable> schedulables;
    @ManyToMany( cascade = { CascadeType.ALL } )
    @JoinTable( name = "demand_destinations", joinColumns = { @JoinColumn( name = "demand_oid" ) }, inverseJoinColumns = { @JoinColumn( name = "destination_oid" ) } )
    private Set<Demand> destinations;
    @ManyToMany( cascade = { CascadeType.ALL } )
    @JoinTable( name = "demand_sources", joinColumns = { @JoinColumn( name = "demand_oid" ) }, inverseJoinColumns = { @JoinColumn( name = "source_oid" ) } )
    private Set<Demand> sources;
    @ManyToOne
    @JoinColumn( name = "material_type_oid" )
    private MaterialType materialType;
    @ManyToOne
    @JoinColumn( name = "customer_order_item_oid" )
    private CustomerOrderItem customerOrderItem;

    @ManyToOne
    @JoinColumn( name = "intermediate_type_oid" )
    private MaterialType intermediateType;

    public String getName()
    {
        return name;
    }

    public void setName( String newName )
    {
        this.name = newName;
    }

    public int getAmount()
    {
        return amount;
    }

    public void setAmount( int amount )
    {
        this.amount = amount;
    }

    public LocalDateTime getDueDate()
    {
        return dueDate;
    }

    public void setDueDate( LocalDateTime newDueDate )
    {
        this.dueDate = newDueDate;
    }

    public int getExecutionState()
    {
        return executionState;
    }

    public void setExecutionState( int newExecutionState )
    {
        this.executionState = newExecutionState;
    }

    public WorkStep getWorkStep()
    {
        return workStep;
    }

    public void setWorkStep( WorkStep newWorkStep )
    {
        WorkStep oldWorkStep = this.workStep;
        this.workStep = newWorkStep;
        if ( oldWorkStep != newWorkStep )
        {
            if ( oldWorkStep != null )
            {
                oldWorkStep.oneSided_removeFromDemands( this );
            }
            if ( newWorkStep != null )
            {
                newWorkStep.oneSided_addToDemands( this );
            }
        }
    }

    public void oneSided_setWorkStep( WorkStep newWorkStep )
    {
        this.workStep = newWorkStep;
    }

    public Set<Schedulable> getSchedulables()
    {
        // lazy evaluation; the field might still be null
        if ( schedulables == null )
        {
            schedulables = new HashSet<Schedulable>();
        }
        return schedulables;
    }

    public boolean containsInSchedulables( Schedulable operation )
    {
        return ( this.schedulables != null ) && this.schedulables.contains( operation );
    }

    public int numberOfSchedulables()
    {
        // handle uninitialized set, but do not initialize it now
        return ( schedulables == null ) ? 0 : schedulables.size();
    }

    public void flushSchedulables()
    {
        if ( schedulables != null )
        {
            Iterator<Schedulable> iter = schedulables.iterator();
            while ( iter.hasNext() )
            {
                iter.next().oneSided_setDemand( null );
            }
            schedulables = null;  // no concurrent modification
        }
    }

    public void addToSchedulables( Schedulable operation )
    {
        getSchedulables().add( operation );
        Demand oldDemand = operation.getDemand();
        if ( ( oldDemand != this ) && ( oldDemand != null ) )
        {
            oldDemand.oneSided_removeFromSchedulables( operation );
        }
        operation.oneSided_setDemand( this );
    }

    public void removeFromSchedulables( Schedulable operation )
    {
        getSchedulables().remove( operation );
        operation.oneSided_setDemand( null );
    }

    public void oneSided_addToSchedulables( Schedulable operation )
    {
        getSchedulables().add( operation );
    }

    public void oneSided_removeFromSchedulables( Schedulable operation )
    {
        getSchedulables().remove( operation );
    }

    public Set<Demand> getDestinations()
    {
        // lazy evaluation; the field might still be null
        if ( destinations == null )
        {
            destinations = new HashSet<Demand>();
        }
        return destinations;
    }

    public boolean containsInDestinations( Demand demand )
    {
        return ( this.destinations != null ) && this.destinations.contains( demand );
    }

    public int numberOfDestinations()
    {
        // handle uninitialized set, but do not initialize it now
        return ( destinations == null ) ? 0 : destinations.size();
    }

    public void flushDestinations()
    {
        if ( destinations != null )
        {
            Iterator<Demand> iter = destinations.iterator();
            while ( iter.hasNext() )
            {
                removeFromDestinations( iter.next() );
                // we do not assume the iterator can tollerate
                // concurrent modification, so we allocate a new one.
                // this might be quite expensive.
                iter = destinations.iterator();
            }
        }
    }

    public void addToDestinations( Demand demand )
    {
        getDestinations().add( demand );
        demand.oneSided_addToSources( this );
    }

    public void removeFromDestinations( Demand demand )
    {
        getDestinations().remove( demand );
        demand.oneSided_removeFromSources( this );
    }

    public void oneSided_addToDestinations( Demand demand )
    {
        getDestinations().add( demand );
    }

    public void oneSided_removeFromDestinations( Demand demand )
    {
        getDestinations().remove( demand );
    }

    public Set<Demand> getSources()
    {
        // lazy evaluation; the field might still be null
        if ( sources == null )
        {
            sources = new HashSet<Demand>();
        }
        return sources;
    }

    public boolean containsInSources( Demand demand )
    {
        return ( this.sources != null ) && this.sources.contains( demand );
    }

    public int numberOfSources()
    {
        // handle uninitialized set, but do not initialize it now
        return ( sources == null ) ? 0 : sources.size();
    }

    public void flushSources()
    {
        if ( sources != null )
        {
            Iterator<Demand> iter = sources.iterator();
            while ( iter.hasNext() )
            {
                removeFromSources( iter.next() );
                // we do not assume the iterator can tollerate
                // concurrent modification, so we allocate a new one.
                // this might be quite expensive.
                iter = sources.iterator();
            }
        }
    }

    public void addToSources( Demand demand )
    {
        getSources().add( demand );
        demand.oneSided_addToDestinations( this );
    }

    public void removeFromSources( Demand demand )
    {
        getSources().remove( demand );
        demand.oneSided_removeFromDestinations( this );
    }

    public void oneSided_addToSources( Demand demand )
    {
        getSources().add( demand );
    }

    public void oneSided_removeFromSources( Demand demand )
    {
        getSources().remove( demand );
    }

    public MaterialType getMaterialType()
    {
        return materialType;
    }

    public void setMaterialType( MaterialType materialType )
    {
        this.materialType = materialType;
    }

    public CustomerOrderItem getCustomerOrderItem()
    {
        return customerOrderItem;
    }

    public void setCustomerOrderItem( CustomerOrderItem newCustomerOrderItem )
    {
        CustomerOrderItem oldCustomerOrderItem = this.customerOrderItem;
        this.customerOrderItem = newCustomerOrderItem;
        if ( oldCustomerOrderItem != newCustomerOrderItem )
        {
            if ( oldCustomerOrderItem != null )
            {
                oldCustomerOrderItem.oneSided_removeFromDemands( this );
            }
            if ( newCustomerOrderItem != null )
            {
                newCustomerOrderItem.oneSided_addToDemands( this );
            }
        }
    }

    public void oneSided_setCustomerOrderItem( CustomerOrderItem newCustomerOrderItem )
    {
        this.customerOrderItem = newCustomerOrderItem;
    }

    public MaterialType getIntermediateType()
    {
        return intermediateType;
    }

    public void setIntermediateType( MaterialType intermediateType )
    {
        this.intermediateType = intermediateType;
    }

    public void removeAllAssociations()
    {
        setWorkStep( null );
        setMaterialType( null );
        setCustomerOrderItem( null );
        setIntermediateType( null );
        flushSchedulables();
        flushDestinations();
        flushSources();
    }
}