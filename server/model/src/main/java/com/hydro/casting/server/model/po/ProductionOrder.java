package com.hydro.casting.server.model.po;

import com.hydro.casting.server.model.mat.MaterialType;
import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Entity
@Table( name = "production_order" )
@NamedQuery( name = "productionOrder.apk", query = "select po from ProductionOrder po where po.apk = :apk")
@NamedQuery( name = "productionOrder.findByBatch", query = "select po from Operation op join ProductionOrder po on op.demand.workStep.productionOrder = po where op.batch = :batch")
public class ProductionOrder extends BaseEntity
{
    @Column( name = "apk", length = 20 )
    private String apk;
    @Column( name = "priority" )
    private int priority;
    @Column( name = "execution_state" )
    private int executionState = 100;
    @OneToMany( mappedBy = "productionOrder" )
    private List<WorkStep> workSteps;

    @Column( name = "kind", length = 10 )
    private String kind;
    @Column( name = "amount")
    private double amount;
    @Column( name = "unit", length = 10 )
    private String unit;
    @Column( name = "erp_charge", length = 20 )
    private String erpCharge;
    @Column( name = "start_ts" )
    private LocalDateTime startTS;
    @Column( name = "end_ts" )
    private LocalDateTime endTS;
    @Column( name = "description", length = 200 )
    private String description;
    @ManyToOne
    @JoinColumn( name = "material_type_oid" )
    private MaterialType materialType;

    public String getApk()
    {
        return apk;
    }

    public void setApk( String newApk )
    {
        this.apk = newApk;
    }

    public int getPriority()
    {
        return priority;
    }

    public void setPriority( int newPriority )
    {
        this.priority = newPriority;
    }

    public int getExecutionState()
    {
        return executionState;
    }

    public void setExecutionState( int newExecutionState )
    {
        this.executionState = newExecutionState;
    }

    public String getKind()
    {
        return kind;
    }

    public void setKind( String kind )
    {
        this.kind = kind;
    }

    public double getAmount()
    {
        return amount;
    }

    public void setAmount( double amount )
    {
        this.amount = amount;
    }

    public String getUnit()
    {
        return unit;
    }

    public void setUnit( String unit )
    {
        this.unit = unit;
    }

    public String getErpCharge()
    {
        return erpCharge;
    }

    public void setErpCharge( String erpCharge )
    {
        this.erpCharge = erpCharge;
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

    public MaterialType getMaterialType()
    {
        return materialType;
    }

    public void setMaterialType( MaterialType materialType )
    {
        this.materialType = materialType;
    }

    public List<WorkStep> getWorkSteps()
    {
        // lazy evaluation; the field might still be null
        if ( workSteps == null )
        {
            workSteps = new ArrayList<WorkStep>();
        }
        return workSteps;
    }

    public WorkStep getFromWorkSteps( int idx )
    {
        return getWorkSteps().get( idx );
    }

    public boolean containsInWorkSteps( WorkStep workStep )
    {
        return ( this.workSteps != null ) && this.workSteps.contains( workStep );
    }

    public int numberOfWorkSteps()
    {
        // handle uninitialized set, but do not initialize it now
        return ( workSteps == null ) ? 0 : workSteps.size();
    }

    public void flushWorkSteps()
    {
        if ( workSteps != null )
        {
            Iterator<WorkStep> iter = workSteps.iterator();
            while ( iter.hasNext() )
            {
                WorkStep c = iter.next();
                c.oneSided_setProductionOrder( null ); // avoid recursion with CME in next statement
                c.removeAllAssociations();
            }
            workSteps.clear();  // no concurrent modification
        }
    }

    public void addToWorkSteps( WorkStep workStep )
    {
        getWorkSteps().add( workStep );
        ProductionOrder oldProductionOrder = workStep.getProductionOrder();
        if ( ( oldProductionOrder != this ) && ( oldProductionOrder != null ) )
        {
            oldProductionOrder.oneSided_removeFromWorkSteps( workStep );
        }
        workStep.oneSided_setProductionOrder( this );
    }

    public void addToWorkSteps( int idx, WorkStep workStep )
    {
        getWorkSteps().add( idx, workStep );
        ProductionOrder oldProductionOrder = workStep.getProductionOrder();
        if ( ( oldProductionOrder != this ) && ( oldProductionOrder != null ) )
        {
            oldProductionOrder.oneSided_removeFromWorkSteps( workStep );
        }
        workStep.oneSided_setProductionOrder( this );
    }

    public void removeFromWorkSteps( WorkStep workStep )
    {
        getWorkSteps().remove( workStep );
        workStep.oneSided_setProductionOrder( null );
    }

    public void oneSided_addToWorkSteps( WorkStep workStep )
    {
        getWorkSteps().add( workStep );
    }

    public void oneSided_removeFromWorkSteps( WorkStep workStep )
    {
        getWorkSteps().remove( workStep );
    }

    public void removeAllAssociations()
    {
        flushWorkSteps();
        setMaterialType( null );
    }

}