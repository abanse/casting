package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.util.Objects;

public class UnloadSlabDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private String slab;
    private String order;
    private double height;
    private double width;
    private double length;
    private double lengthBonus;
    private String netzeSP;
    private String barrenSP;

    @Override
    public long getId()
    {
        if ( slab != null )
        {
            return slab.hashCode();
        }
        return 0;
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

    public double getHeight()
    {
        return height;
    }

    public void setHeight( double height )
    {
        this.height = height;
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

    public String getNetzeSP()
    {
        return netzeSP;
    }

    public double getCastingLength()
    {
        return length + lengthBonus;
    }

    public void setCastingLength( double castingLength )
    {
        // nothing to do
    }
    public void setNetzeSP( String netzeSP )
    {
        this.netzeSP = netzeSP;
    }

    public String getBarrenSP()
    {
        return barrenSP;
    }

    public void setBarrenSP( String barrenSP )
    {
        this.barrenSP = barrenSP;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        UnloadSlabDTO that = (UnloadSlabDTO) o;
        return Double.compare( that.height, height ) == 0 && Double.compare( that.width, width ) == 0 && Double.compare( that.length, length ) == 0
                && Double.compare( that.lengthBonus, lengthBonus ) == 0 && Objects.equals( slab, that.slab ) && Objects.equals( order, that.order ) && Objects.equals( netzeSP, that.netzeSP )
                && Objects.equals( barrenSP, that.barrenSP );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( slab, order, height, width, length, lengthBonus, netzeSP, barrenSP );
    }

    @Override
    public String toString()
    {
        return "UnloadSlabDTO{" + "slab='" + slab + '\'' + ", order='" + order + '\'' + ", height=" + height + ", width=" + width + ", length=" + length + ", lengthBonus=" + lengthBonus
                + ", netzeSP='" + netzeSP + '\'' + ", barrenSP='" + barrenSP + '\'' + '}';
    }
}
