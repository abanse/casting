package com.hydro.casting.server.contract.dto;

import java.time.LocalDateTime;
import java.util.Objects;

public class CrucibleMaterialDTO extends MaterialDTO
{
    private static final long serialVersionUID = 1L;

    private String crucibleName;
    private double grossWeight;

    public String getCrucibleName()
    {
        return crucibleName;
    }

    public void setCrucibleName( String crucibleName )
    {
        this.crucibleName = crucibleName;
    }

    public double getGrossWeight()
    {
        return grossWeight;
    }

    public void setGrossWeight( double grossWeight )
    {
        this.grossWeight = grossWeight;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( !( o instanceof CrucibleMaterialDTO ) )
            return false;
        if ( !super.equals( o ) )
            return false;
        CrucibleMaterialDTO that = (CrucibleMaterialDTO) o;
        return Double.compare( that.grossWeight, grossWeight ) == 0 && Objects.equals( crucibleName, that.crucibleName );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), crucibleName, grossWeight );
    }

    @Override
    public String toString()
    {
        return "CrucibleMaterialDTO{" + "crucibleName='" + crucibleName + '\'' + ", grossWeight=" + grossWeight + "} " + super.toString();
    }
}