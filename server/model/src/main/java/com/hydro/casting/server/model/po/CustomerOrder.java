package com.hydro.casting.server.model.po;

import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Entity
@Table( name = "customer_order" )
@NamedQuery( name = "customerOrder.apk", query = "select co from CustomerOrder co where co.apk = :apk")
public class CustomerOrder extends BaseEntity
{
    @Column( name = "apk", length = 20 )
    private String apk;
    @Column( name = "order_date" )
    private LocalDateTime orderDate;
    @OneToMany( mappedBy = "customerOrder" )
    private Set<CustomerOrderItem> orderItems;

    public String getApk()
    {
        return apk;
    }

    public void setApk( String newApk )
    {
        this.apk = newApk;
    }

    public LocalDateTime getOrderDate()
    {
        return orderDate;
    }

    public void setOrderDate( LocalDateTime newOrderDate )
    {
        this.orderDate = newOrderDate;
    }

    public Set<CustomerOrderItem> getOrderItems()
    {
        // lazy evaluation; the field might still be null
        if ( orderItems == null )
        {
            orderItems = new HashSet<CustomerOrderItem>();
        }
        return orderItems;
    }

    public boolean containsInOrderItems( CustomerOrderItem customerOrderItem )
    {
        return ( this.orderItems != null ) && this.orderItems.contains( customerOrderItem );
    }

    public int numberOfOrderItems()
    {
        // handle uninitialized set, but do not initialize it now
        return ( orderItems == null ) ? 0 : orderItems.size();
    }

    public void flushOrderItems()
    {
        if ( orderItems != null )
        {
            Iterator<CustomerOrderItem> iter = orderItems.iterator();
            while ( iter.hasNext() )
            {
                CustomerOrderItem c = iter.next();
                c.oneSided_setCustomerOrder( null ); // avoid recursion with CME in next statement
                c.removeAllAssociations();
            }
            orderItems.clear();  // no concurrent modification
        }
    }

    public void addToOrderItems( CustomerOrderItem customerOrderItem )
    {
        getOrderItems().add( customerOrderItem );
        CustomerOrder oldCustomerOrder = customerOrderItem.getCustomerOrder();
        if ( ( oldCustomerOrder != this ) && ( oldCustomerOrder != null ) )
        {
            oldCustomerOrder.oneSided_removeFromOrderItems( customerOrderItem );
        }
        customerOrderItem.oneSided_setCustomerOrder( this );
    }

    public void removeFromOrderItems( CustomerOrderItem customerOrderItem )
    {
        getOrderItems().remove( customerOrderItem );
        customerOrderItem.oneSided_setCustomerOrder( null );
    }

    public void oneSided_addToOrderItems( CustomerOrderItem customerOrderItem )
    {
        getOrderItems().add( customerOrderItem );
    }

    public void oneSided_removeFromOrderItems( CustomerOrderItem customerOrderItem )
    {
        getOrderItems().remove( customerOrderItem );
    }

    public void removeAllAssociations()
    {
        flushOrderItems();
    }
}

