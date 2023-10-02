package com.hydro.casting.server.model.po;

import com.hydro.casting.server.model.mat.MaterialType;
import com.hydro.casting.server.model.sched.Demand;
import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Entity
@Table( name = "customer_order_item" )
@NamedQuery( name = "customerOrderItem.apk", query = "select coi from CustomerOrderItem coi where coi.apk = :apk")
public class CustomerOrderItem extends BaseEntity
{
    @Column( name = "apk", length = 20 )
    private String apk;
    @Column( name = "requested_quantity" )
    private double requestedQuantity;
    @Column( name = "requested_date_from" )
    private LocalDateTime requestedDateFrom;
    @Column( name = "requested_date_To" )
    private LocalDateTime requestedDateTo;
    @Column( name = "experiment_number" )
    private String experimentNumber;

    @OneToMany( mappedBy = "customerOrderItem" )
    private Set<Demand> demands;
    @ManyToOne
    @JoinColumn( name = "customer_order_oid" )
    private CustomerOrder customerOrder;
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

    public double getRequestedQuantity()
    {
        return requestedQuantity;
    }

    public void setRequestedQuantity( double newRequestedQuantity )
    {
        this.requestedQuantity = newRequestedQuantity;
    }

    public LocalDateTime getRequestedDateFrom()
    {
        return requestedDateFrom;
    }

    public void setRequestedDateFrom( LocalDateTime requestedDateFrom )
    {
        this.requestedDateFrom = requestedDateFrom;
    }

    public LocalDateTime getRequestedDateTo()
    {
        return requestedDateTo;
    }

    public void setRequestedDateTo( LocalDateTime requestedDateTo )
    {
        this.requestedDateTo = requestedDateTo;
    }

    public String getExperimentNumber()
    {
        return experimentNumber;
    }

    public void setExperimentNumber( String experimentNumber )
    {
        this.experimentNumber = experimentNumber;
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
                removeFromDemands( iter.next() );
                // we do not assume the iterator can tollerate
                // concurrent modification, so we allocate a new one.
                // this might be quite expensive.
                iter = demands.iterator();
            }
        }
    }

    public void addToDemands( Demand demand )
    {
        getDemands().add( demand );
        CustomerOrderItem oldCustomerOrderItem = demand.getCustomerOrderItem();
        if ( ( oldCustomerOrderItem != this ) && ( oldCustomerOrderItem != null ) )
        {
            oldCustomerOrderItem.oneSided_removeFromDemands( demand );
        }
        demand.oneSided_setCustomerOrderItem( this );
    }

    public void removeFromDemands( Demand demand )
    {
        getDemands().remove( demand );
        demand.oneSided_setCustomerOrderItem( null );
    }

    public void oneSided_addToDemands( Demand demand )
    {
        getDemands().add( demand );
    }

    public void oneSided_removeFromDemands( Demand demand )
    {
        getDemands().remove( demand );
    }

    public CustomerOrder getCustomerOrder()
    {
        return customerOrder;
    }

    public void setCustomerOrder( CustomerOrder newCustomerOrder )
    {
        CustomerOrder oldCustomerOrder = this.customerOrder;
        this.customerOrder = newCustomerOrder;
        if ( oldCustomerOrder != newCustomerOrder )
        {
            if ( oldCustomerOrder != null )
            {
                oldCustomerOrder.oneSided_removeFromOrderItems( this );
            }
            if ( newCustomerOrder != null )
            {
                newCustomerOrder.oneSided_addToOrderItems( this );
            }
        }
    }

    public void oneSided_setCustomerOrder( CustomerOrder newCustomerOrder )
    {
        this.customerOrder = newCustomerOrder;
    }

    public MaterialType getMaterialType()
    {
        return materialType;
    }

    public void setMaterialType( MaterialType materialType )
    {
        this.materialType = materialType;
    }

    public void removeAllAssociations()
    {
        flushDemands();
        setCustomerOrder( null );
        setMaterialType( null );
    }
}

