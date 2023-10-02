package com.hydro.casting.server.contract.dto;

import java.util.Objects;

public class FurnaceInstructionDTO implements CastingInstructionDTO
{
    private static final long serialVersionUID = 1L;

    private String furnace;
    private Long castingBatchOID;
    private String charge;
    private String alloy;
    private Integer plannedWeight;
    private Double maxWeight;

    private ChargeSpecificationDTO chargeSpecification;
    private AnalysisDetailDTO chargeAnalysisDetail;

    @Override
    public long getId()
    {
        return Objects.hash( furnace );
    }

    @Override
    public String getMachineApk()
    {
        return furnace;
    }

    public String getFurnace()
    {
        return furnace;
    }

    public void setFurnace( String furnace )
    {
        this.furnace = furnace;
    }

    public Long getCastingBatchOID()
    {
        return castingBatchOID;
    }

    public void setCastingBatchOID( Long castingBatchOID )
    {
        this.castingBatchOID = castingBatchOID;
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

    public String getAlloy()
    {
        return alloy;
    }

    public void setAlloy( String alloy )
    {
        this.alloy = alloy;
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
        return plannedWeight.doubleValue() / 1000.0;
    }

    public void setPlannedWeightT( Double plannedWeightT )
    {
        // ignore
    }

    public Double getMaxWeight()
    {
        return maxWeight;
    }

    public void setMaxWeight( Double maxWeight )
    {
        this.maxWeight = maxWeight;
    }

    public Double getUtilization()
    {
        if ( maxWeight == null || plannedWeight == null )
        {
            return null;
        }
        return plannedWeight.doubleValue() / maxWeight;
    }

    public void setUtilization( Double utilization )
    {
        // ignore
    }

    public ChargeSpecificationDTO getChargeSpecification()
    {
        return chargeSpecification;
    }

    public void setChargeSpecification( ChargeSpecificationDTO chargeSpecification )
    {
        this.chargeSpecification = chargeSpecification;
    }

    public AnalysisDetailDTO getChargeAnalysisDetail()
    {
        return chargeAnalysisDetail;
    }

    public void setChargeAnalysisDetail( AnalysisDetailDTO chargeAnalysisDetail )
    {
        this.chargeAnalysisDetail = chargeAnalysisDetail;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( !( o instanceof FurnaceInstructionDTO ) )
            return false;
        FurnaceInstructionDTO that = (FurnaceInstructionDTO) o;
        return Objects.equals( furnace, that.furnace ) && Objects.equals( castingBatchOID, that.castingBatchOID ) && Objects.equals( charge, that.charge ) && Objects.equals( alloy, that.alloy )
                && Objects.equals( plannedWeight, that.plannedWeight ) && Objects.equals( maxWeight, that.maxWeight ) && Objects.equals( chargeSpecification, that.chargeSpecification )
                && Objects.equals( chargeAnalysisDetail, that.chargeAnalysisDetail );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( furnace, castingBatchOID, charge, alloy, plannedWeight, maxWeight, chargeSpecification, chargeAnalysisDetail );
    }

    @Override
    public String toString()
    {
        return "FurnaceInstructionDTO{" + "furnace='" + furnace + '\'' + ", castingBatchOID=" + castingBatchOID + ", charge='" + charge + '\'' + ", alloy='" + alloy + '\'' + ", plannedWeight="
                + plannedWeight + ", maxWeight=" + maxWeight + ", chargeSpecification=" + chargeSpecification + ", chargeAnalysisDetail=" + chargeAnalysisDetail + '}';
    }
}
