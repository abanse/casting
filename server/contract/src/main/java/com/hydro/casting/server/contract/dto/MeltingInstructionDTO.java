package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.time.LocalDateTime;
import java.util.Objects;
public class MeltingInstructionDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private String machine;
    private Long meltingBatchOID;
    private String charge;
    private String alloy;
    private LocalDateTime meltingStartTS;
    private LocalDateTime skimmingStartTS;
    private LocalDateTime skimmingMeltingChamberStartTS;
    private LocalDateTime mixingStartTS;
    private LocalDateTime pouringStartTS;

    @Override
    public long getId()
    {
        return Objects.hash( machine );
    }

    public String getChargeWithoutYear()
    {
        if ( charge == null || charge.length() < 2 )
        {
            return null;
        }
        return charge.substring( 2 );
    }

    public void setChargeWithoutYear( String chargeWithoutYear )
    {
        // ignore
    }

    public String getMachine()
    {
        return machine;
    }

    public void setMachine( String machine )
    {
        this.machine = machine;
    }

    public Long getMeltingBatchOID()
    {
        return meltingBatchOID;
    }

    public void setMeltingBatchOID( Long meltingBatchOID )
    {
        this.meltingBatchOID = meltingBatchOID;
    }

    public String getCharge()
    {
        return charge;
    }

    public void setCharge( String charge )
    {
        this.charge = charge;
    }

    public String getAlloy()
    {
        return alloy;
    }

    public void setAlloy( String alloy )
    {
        this.alloy = alloy;
    }

    public LocalDateTime getMeltingStartTS()
    {
        return meltingStartTS;
    }

    public void setMeltingStartTS( LocalDateTime meltingStartTS )
    {
        this.meltingStartTS = meltingStartTS;
    }

    public LocalDateTime getSkimmingStartTS()
    {
        return skimmingStartTS;
    }

    public void setSkimmingStartTS( LocalDateTime skimmingStartTS )
    {
        this.skimmingStartTS = skimmingStartTS;
    }

    public LocalDateTime getSkimmingMeltingChamberStartTS()
    {
        return skimmingMeltingChamberStartTS;
    }

    public void setSkimmingMeltingChamberStartTS( LocalDateTime skimmingMeltingChamberStartTS )
    {
        this.skimmingMeltingChamberStartTS = skimmingMeltingChamberStartTS;
    }

    public LocalDateTime getMixingStartTS()
    {
        return mixingStartTS;
    }

    public void setMixingStartTS( LocalDateTime mixingStartTS )
    {
        this.mixingStartTS = mixingStartTS;
    }

    public LocalDateTime getPouringStartTS()
    {
        return pouringStartTS;
    }

    public void setPouringStartTS( LocalDateTime pouringStartTS )
    {
        this.pouringStartTS = pouringStartTS;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        MeltingInstructionDTO that = (MeltingInstructionDTO) o;
        return Objects.equals( getMachine(), that.getMachine() ) && Objects.equals( getMeltingBatchOID(), that.getMeltingBatchOID() ) && Objects.equals( getCharge(), that.getCharge() )
                && Objects.equals( getAlloy(), that.getAlloy() ) && Objects.equals( getMeltingStartTS(), that.getMeltingStartTS() ) && Objects.equals( getSkimmingStartTS(), that.getSkimmingStartTS() )
                && Objects.equals( getSkimmingMeltingChamberStartTS(), that.getSkimmingMeltingChamberStartTS() ) && Objects.equals( getMixingStartTS(), that.getMixingStartTS() ) && Objects.equals(
                getPouringStartTS(), that.getPouringStartTS() );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( getMachine(), getMeltingBatchOID(), getCharge(), getAlloy(), getMeltingStartTS(), getSkimmingStartTS(), getSkimmingMeltingChamberStartTS(), getMixingStartTS(),
                getPouringStartTS() );
    }

    @Override
    public String toString()
    {
        return "MeltingInstructionDTO{" + "machine='" + machine + '\'' + ", meltingBatchOID=" + meltingBatchOID + ", charge='" + charge + '\'' + ", alloy='" + alloy + '\'' + ", meltingStartTS="
                + meltingStartTS + ", skimmingStartTS=" + skimmingStartTS + ", skimmingMeltingChamberStartTS=" + skimmingMeltingChamberStartTS + ", mixingStartTS=" + mixingStartTS
                + ", pouringStartTS=" + pouringStartTS + '}';
    }
}
