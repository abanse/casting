package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.util.Objects;

public class CasterSchedulePosDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private CasterScheduleDTO casterSchedule;
    private int position;
    private int amount;// Normalerweise 1 nur bei doppelt lang gießen 2
    private String materialType;
    private String customerOrderItem;
    private String experimentNumber;
    private double width;
    private double length;
    private double lengthBonus;

    @Override
    public long getId()
    {
        if ( casterSchedule == null )
        {
            return 0;
        }
        return Objects.hash( casterSchedule.getId(), position );
    }

    public CasterScheduleDTO getCasterSchedule()
    {
        return casterSchedule;
    }

    public void setCasterSchedule( CasterScheduleDTO casterSchedule )
    {
        this.casterSchedule = casterSchedule;
    }

    public int getPosition()
    {
        return position;
    }

    public void setPosition( int position )
    {
        this.position = position;
    }

    public int getAmount()
    {
        return amount;
    }

    public void setAmount( int amount )
    {
        this.amount = amount;
    }

    public String getMaterialType()
    {
        return materialType;
    }

    public void setMaterialType( String materialType )
    {
        this.materialType = materialType;
    }

    public String getCustomerOrderItem()
    {
        return customerOrderItem;
    }

    public void setCustomerOrderItem( String customerOrderItem )
    {
        this.customerOrderItem = customerOrderItem;
    }

    public String getExperimentNumber()
    {
        return experimentNumber;
    }

    public void setExperimentNumber( String experimentNumber )
    {
        this.experimentNumber = experimentNumber;
    }

    public double getWidth()
    {
        return width;
    }

    public void setWidth( double width )
    {
        this.width = width;
    }

    public double getLength()
    {
        return length;
    }

    public void setLength( double length )
    {
        this.length = length;
    }

    public double getLengthBonus()
    {
        return lengthBonus;
    }

    public void setLengthBonus( double lengthBonus )
    {
        this.lengthBonus = lengthBonus;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        CasterSchedulePosDTO that = (CasterSchedulePosDTO) o;
        return position == that.position && amount == that.amount && Double.compare( that.width, width ) == 0 && Double.compare( that.length, length ) == 0
                && Double.compare( that.lengthBonus, lengthBonus ) == 0 && Objects.equals( casterSchedule, that.casterSchedule ) && Objects.equals( materialType, that.materialType ) && Objects
                .equals( customerOrderItem, that.customerOrderItem ) && Objects.equals( experimentNumber, that.experimentNumber );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( casterSchedule, position, amount, materialType, customerOrderItem, experimentNumber, width, length, lengthBonus );
    }
}
