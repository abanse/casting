package com.hydro.casting.server.model.po;

import com.hydro.casting.server.model.res.Plant;
import com.hydro.casting.server.model.sched.Demand;
import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Entity
@Table( name = "work_step" )
@NamedQuery( name = "workStep.apk", query = "select ws from WorkStep ws where ws.apk = :apk")
public class WorkStep extends BaseEntity
{
    @Column( name = "apk", length = 20 )
    private String apk;
    @Column( name = "execution_state" )
    private int executionState = 100;

    @Column( name = "work_step_number", length = 10 )
    private String workStepNumber;
    @Column( name = "start_ts" )
    private LocalDateTime startTS;
    @Column( name = "end_ts" )
    private LocalDateTime endTS;
    @Column( name = "description", length = 200 )
    private String description;
    @Column( name = "work_place", length = 20 )
    private String workPlace;
    @Column( name = "work_place_description", length = 200 )
    private String workPlaceDescription;

    @ManyToOne
    @JoinColumn( name = "production_order_oid" )
    private ProductionOrder productionOrder;
    @OneToMany( mappedBy = "workStep" )
    private Set<Demand> demands;
    @ManyToOne
    @JoinColumn( name = "plant_oid" )
    private Plant plant;

    public String getApk()
    {
        return apk;
    }

    public void setApk( String newApk )
    {
        this.apk = newApk;
    }

    public int getExecutionState()
    {
        return executionState;
    }

    public void setExecutionState( int newExecutionState )
    {
        this.executionState = newExecutionState;
    }

    public String getWorkStepNumber()
    {
        return workStepNumber;
    }

    public void setWorkStepNumber( String workStepNumber )
    {
        this.workStepNumber = workStepNumber;
    }

    public LocalDateTime getStartTS()
    {
        return startTS;
    }

    public void setStartTS( LocalDateTime startTS )
    {
        this.startTS = startTS;
    }

    public LocalDateTime getEndTS()
    {
        return endTS;
    }

    public void setEndTS( LocalDateTime endTS )
    {
        this.endTS = endTS;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getWorkPlace()
    {
        return workPlace;
    }

    public void setWorkPlace( String workPlace )
    {
        this.workPlace = workPlace;
    }

    public String getWorkPlaceDescription()
    {
        return workPlaceDescription;
    }

    public void setWorkPlaceDescription( String workPlaceDescription )
    {
        this.workPlaceDescription = workPlaceDescription;
    }

    public ProductionOrder getProductionOrder()
    {
        return productionOrder;
    }

    public void setProductionOrder( ProductionOrder newProductionOrder )
    {
        ProductionOrder oldProductionOrder = this.productionOrder;
        this.productionOrder = newProductionOrder;
        if ( oldProductionOrder != newProductionOrder )
        {
            if ( oldProductionOrder != null )
            {
                oldProductionOrder.oneSided_removeFromWorkSteps( this );
            }
            if ( newProductionOrder != null )
            {
                newProductionOrder.oneSided_addToWorkSteps( this );
            }
        }
    }

    public void oneSided_setProductionOrder( ProductionOrder newProductionOrder )
    {
        this.productionOrder = newProductionOrder;
    }

    public Set<Demand> getDemands()
    {
        // lazy evaluation; the field might still be null
        if ( demands == null )
        {
            demands = new HashSet<Demand>();
        }
        return demands;
    }

    public boolean containsInDemands( Demand demand )
    {
        return ( this.demands != null ) && this.demands.contains( demand );
    }

    public int numberOfDemands()
    {
        // handle uninitialized set, but do not initialize it now
        return ( demands == null ) ? 0 : demands.size();
    }

    public void flushDemands()
    {
        if ( demands != null )
        {
            Iterator<Demand> iter = demands.iterator();
            while ( iter.hasNext() )
            {
                iter.next().oneSided_setWorkStep( null );
            }
            demands = null;  // no concurrent modification
        }
    }

    public void addToDemands( Demand demand )
    {
        getDemands().add( demand );
        WorkStep oldWorkStep = demand.getWorkStep();
        if ( ( oldWorkStep != this ) && ( oldWorkStep != null ) )
        {
            oldWorkStep.oneSided_removeFromDemands( demand );
        }
        demand.oneSided_setWorkStep( this );
    }

    public void removeFromDemands( Demand demand )
    {
        getDemands().remove( demand );
        demand.oneSided_setWorkStep( null );
    }

    public void oneSided_addToDemands( Demand demand )
    {
        getDemands().add( demand );
    }

    public void oneSided_removeFromDemands( Demand demand )
    {
        getDemands().remove( demand );
    }

    public Plant getPlant()
    {
        return plant;
    }

    public void setPlant( Plant newPlant )
    {
        this.plant = newPlant;
    }

    public void removeAllAssociations()
    {
        setProductionOrder( null );
        flushDemands();
        setPlant( null );
    }
}