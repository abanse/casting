package com.hydro.casting.server.contract.dto;

import com.hydro.casting.common.Casting;
import com.hydro.core.common.util.NumberTools;
import com.hydro.core.common.util.StringTools;
import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class CasterScheduleDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private long id;
    private String machine;
    private String type;
    private long executingSequenceIndex;
    private int executionState;
    private String alloy;
    private String alloyQuality;
    private String processOrder;
    private double specWeight;
    private Double percentageS1;
    private Double percentageS2;
    private Double percentageS3;
    private Double percentageEL;
    private Double percentageRA;
    private Integer plannedMeltingDuration;
    private Integer plannedCastingDuration;
    private Long castingSequence;

    private Integer pos1Amount;
    private String pos1MaterialType;
    private String pos1CustomerOrderItem;
    private String pos1ExperimentNumber;
    private Double pos1Width;
    private Double pos1Length;
    private Double pos1LengthBonus;

    private Integer pos2Amount;
    private String pos2MaterialType;
    private String pos2CustomerOrderItem;
    private String pos2ExperimentNumber;
    private Double pos2Width;
    private Double pos2Length;
    private Double pos2LengthBonus;

    private Integer pos3Amount;
    private String pos3MaterialType;
    private String pos3CustomerOrderItem;
    private String pos3ExperimentNumber;
    private Double pos3Width;
    private Double pos3Length;
    private Double pos3LengthBonus;

    private Integer pos4Amount;
    private String pos4MaterialType;
    private String pos4CustomerOrderItem;
    private String pos4ExperimentNumber;
    private Double pos4Width;
    private Double pos4Length;
    private Double pos4LengthBonus;

    private Integer pos5Amount;
    private String pos5MaterialType;
    private String pos5CustomerOrderItem;
    private String pos5ExperimentNumber;
    private Double pos5Width;
    private Double pos5Length;
    private Double pos5LengthBonus;

    private String charge;
    private LocalDateTime inProgressTS;
    private Integer duration;
    private String annotation;
    private String meltingFurnace;
    private Integer plannedLength;
    private Double plannedWeight;
    private Double netWeight;

    private CasterSchedulePosDTO pos1;
    private CasterSchedulePosDTO pos2;
    private CasterSchedulePosDTO pos3;
    private CasterSchedulePosDTO pos4;
    private CasterSchedulePosDTO pos5;

    private transient CasterScheduleDTO previous;
    private transient CasterScheduleDTO next;

    @Override
    public long getId()
    {
        return id;
    }

    public void setId( long id )
    {
        this.id = id;
    }

    public String getMachine()
    {
        return machine;
    }

    public void setMachine( String machine )
    {
        this.machine = machine;
    }

    public long getExecutingSequenceIndex()
    {
        return executingSequenceIndex;
    }

    public void setExecutingSequenceIndex( long executingSequenceIndex )
    {
        this.executingSequenceIndex = executingSequenceIndex;
    }

    public int getExecutionState()
    {
        return executionState;
    }

    public void setExecutionState( int executionState )
    {
        this.executionState = executionState;
    }

    public String getAlloy()
    {
        return alloy;
    }

    public void setAlloy( String alloy )
    {
        this.alloy = alloy;
    }

    public String getAlloyQuality()
    {
        return alloyQuality;
    }

    public void setAlloyQuality( String alloyQuality )
    {
        this.alloyQuality = alloyQuality;
    }

    public String getProcessOrder()
    {
        return processOrder;
    }

    public void setProcessOrder( String processOrder )
    {
        this.processOrder = processOrder;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public double getSpecWeight()
    {
        if ( specWeight <= 0 )
        {
            return 2.7;
        }
        return specWeight;
    }

    public String getAnnotation()
    {
        return annotation;
    }

    public void setAnnotation( String annotation )
    {
        this.annotation = annotation;
    }

    public void setSpecWeight( double specWeight )
    {
        this.specWeight = specWeight;
    }

    public Double getPercentageS1()
    {
        return percentageS1;
    }

    public void setPercentageS1( Double percentageS1 )
    {
        this.percentageS1 = percentageS1;
    }

    public Double getPercentageS2()
    {
        return percentageS2;
    }

    public void setPercentageS2( Double percentageS2 )
    {
        this.percentageS2 = percentageS2;
    }

    public Double getPercentageS3()
    {
        return percentageS3;
    }

    public void setPercentageS3( Double percentageS3 )
    {
        this.percentageS3 = percentageS3;
    }

    public Double getPercentageEL()
    {
        return percentageEL;
    }

    public void setPercentageEL( Double percentageEL )
    {
        this.percentageEL = percentageEL;
    }

    public Double getPercentageRA()
    {
        return percentageRA;
    }

    public void setPercentageRA( Double percentageRA )
    {
        this.percentageRA = percentageRA;
    }

    public Integer getPlannedMeltingDuration()
    {
        return plannedMeltingDuration;
    }

    public void setPlannedMeltingDuration( Integer plannedMeltingDuration )
    {
        this.plannedMeltingDuration = plannedMeltingDuration;
    }

    public Integer getPlannedCastingDuration()
    {
        return plannedCastingDuration;
    }

    public void setPlannedCastingDuration( Integer plannedCastingDuration )
    {
        this.plannedCastingDuration = plannedCastingDuration;
    }

    public Long getCastingSequence()
    {
        return castingSequence;
    }

    public void setCastingSequence( Long castingSequence )
    {
        this.castingSequence = castingSequence;
    }

    public Integer getPos1Amount()
    {
        return pos1Amount;
    }

    public void setPos1Amount( Integer pos1Amount )
    {
        this.pos1Amount = pos1Amount;
    }

    public String getPos1MaterialType()
    {
        return pos1MaterialType;
    }

    public void setPos1MaterialType( String pos1MaterialType )
    {
        this.pos1MaterialType = pos1MaterialType;
    }

    public String getPos1CustomerOrderItem()
    {
        return pos1CustomerOrderItem;
    }

    public void setPos1CustomerOrderItem( String pos1CustomerOrderItem )
    {
        this.pos1CustomerOrderItem = pos1CustomerOrderItem;
    }

    public String getPos1ExperimentNumber()
    {
        return pos1ExperimentNumber;
    }

    public void setPos1ExperimentNumber( String pos1ExperimentNumber )
    {
        this.pos1ExperimentNumber = pos1ExperimentNumber;
    }

    public Double getPos1Width()
    {
        return pos1Width;
    }

    public void setPos1Width( Double pos1Width )
    {
        this.pos1Width = pos1Width;
    }

    public Double getPos1Length()
    {
        return pos1Length;
    }

    public void setPos1Length( Double pos1Length )
    {
        this.pos1Length = pos1Length;
    }

    public Double getPos1LengthBonus()
    {
        return pos1LengthBonus;
    }

    public void setPos1LengthBonus( Double pos1LengthBonus )
    {
        this.pos1LengthBonus = pos1LengthBonus;
    }

    public Integer getPos2Amount()
    {
        return pos2Amount;
    }

    public void setPos2Amount( Integer pos2Amount )
    {
        this.pos2Amount = pos2Amount;
    }

    public String getPos2MaterialType()
    {
        return pos2MaterialType;
    }

    public void setPos2MaterialType( String pos2MaterialType )
    {
        this.pos2MaterialType = pos2MaterialType;
    }

    public String getPos2CustomerOrderItem()
    {
        return pos2CustomerOrderItem;
    }

    public void setPos2CustomerOrderItem( String pos2CustomerOrderItem )
    {
        this.pos2CustomerOrderItem = pos2CustomerOrderItem;
    }

    public String getPos2ExperimentNumber()
    {
        return pos2ExperimentNumber;
    }

    public void setPos2ExperimentNumber( String pos2ExperimentNumber )
    {
        this.pos2ExperimentNumber = pos2ExperimentNumber;
    }

    public Double getPos2Width()
    {
        return pos2Width;
    }

    public void setPos2Width( Double pos2Width )
    {
        this.pos2Width = pos2Width;
    }

    public Double getPos2Length()
    {
        return pos2Length;
    }

    public void setPos2Length( Double pos2Length )
    {
        this.pos2Length = pos2Length;
    }

    public Double getPos2LengthBonus()
    {
        return pos2LengthBonus;
    }

    public void setPos2LengthBonus( Double pos2LengthBonus )
    {
        this.pos2LengthBonus = pos2LengthBonus;
    }

    public Integer getPos3Amount()
    {
        return pos3Amount;
    }

    public void setPos3Amount( Integer pos3Amount )
    {
        this.pos3Amount = pos3Amount;
    }

    public String getPos3MaterialType()
    {
        return pos3MaterialType;
    }

    public void setPos3MaterialType( String pos3MaterialType )
    {
        this.pos3MaterialType = pos3MaterialType;
    }

    public String getPos3CustomerOrderItem()
    {
        return pos3CustomerOrderItem;
    }

    public void setPos3CustomerOrderItem( String pos3CustomerOrderItem )
    {
        this.pos3CustomerOrderItem = pos3CustomerOrderItem;
    }

    public String getPos3ExperimentNumber()
    {
        return pos3ExperimentNumber;
    }

    public void setPos3ExperimentNumber( String pos3ExperimentNumber )
    {
        this.pos3ExperimentNumber = pos3ExperimentNumber;
    }

    public Double getPos3Width()
    {
        return pos3Width;
    }

    public void setPos3Width( Double pos3Width )
    {
        this.pos3Width = pos3Width;
    }

    public Double getPos3Length()
    {
        return pos3Length;
    }

    public void setPos3Length( Double pos3Length )
    {
        this.pos3Length = pos3Length;
    }

    public Double getPos3LengthBonus()
    {
        return pos3LengthBonus;
    }

    public void setPos3LengthBonus( Double pos3LengthBonus )
    {
        this.pos3LengthBonus = pos3LengthBonus;
    }

    public Integer getPos4Amount()
    {
        return pos4Amount;
    }

    public void setPos4Amount( Integer pos4Amount )
    {
        this.pos4Amount = pos4Amount;
    }

    public String getPos4MaterialType()
    {
        return pos4MaterialType;
    }

    public void setPos4MaterialType( String pos4MaterialType )
    {
        this.pos4MaterialType = pos4MaterialType;
    }

    public String getPos4CustomerOrderItem()
    {
        return pos4CustomerOrderItem;
    }

    public void setPos4CustomerOrderItem( String pos4CustomerOrderItem )
    {
        this.pos4CustomerOrderItem = pos4CustomerOrderItem;
    }

    public String getPos4ExperimentNumber()
    {
        return pos4ExperimentNumber;
    }

    public void setPos4ExperimentNumber( String pos4ExperimentNumber )
    {
        this.pos4ExperimentNumber = pos4ExperimentNumber;
    }

    public Double getPos4Width()
    {
        return pos4Width;
    }

    public void setPos4Width( Double pos4Width )
    {
        this.pos4Width = pos4Width;
    }

    public Double getPos4Length()
    {
        return pos4Length;
    }

    public void setPos4Length( Double pos4Length )
    {
        this.pos4Length = pos4Length;
    }

    public Double getPos4LengthBonus()
    {
        return pos4LengthBonus;
    }

    public void setPos4LengthBonus( Double pos4LengthBonus )
    {
        this.pos4LengthBonus = pos4LengthBonus;
    }

    public Integer getPos5Amount()
    {
        return pos5Amount;
    }

    public void setPos5Amount( Integer pos5Amount )
    {
        this.pos5Amount = pos5Amount;
    }

    public String getPos5MaterialType()
    {
        return pos5MaterialType;
    }

    public void setPos5MaterialType( String pos5MaterialType )
    {
        this.pos5MaterialType = pos5MaterialType;
    }

    public String getPos5CustomerOrderItem()
    {
        return pos5CustomerOrderItem;
    }

    public void setPos5CustomerOrderItem( String pos5CustomerOrderItem )
    {
        this.pos5CustomerOrderItem = pos5CustomerOrderItem;
    }

    public String getPos5ExperimentNumber()
    {
        return pos5ExperimentNumber;
    }

    public void setPos5ExperimentNumber( String pos5ExperimentNumber )
    {
        this.pos5ExperimentNumber = pos5ExperimentNumber;
    }

    public Double getPos5Width()
    {
        return pos5Width;
    }

    public void setPos5Width( Double pos5Width )
    {
        this.pos5Width = pos5Width;
    }

    public Double getPos5Length()
    {
        return pos5Length;
    }

    public void setPos5Length( Double pos5Length )
    {
        this.pos5Length = pos5Length;
    }

    public Double getPos5LengthBonus()
    {
        return pos5LengthBonus;
    }

    public void setPos5LengthBonus( Double pos5LengthBonus )
    {
        this.pos5LengthBonus = pos5LengthBonus;
    }

    public String getCharge()
    {
        if ( "setup".equals( type ) )
        {
            return null;
        }
//        if ( charge == null )
//        {
//            final CasterScheduleDTO prevCastingBatch = findPrevCastingBatch( previous );
//            if ( prevCastingBatch != null )
//            {
//                charge = Casting.getNextCharge( machine, prevCastingBatch.getCharge() );
//            }
//        }
        return charge;
    }

    public void setCharge( String charge )
    {
        this.charge = charge;
    }

    public String getChargeWithoutYear()
    {
        final String charge = getCharge();
        if ( StringTools.isFilled( charge ) && charge.length() > 2 )
        {
            return charge.substring( 2 );
        }
        return null;
    }

    public void setChargeWithoutYear(String chargeWithoutYear)
    {
        // only getter needed
    }

    public LocalDateTime getInProgressTS()
    {
        if ( inProgressTS == null && previous != null && previous.getInProgressTS() != null && previous.getDuration() != null )
        {
            inProgressTS = previous.getInProgressTS().plus( previous.getDuration(), ChronoUnit.MINUTES );
        }
        return inProgressTS;
    }

    public void setInProgressTS( LocalDateTime inProgressTS )
    {
        this.inProgressTS = inProgressTS;
    }

    public Integer getDuration()
    {
        if ( duration == null )
        {
            duration = Casting.getDefaultDuration( machine );
        }
        return duration;
    }

    public void setDuration( Integer duration )
    {
        this.duration = duration;
    }

    public String getMeltingFurnace()
    {
        if ( "setup".equals( type ) )
        {
            return null;
        }
        if ( meltingFurnace == null )
        {
            final CasterScheduleDTO prevCastingBatch = findPrevCastingBatch( previous );
            if ( prevCastingBatch != null )
            {
                // get opposite
                meltingFurnace = Casting.getOppositeMeltingFurnace( machine, prevCastingBatch.getMeltingFurnace() );
            }
        }
        return meltingFurnace;
    }

    public void setMeltingFurnace( String meltingFurnace )
    {
        this.meltingFurnace = meltingFurnace;
    }

    public int getPos1CastingLength()
    {
        if ( pos1Length != null )
        {
            if ( pos1LengthBonus != null )
            {
                if ( pos1Amount != null )
                {
                    return ( pos1Length.intValue() * pos1Amount.intValue() ) + pos1LengthBonus.intValue();
                }
                return pos1Length.intValue() + pos1LengthBonus.intValue();
            }
            return pos1Length.intValue();
        }
        return 0;
    }

    public int getPos2CastingLength()
    {
        if ( pos2Length != null )
        {
            if ( pos2LengthBonus != null )
            {
                if ( pos2Amount != null )
                {
                    return ( pos2Length.intValue() * pos2Amount.intValue() ) + pos2LengthBonus.intValue();
                }
                return pos2Length.intValue() + pos2LengthBonus.intValue();
            }
            return pos2Length.intValue();
        }
        return 0;
    }

    public int getPos3CastingLength()
    {
        if ( pos3Length != null )
        {
            if ( pos3LengthBonus != null )
            {
                if ( pos3Amount != null )
                {
                    return ( pos3Length.intValue() * pos3Amount.intValue() ) + pos3LengthBonus.intValue();
                }
                return pos3Length.intValue() + pos3LengthBonus.intValue();
            }
            return pos3Length.intValue();
        }
        return 0;
    }

    public int getPos4CastingLength()
    {
        if ( pos4Length != null )
        {
            if ( pos4LengthBonus != null )
            {
                if ( pos4Amount != null )
                {
                    return ( pos4Length.intValue() * pos4Amount.intValue() ) + pos4LengthBonus.intValue();
                }
                return pos4Length.intValue() + pos4LengthBonus.intValue();
            }
            return pos4Length.intValue();
        }
        return 0;
    }

    public int getPos5CastingLength()
    {
        if ( pos5Length != null )
        {
            if ( pos5LengthBonus != null )
            {
                if ( pos5Amount != null )
                {
                    return ( pos5Length.intValue() * pos5Amount.intValue() ) + pos5LengthBonus.intValue();
                }
                return pos5Length.intValue() + pos5LengthBonus.intValue();
            }
            return pos5Length.intValue();
        }
        return 0;
    }

    public Integer getPlannedLength()
    {
        if ( plannedLength == null || plannedLength <= 0 )
        {
            int maxLength = 0;
            if ( getPos1CastingLength() > maxLength )
            {
                maxLength = getPos1CastingLength();
            }
            if ( getPos2CastingLength() > maxLength )
            {
                maxLength = getPos2CastingLength();
            }
            if ( getPos3CastingLength() > maxLength )
            {
                maxLength = getPos3CastingLength();
            }
            if ( getPos4CastingLength() > maxLength )
            {
                maxLength = getPos4CastingLength();
            }
            if ( getPos5CastingLength() > maxLength )
            {
                maxLength = getPos5CastingLength();
            }
            return maxLength;
        }
        return plannedLength;
    }

    public void setPlannedLength( Integer plannedLength )
    {
        this.plannedLength = plannedLength;
    }

    public double getPlannedWeight()
    {
        if ( plannedWeight == null || plannedWeight <= 0 )
        {
            final double plannedLength = getPlannedLength();
            final double pos1w = NumberTools.getNullSafe( pos1Width );
            final double pos2w = NumberTools.getNullSafe( pos2Width );
            final double pos3w = NumberTools.getNullSafe( pos3Width );
            final double pos4w = NumberTools.getNullSafe( pos4Width );
            final double pos5w = NumberTools.getNullSafe( pos5Width );
            return getSpecWeight() / 1000000 * plannedLength * 600 * ( pos1w + pos2w + pos3w + pos4w + pos5w );
        }
        return plannedWeight;
    }

    public void setPlannedWeight( Double plannedWeight )
    {
        this.plannedWeight = plannedWeight;
    }

    public Double getNetWeight()
    {
        if ( netWeight == null || netWeight <= 0 )
        {
            final double pos1w = NumberTools.getNullSafe( pos1Width ) * 600 * NumberTools.getNullSafe( pos1Length ) * getSpecWeight() / 1000000;
            final double pos2w = NumberTools.getNullSafe( pos2Width ) * 600 * NumberTools.getNullSafe( pos2Length ) * getSpecWeight() / 1000000;
            final double pos3w = NumberTools.getNullSafe( pos3Width ) * 600 * NumberTools.getNullSafe( pos3Length ) * getSpecWeight() / 1000000;
            final double pos4w = NumberTools.getNullSafe( pos4Width ) * 600 * NumberTools.getNullSafe( pos4Length ) * getSpecWeight() / 1000000;
            final double pos5w = NumberTools.getNullSafe( pos5Width ) * 600 * NumberTools.getNullSafe( pos5Length ) * getSpecWeight() / 1000000;
            return pos1w + pos2w + pos3w + pos4w + pos5w;
        }

        return netWeight;
    }

    public void setNetWeight( Double netWeight )
    {
        this.netWeight = netWeight;
    }

    public CasterScheduleDTO getPrevious()
    {
        return previous;
    }

    public void setPrevious( CasterScheduleDTO previous )
    {
        this.previous = previous;
    }

    public CasterScheduleDTO getNext()
    {
        return next;
    }

    public void setNext( CasterScheduleDTO next )
    {
        this.next = next;
    }

    public CasterSchedulePosDTO getPos( int pos )
    {
        if ( pos == 1 )
        {
            return getPos1();
        }
        else if ( pos == 2 )
        {
            return getPos2();
        }
        else if ( pos == 3 )
        {
            return getPos3();
        }
        else if ( pos == 4 )
        {
            return getPos4();
        }
        else if ( pos == 5 )
        {
            return getPos5();
        }
        return null;
    }

    public CasterSchedulePosDTO getPos1()
    {
        if ( pos1 == null )
        {
            pos1 = new CasterSchedulePosDTO();
            pos1.setCasterSchedule( this );
            pos1.setPosition( 1 );
            if ( pos1MaterialType != null )
            {
                pos1.setAmount( pos1Amount );
                pos1.setMaterialType( pos1MaterialType );
                pos1.setCustomerOrderItem( pos1CustomerOrderItem );
                pos1.setExperimentNumber( pos1ExperimentNumber );
                pos1.setWidth( pos1Width );
                pos1.setLength( pos1Length );
                pos1.setLengthBonus( pos1LengthBonus );
            }
        }
        return pos1;
    }

    public CasterSchedulePosDTO getPos2()
    {
        if ( pos2 == null )
        {
            pos2 = new CasterSchedulePosDTO();
            pos2.setCasterSchedule( this );
            pos2.setPosition( 2 );
            if ( pos2MaterialType != null )
            {
                pos2.setAmount( pos2Amount );
                pos2.setMaterialType( pos2MaterialType );
                pos2.setCustomerOrderItem( pos2CustomerOrderItem );
                pos2.setExperimentNumber( pos2ExperimentNumber );
                pos2.setWidth( pos2Width );
                pos2.setLength( pos2Length );
                pos2.setLengthBonus( pos2LengthBonus );
            }
        }
        return pos2;
    }

    public CasterSchedulePosDTO getPos3()
    {
        if ( pos3 == null )
        {
            pos3 = new CasterSchedulePosDTO();
            pos3.setCasterSchedule( this );
            pos3.setPosition( 3 );
            if ( pos3MaterialType != null )
            {
                pos3.setAmount( pos3Amount );
                pos3.setMaterialType( pos3MaterialType );
                pos3.setCustomerOrderItem( pos3CustomerOrderItem );
                pos3.setExperimentNumber( pos3ExperimentNumber );
                pos3.setWidth( pos3Width );
                pos3.setLength( pos3Length );
                pos3.setLengthBonus( pos3LengthBonus );
            }
        }
        return pos3;
    }

    public CasterSchedulePosDTO getPos4()
    {
        if ( pos4 == null )
        {
            pos4 = new CasterSchedulePosDTO();
            pos4.setCasterSchedule( this );
            pos4.setPosition( 4 );
            if ( pos4MaterialType != null )
            {
                pos4.setAmount( pos4Amount );
                pos4.setMaterialType( pos4MaterialType );
                pos4.setCustomerOrderItem( pos4CustomerOrderItem );
                pos4.setExperimentNumber( pos4ExperimentNumber );
                pos4.setWidth( pos4Width );
                pos4.setLength( pos4Length );
                pos4.setLengthBonus( pos4LengthBonus );
            }
        }
        return pos4;
    }

    public CasterSchedulePosDTO getPos5()
    {
        if ( pos5 == null )
        {
            pos5 = new CasterSchedulePosDTO();
            pos5.setCasterSchedule( this );
            pos5.setPosition( 5 );
            if ( pos5MaterialType != null )
            {
                pos5.setAmount( pos5Amount );
                pos5.setMaterialType( pos5MaterialType );
                pos5.setCustomerOrderItem( pos5CustomerOrderItem );
                pos5.setExperimentNumber( pos5ExperimentNumber );
                pos5.setWidth( pos5Width );
                pos5.setLength( pos5Length );
                pos5.setLengthBonus( pos5LengthBonus );
            }
        }
        return pos5;
    }

    public String getPlannedCalenderWeek()
    {
        final LocalDateTime calcStart = getInProgressTS();
        if ( calcStart != null )
        {
            return "" + calcStart.get( ChronoField.ALIGNED_WEEK_OF_YEAR );
        }
        return null;
    }

    public CasterScheduleDTO findPrevCastingBatch()
    {
        return findPrevCastingBatch( previous );
    }

    private CasterScheduleDTO findPrevCastingBatch( final CasterScheduleDTO casterScheduleDTO )
    {
        if ( casterScheduleDTO == null )
        {
            return null;
        }
        if ( !"setup".equals( casterScheduleDTO.getType() ) )
        {
            return casterScheduleDTO;
        }
        return findPrevCastingBatch( casterScheduleDTO.getPrevious() );
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        CasterScheduleDTO that = (CasterScheduleDTO) o;
        return id == that.id && executingSequenceIndex == that.executingSequenceIndex && executionState == that.executionState && Double.compare( that.specWeight, specWeight ) == 0 && Objects.equals(
                machine, that.machine ) && Objects.equals( type, that.type ) && Objects.equals( alloy, that.alloy ) && Objects.equals( alloyQuality, that.alloyQuality ) && Objects.equals(
                processOrder, that.processOrder ) && Objects.equals( percentageS1, that.percentageS1 ) && Objects.equals( percentageS2, that.percentageS2 ) && Objects.equals( percentageS3,
                that.percentageS3 ) && Objects.equals( percentageEL, that.percentageEL ) && Objects.equals( percentageRA, that.percentageRA ) && Objects.equals( plannedMeltingDuration,
                that.plannedMeltingDuration ) && Objects.equals( plannedCastingDuration, that.plannedCastingDuration ) && Objects.equals( castingSequence, that.castingSequence ) && Objects.equals(
                pos1Amount, that.pos1Amount ) && Objects.equals( pos1MaterialType, that.pos1MaterialType ) && Objects.equals( pos1CustomerOrderItem, that.pos1CustomerOrderItem ) && Objects.equals(
                pos1ExperimentNumber, that.pos1ExperimentNumber ) && Objects.equals( pos1Width, that.pos1Width ) && Objects.equals( pos1Length, that.pos1Length ) && Objects.equals( pos1LengthBonus,
                that.pos1LengthBonus ) && Objects.equals( pos2Amount, that.pos2Amount ) && Objects.equals( pos2MaterialType, that.pos2MaterialType ) && Objects.equals( pos2CustomerOrderItem,
                that.pos2CustomerOrderItem ) && Objects.equals( pos2ExperimentNumber, that.pos2ExperimentNumber ) && Objects.equals( pos2Width, that.pos2Width ) && Objects.equals( pos2Length,
                that.pos2Length ) && Objects.equals( pos2LengthBonus, that.pos2LengthBonus ) && Objects.equals( pos3Amount, that.pos3Amount ) && Objects.equals( pos3MaterialType,
                that.pos3MaterialType ) && Objects.equals( pos3CustomerOrderItem, that.pos3CustomerOrderItem ) && Objects.equals( pos3ExperimentNumber, that.pos3ExperimentNumber ) && Objects.equals(
                pos3Width, that.pos3Width ) && Objects.equals( pos3Length, that.pos3Length ) && Objects.equals( pos3LengthBonus, that.pos3LengthBonus ) && Objects.equals( pos4Amount, that.pos4Amount )
                && Objects.equals( pos4MaterialType, that.pos4MaterialType ) && Objects.equals( pos4CustomerOrderItem, that.pos4CustomerOrderItem ) && Objects.equals( pos4ExperimentNumber,
                that.pos4ExperimentNumber ) && Objects.equals( pos4Width, that.pos4Width ) && Objects.equals( pos4Length, that.pos4Length ) && Objects.equals( pos4LengthBonus, that.pos4LengthBonus )
                && Objects.equals( pos5Amount, that.pos5Amount ) && Objects.equals( pos5MaterialType, that.pos5MaterialType ) && Objects.equals( pos5CustomerOrderItem, that.pos5CustomerOrderItem )
                && Objects.equals( pos5ExperimentNumber, that.pos5ExperimentNumber ) && Objects.equals( pos5Width, that.pos5Width ) && Objects.equals( pos5Length, that.pos5Length ) && Objects.equals(
                pos5LengthBonus, that.pos5LengthBonus ) && Objects.equals( charge, that.charge ) && Objects.equals( inProgressTS, that.inProgressTS ) && Objects.equals( duration, that.duration )
                && Objects.equals( annotation, that.annotation ) && Objects.equals( meltingFurnace, that.meltingFurnace ) && Objects.equals( plannedLength, that.plannedLength ) && Objects.equals(
                plannedWeight, that.plannedWeight ) && Objects.equals( netWeight, that.netWeight );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, machine, type, executingSequenceIndex, executionState, alloy, alloyQuality, processOrder, specWeight, percentageS1, percentageS2, percentageS3, percentageEL,
                percentageRA, plannedMeltingDuration, plannedCastingDuration, castingSequence, pos1Amount, pos1MaterialType, pos1CustomerOrderItem, pos1ExperimentNumber, pos1Width, pos1Length,
                pos1LengthBonus, pos2Amount, pos2MaterialType, pos2CustomerOrderItem, pos2ExperimentNumber, pos2Width, pos2Length, pos2LengthBonus, pos3Amount, pos3MaterialType, pos3CustomerOrderItem,
                pos3ExperimentNumber, pos3Width, pos3Length, pos3LengthBonus, pos4Amount, pos4MaterialType, pos4CustomerOrderItem, pos4ExperimentNumber, pos4Width, pos4Length, pos4LengthBonus,
                pos5Amount, pos5MaterialType, pos5CustomerOrderItem, pos5ExperimentNumber, pos5Width, pos5Length, pos5LengthBonus, charge, inProgressTS, duration, annotation, meltingFurnace,
                plannedLength, plannedWeight, netWeight );
    }

    @Override
    public String toString()
    {
        return "CasterScheduleDTO{" + "id=" + id + ", machine='" + machine + '\'' + ", type='" + type + '\'' + ", executingSequenceIndex=" + executingSequenceIndex + ", executionState="
                + executionState + ", alloy='" + alloy + '\'' + ", alloyQuality='" + alloyQuality + '\'' + ", processOrder='" + processOrder + '\'' + ", specWeight=" + specWeight + ", percentageS1="
                + percentageS1 + ", percentageS2=" + percentageS2 + ", percentageS3=" + percentageS3 + ", percentageEL=" + percentageEL + ", percentageRA=" + percentageRA + ", plannedMeltingDuration="
                + plannedMeltingDuration + ", plannedCastingDuration=" + plannedCastingDuration + ", castingSequence=" + castingSequence + ", pos1Amount=" + pos1Amount + ", pos1MaterialType='"
                + pos1MaterialType + '\'' + ", pos1CustomerOrderItem='" + pos1CustomerOrderItem + '\'' + ", pos1ExperimentNumber='" + pos1ExperimentNumber + '\'' + ", pos1Width=" + pos1Width
                + ", pos1Length=" + pos1Length + ", pos1LengthBonus=" + pos1LengthBonus + ", pos2Amount=" + pos2Amount + ", pos2MaterialType='" + pos2MaterialType + '\'' + ", pos2CustomerOrderItem='"
                + pos2CustomerOrderItem + '\'' + ", pos2ExperimentNumber='" + pos2ExperimentNumber + '\'' + ", pos2Width=" + pos2Width + ", pos2Length=" + pos2Length + ", pos2LengthBonus="
                + pos2LengthBonus + ", pos3Amount=" + pos3Amount + ", pos3MaterialType='" + pos3MaterialType + '\'' + ", pos3CustomerOrderItem='" + pos3CustomerOrderItem + '\''
                + ", pos3ExperimentNumber='" + pos3ExperimentNumber + '\'' + ", pos3Width=" + pos3Width + ", pos3Length=" + pos3Length + ", pos3LengthBonus=" + pos3LengthBonus + ", pos4Amount="
                + pos4Amount + ", pos4MaterialType='" + pos4MaterialType + '\'' + ", pos4CustomerOrderItem='" + pos4CustomerOrderItem + '\'' + ", pos4ExperimentNumber='" + pos4ExperimentNumber + '\''
                + ", pos4Width=" + pos4Width + ", pos4Length=" + pos4Length + ", pos4LengthBonus=" + pos4LengthBonus + ", pos5Amount=" + pos5Amount + ", pos5MaterialType='" + pos5MaterialType + '\''
                + ", pos5CustomerOrderItem='" + pos5CustomerOrderItem + '\'' + ", pos5ExperimentNumber='" + pos5ExperimentNumber + '\'' + ", pos5Width=" + pos5Width + ", pos5Length=" + pos5Length
                + ", pos5LengthBonus=" + pos5LengthBonus + ", charge='" + charge + '\'' + ", inProgressTS=" + inProgressTS + ", duration=" + duration + ", annotation='" + annotation + '\''
                + ", meltingFurnace='" + meltingFurnace + '\'' + ", plannedLength=" + plannedLength + ", plannedWeight=" + plannedWeight + ", netWeight=" + netWeight + '}';
    }
}
