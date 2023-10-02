package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.util.Objects;

public class CasterOccupancyPosDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private int position;
    private String slab;
    private String order;
    private int amount;// Normalerweise 1 nur bei doppelt lang gießen 2
    private String materialType;
    private String experimentNumber;
    private double width;
    private double length; // castingLength = length + lengthBonus
    private double lengthBonus;
    private double weight;
    private String mold;
    private String ags;

    @Override
    public long getId()
    {
        if ( slab == null )
        {
            return 0;
        }
        return slab.hashCode();
    }

    public int getPosition()
    {
        return position;
    }

    public void setPosition( int position )
    {
        this.position = position;
    }

    public String getSlab()
    {
        return slab;
    }

    public void setSlab( String slab )
    {
        this.slab = slab;
    }

    public String getOrder()
    {
        return order;
    }

    public void setOrder( String order )
    {
        this.order = order;
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

    public double getWeight()
    {
        return weight;
    }

    public void setWeight( double weight )
    {
        this.weight = weight;
    }

    public String getMold()
    {
        return mold;
    }

    public void setMold( String mold )
    {
        this.mold = mold;
    }

    public String getAgs()
    {
        return ags;
    }

    public void setAgs( String ags )
    {
        this.ags = ags;
    }

    // ==========================================

    public double getCastingLength()
    {
        return length + lengthBonus;
    }

    public void setCastingLength( double castingLength )
    {
        // ignore
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        CasterOccupancyPosDTO that = (CasterOccupancyPosDTO) o;
        return position == that.position && amount == that.amount && Double.compare( that.width, width ) == 0 && Double.compare( that.length, length ) == 0
                && Double.compare( that.lengthBonus, lengthBonus ) == 0 && Double.compare( that.weight, weight ) == 0 && Objects.equals( slab, that.slab ) && Objects.equals(
                order, that.order ) && Objects.equals( materialType, that.materialType ) && Objects.equals( experimentNumber, that.experimentNumber ) && Objects.equals( mold, that.mold )
                && Objects.equals( ags, that.ags );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( position, slab, order, amount, materialType, experimentNumber, width, length, lengthBonus, weight, mold, ags );
    }

    @Override
    public String toString()
    {
        return "CasterOccupancyPosDTO{" + "position=" + position + ", slab='" + slab + '\'' + ", order='" + order + '\'' + ", amount=" + amount + ", materialType='" + materialType + '\''
                + ", experimentNumber='" + experimentNumber + '\'' + ", width=" + width + ", length=" + length + ", lengthBonus=" + lengthBonus + ", weight=" + weight + ", mold='" + mold + '\''
                + ", ags='" + ags + '}';
    }
}
