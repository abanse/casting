package com.hydro.casting.server.contract.dto;

import java.util.Objects;
public class CasterInstructionDTO implements CastingInstructionDTO
{
    private static final long serialVersionUID = 1L;

    private String caster;
    private String furnace;
    private String charge;
    private Long castingBatchOID;
    private String alloy;
    private String castingProgram;
    private Integer castingLength;
    private Integer plannedWeight;
    private Integer netWeight;

    @Override
    public long getId()
    {
        return Objects.hash( caster );
    }

    @Override
    public String getMachineApk()
    {
        return caster;
    }

    public String getCaster()
    {
        return caster;
    }

    public void setCaster( String caster )
    {
        this.caster = caster;
    }

    public String getFurnace()
    {
        return furnace;
    }

    public void setFurnace( String furnace )
    {
        this.furnace = furnace;
    }

    public String getCharge()
    {
        return charge;
    }

    public void setCharge( String charge )
    {
        this.charge = charge;
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

    public Long getCastingBatchOID()
    {
        return castingBatchOID;
    }

    public void setCastingBatchOID( Long castingBatchOID )
    {
        this.castingBatchOID = castingBatchOID;
    }

    public String getAlloy()
    {
        return alloy;
    }

    public void setAlloy( String alloy )
    {
        this.alloy = alloy;
    }

    public String getCastingProgram()
    {
        return castingProgram;
    }

    public void setCastingProgram( String castingProgram )
    {
        this.castingProgram = castingProgram;
    }

    public Integer getCastingLength()
    {
        return castingLength;
    }

    public void setCastingLength( Integer castingLength )
    {
        this.castingLength = castingLength;
    }

    public Double getCastingLengthM()
    {
        if ( castingLength == null )
        {
            return null;
        }
        return Double.valueOf( castingLength.doubleValue() / 100.0 );
    }

    public void setCastingLengthM( Double castingLengthM )
    {
        // ignore
    }

    public Integer getPlannedWeight()
    {
        return plannedWeight;
    }

    public void setPlannedWeight( Integer plannedWeight )
    {
        this.plannedWeight = plannedWeight;
    }

    public Double getPlannedWeightT()
    {
        if ( plannedWeight == null )
        {
            return null;
        }
        return Double.valueOf( plannedWeight.doubleValue() / 1000.0 );
    }

    public void setPlannedWeightT( Double plannedWeightT )
    {
        // ignore
    }

    public Integer getNetWeight()
    {
        return netWeight;
    }

    public void setNetWeight( Integer netWeight )
    {
        this.netWeight = netWeight;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        CasterInstructionDTO that = (CasterInstructionDTO) o;
        return Objects.equals( caster, that.caster ) && Objects.equals( furnace, that.furnace ) && Objects.equals( charge, that.charge ) && Objects.equals( castingBatchOID, that.castingBatchOID )
                && Objects.equals( alloy, that.alloy ) && Objects.equals( castingProgram, that.castingProgram ) && Objects.equals( castingLength, that.castingLength ) && Objects.equals( plannedWeight,
                that.plannedWeight ) && Objects.equals( netWeight, that.netWeight );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( caster, furnace, charge, castingBatchOID, alloy, castingProgram, castingLength, plannedWeight, netWeight );
    }
}
