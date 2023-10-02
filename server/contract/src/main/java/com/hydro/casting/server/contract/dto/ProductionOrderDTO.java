package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.time.LocalDateTime;
import java.util.Objects;

public class ProductionOrderDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private long id;
    private String apk;
    private int executionState;
    private String kind;
    private double amount;
    private String unit;
    private String erpCharge;
    private LocalDateTime startTS;
    private LocalDateTime endTS;
    private String description;
    private String materialTypeApk;
    private String materialTypeDescription;
    private String charge;

    @Override
    public long getId()
    {
        return id;
    }

    public void setId( long id )
    {
        this.id = id;
    }

    public String getApk()
    {
        return apk;
    }

    public void setApk( String apk )
    {
        this.apk = apk;
    }

    public int getExecutionState()
    {
        return executionState;
    }

    public void setExecutionState( int executionState )
    {
        this.executionState = executionState;
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

    public String getMaterialTypeApk()
    {
        return materialTypeApk;
    }

    public void setMaterialTypeApk( String materialTypeApk )
    {
        this.materialTypeApk = materialTypeApk;
    }

    public String getMaterialTypeDescription()
    {
        return materialTypeDescription;
    }

    public void setMaterialTypeDescription( String materialTypeDescription )
    {
        this.materialTypeDescription = materialTypeDescription;
    }

    public String getCharge()
    {
        return charge;
    }

    public void setCharge( String charge )
    {
        this.charge = charge;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        ProductionOrderDTO that = (ProductionOrderDTO) o;
        return id == that.id && executionState == that.executionState && Double.compare( that.amount, amount ) == 0 && Objects.equals( apk, that.apk ) && Objects.equals( kind, that.kind )
                && Objects.equals( unit, that.unit ) && Objects.equals( erpCharge, that.erpCharge ) && Objects.equals( startTS, that.startTS ) && Objects.equals( endTS, that.endTS ) && Objects.equals(
                description, that.description ) && Objects.equals( materialTypeApk, that.materialTypeApk ) && Objects.equals( materialTypeDescription, that.materialTypeDescription ) && Objects.equals(
                charge, that.charge );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, apk, executionState, kind, amount, unit, erpCharge, startTS, endTS, description, materialTypeApk, materialTypeDescription, charge );
    }

    @Override
    public String toString()
    {
        return "ProductionOrderDTO{" + "id=" + id + ", apk='" + apk + '\'' + ", executionState=" + executionState + ", kind='" + kind + '\'' + ", amount=" + amount + ", unit='" + unit + '\''
                + ", erpCharge='" + erpCharge + '\'' + ", startTS=" + startTS + ", endTS=" + endTS + ", description='" + description + '\'' + ", materialTypeApk='" + materialTypeApk + '\''
                + ", materialTypeDescription='" + materialTypeDescription + '\'' + ", charge='" + charge + '\'' + '}';
    }
}
