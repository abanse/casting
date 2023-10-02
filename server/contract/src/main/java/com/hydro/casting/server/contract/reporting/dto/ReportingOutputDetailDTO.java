package com.hydro.casting.server.contract.reporting.dto;

import com.hydro.core.common.util.DateTimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

public class ReportingOutputDetailDTO extends ReportingDTO
{
    private static final long serialVersionUID = 1L;

    private final static Logger log = LoggerFactory.getLogger( ReportingOutputDetailDTO.class );

    private Long opHistId;
    private String scheduleNbr;
    private String lot;
    private String sublot;
    private String invSuffix;
    private Integer operationSeq;
    private String parentLot;
    private String parentSublot;
    private String parentInvSuffix;
    private Integer parentOperationSeq;
    private Date finishedDate;
    private Integer finishedTime;
    private String shift;
    private Integer exitArbor;
    private Double thickness;
    private Double thicknessIn;
    private Double width;
    private Double length;
    private Long weight;
    private Long weightIn;
    private Double buildup;
    private Integer cutId;
    private String dropId;
    private Integer piecesOut;
    private String firstPass;
    private String lastPass;
    private String reworkReason;
    private String finishingCc;
    private String customerName;
    private String customerNbr;
    private String outputType;
    private Integer numberPlates;
    private Integer multipleScalped;
    private String sharpened;
    private Long scrapWeight1;
    private Long scrapWeight2;
    private Double standardScrapFactor;

    @Override
    public long getId()
    {
        return 0;
    }

    public Long getOpHistId()
    {
        if ( opHistId == null )
        {
            log.warn( "opHistId is null" );
            return 0l;
        }
        return opHistId;
    }

    public void setOpHistId( Long opHistId )
    {
        this.opHistId = opHistId;
    }

    public String getScheduleNbr()
    {
        return scheduleNbr;
    }

    public void setScheduleNbr( String scheduleNbr )
    {
        this.scheduleNbr = scheduleNbr;
    }

    public String getLot()
    {
        return lot;
    }

    public void setLot( String lot )
    {
        this.lot = lot;
    }

    public String getSublot()
    {
        return sublot;
    }

    public void setSublot( String sublot )
    {
        this.sublot = sublot;
    }

    public String getInvSuffix()
    {
        return invSuffix;
    }

    public void setInvSuffix( String invSuffix )
    {
        this.invSuffix = invSuffix;
    }

    public Integer getOperationSeq()
    {
        if ( operationSeq == null )
        {
            log.warn( "operationSeq is null opHistId: " + getOpHistId() );
            return 0;
        }

        return operationSeq;
    }

    public void setOperationSeq( Integer operationSeq )
    {
        this.operationSeq = operationSeq;
    }

    public String getParentLot()
    {
        return parentLot;
    }

    public void setParentLot( String parentLot )
    {
        this.parentLot = parentLot;
    }

    public String getParentSublot()
    {
        return parentSublot;
    }

    public void setParentSublot( String parentSublot )
    {
        this.parentSublot = parentSublot;
    }

    public String getParentInvSuffix()
    {
        return parentInvSuffix;
    }

    public void setParentInvSuffix( String parentInvSuffix )
    {
        this.parentInvSuffix = parentInvSuffix;
    }

    public Integer getParentOperationSeq()
    {
        return parentOperationSeq;
    }

    public void setParentOperationSeq( Integer parentOperationSeq )
    {
        this.parentOperationSeq = parentOperationSeq;
    }

    public Date getFinishedDate()
    {
        return finishedDate;
    }

    public void setFinishedDate( Date finishedDate )
    {
        this.finishedDate = finishedDate;
    }

    public Integer getFinishedTime()
    {
        if ( finishedTime == null )
        {
            log.warn( "finishedTime is null opHistId: " + getOpHistId() );
            return 0;
        }

        return finishedTime;
    }

    public void setFinishedTime( Integer finishedTime )
    {
        this.finishedTime = finishedTime;
    }

    public String getShift()
    {
        return shift;
    }

    public void setShift( String shift )
    {
        this.shift = shift;
    }

    public Integer getExitArbor()
    {
        if ( exitArbor == null )
        {
            log.warn( "exitArbor is null opHistId: " + getOpHistId() );
            return 0;
        }
        return exitArbor;
    }

    public void setExitArbor( Integer exitArbor )
    {
        this.exitArbor = exitArbor;
    }

    public Double getThickness()
    {
        if ( thickness == null )
        {
            log.warn( "thickness is null opHistId: " + getOpHistId() );
            return 0d;
        }
        return thickness;
    }

    public void setThickness( Double thickness )
    {
        this.thickness = thickness;
    }

    public Double getThicknessIn()
    {
        if ( thicknessIn == null )
        {
            log.warn( "thicknessIn is null opHistId: " + getOpHistId() );
            return 0d;
        }
        return thicknessIn;
    }

    public void setThicknessIn( Double thicknessIn )
    {
        this.thicknessIn = thicknessIn;
    }

    public Double getWidth()
    {
        if ( width == null )
        {
            log.warn( "width is null opHistId: " + getOpHistId() );
            return 0d;
        }
        return width;
    }

    public void setWidth( Double width )
    {
        this.width = width;
    }

    public Double getLength()
    {
        if ( length == null )
        {
            log.warn( "length is null opHistId: " + getOpHistId() );
            return 0d;
        }
        return length;
    }

    public void setLength( Double length )
    {
        this.length = length;
    }

    public Long getWeight()
    {
        if ( weight == null )
        {
            log.warn( "weight is null opHistId: " + getOpHistId() );
            return 0l;
        }
        return weight;
    }

    public void setWeight( Long weight )
    {
        this.weight = weight;
    }

    public Long getWeightIn()
    {
        if ( weightIn == null )
        {
            log.warn( "weightIn is null opHistId: " + getOpHistId() );
            return 0l;
        }
        return weightIn;
    }

    public void setWeightIn( Long weightIn )
    {
        this.weightIn = weightIn;
    }

    public Double getBuildup()
    {
        if ( buildup == null )
        {
            log.warn( "buildup is null opHistId: " + getOpHistId() );
            return 0d;
        }
        return buildup;
    }

    public void setBuildup( Double buildup )
    {
        this.buildup = buildup;
    }

    public Integer getCutId()
    {
        return cutId;
    }

    public void setCutId( Integer cutId )
    {
        this.cutId = cutId;
    }

    public String getDropId()
    {
        return dropId;
    }

    public void setDropId( String dropId )
    {
        this.dropId = dropId;
    }

    public Integer getPiecesOut()
    {
        if ( piecesOut == null )
        {
            log.warn( "piecesOut is null opHistId: " + getOpHistId() );
            return 0;
        }
        return piecesOut;
    }

    public void setPiecesOut( Integer piecesOut )
    {
        this.piecesOut = piecesOut;
    }

    public String getFirstPass()
    {
        return firstPass;
    }

    public void setFirstPass( String firstPass )
    {
        this.firstPass = firstPass;
    }

    public String getLastPass()
    {
        return lastPass;
    }

    public void setLastPass( String lastPass )
    {
        this.lastPass = lastPass;
    }

    public String getReworkReason()
    {
        return reworkReason;
    }

    public void setReworkReason( String reworkReason )
    {
        this.reworkReason = reworkReason;
    }

    public String getFinishingCc()
    {
        return finishingCc;
    }

    public void setFinishingCc( String finishingCc )
    {
        this.finishingCc = finishingCc;
    }

    public String getCustomerName()
    {
        return customerName;
    }

    public void setCustomerName( String customerName )
    {
        this.customerName = customerName;
    }

    public String getCustomerNbr()
    {
        return customerNbr;
    }

    public void setCustomerNbr( String customerNbr )
    {
        this.customerNbr = customerNbr;
    }

    public String getOutputType()
    {
        return outputType;
    }

    public void setOutputType( String outputType )
    {
        this.outputType = outputType;
    }

    public Integer getNumberPlates()
    {
        if ( numberPlates == null )
        {
            log.warn( "numberPlates is null opHistId: " + getOpHistId() );
            return 0;
        }
        return numberPlates;
    }

    public void setNumberPlates( Integer numberPlates )
    {
        this.numberPlates = numberPlates;
    }

    public LocalDateTime getFinishedLocalDateTime()
    {
        return DateTimeUtil.getDateTime( finishedDate, finishedTime );
    }

    public Integer getMultipleScalped()
    {
        return multipleScalped;
    }

    public void setMultipleScalped( Integer multipleScalped )
    {
        this.multipleScalped = multipleScalped;
    }

    public String getSharpened()
    {
        return sharpened;
    }

    public void setSharpened( String sharpened )
    {
        this.sharpened = sharpened;
    }

    public Long getScrapWeight1()
    {
        return scrapWeight1;
    }

    public void setScrapWeight1( Long scrapWeight1 )
    {
        this.scrapWeight1 = scrapWeight1;
    }

    public Long getScrapWeight2()
    {
        return scrapWeight2;
    }

    public void setScrapWeight2( Long scrapWeight2 )
    {
        this.scrapWeight2 = scrapWeight2;
    }

    public Double getStandardScrapFactor()
    {
        return standardScrapFactor;
    }

    public void setStandardScrapFactor( Double standardScrapFactor )
    {
        this.standardScrapFactor = standardScrapFactor;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        if ( !super.equals( o ) )
            return false;
        ReportingOutputDetailDTO that = (ReportingOutputDetailDTO) o;
        return Objects.equals( opHistId, that.opHistId ) && Objects.equals( scheduleNbr, that.scheduleNbr ) && Objects.equals( lot, that.lot ) && Objects.equals( sublot, that.sublot )
                && Objects.equals( invSuffix, that.invSuffix ) && Objects.equals( operationSeq, that.operationSeq ) && Objects.equals( parentLot, that.parentLot ) && Objects.equals( parentSublot,
                that.parentSublot ) && Objects.equals( parentInvSuffix, that.parentInvSuffix ) && Objects.equals( parentOperationSeq, that.parentOperationSeq ) && Objects.equals( finishedDate,
                that.finishedDate ) && Objects.equals( finishedTime, that.finishedTime ) && Objects.equals( shift, that.shift ) && Objects.equals( exitArbor, that.exitArbor ) && Objects.equals(
                thickness, that.thickness ) && Objects.equals( thicknessIn, that.thicknessIn ) && Objects.equals( width, that.width ) && Objects.equals( length, that.length ) && Objects.equals(
                weight, that.weight ) && Objects.equals( weightIn, that.weightIn ) && Objects.equals( buildup, that.buildup ) && Objects.equals( cutId, that.cutId ) && Objects.equals( dropId,
                that.dropId ) && Objects.equals( piecesOut, that.piecesOut ) && Objects.equals( firstPass, that.firstPass ) && Objects.equals( lastPass, that.lastPass ) && Objects.equals(
                reworkReason, that.reworkReason ) && Objects.equals( finishingCc, that.finishingCc ) && Objects.equals( customerName, that.customerName ) && Objects.equals( customerNbr,
                that.customerNbr ) && Objects.equals( outputType, that.outputType ) && Objects.equals( numberPlates, that.numberPlates ) && Objects.equals( multipleScalped, that.multipleScalped )
                && Objects.equals( sharpened, that.sharpened ) && Objects.equals( scrapWeight1, that.scrapWeight1 ) && Objects.equals( scrapWeight2, that.scrapWeight2 ) && Objects.equals(
                standardScrapFactor, that.standardScrapFactor );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), opHistId, scheduleNbr, lot, sublot, invSuffix, operationSeq, parentLot, parentSublot, parentInvSuffix, parentOperationSeq, finishedDate, finishedTime,
                shift, exitArbor, thickness, thicknessIn, width, length, weight, weightIn, buildup, cutId, dropId, piecesOut, firstPass, lastPass, reworkReason, finishingCc, customerName, customerNbr,
                outputType, numberPlates, multipleScalped, sharpened, scrapWeight1, scrapWeight2, standardScrapFactor );
    }

    @Override
    public String toString()
    {
        return "ReportingOutputDetailDTO{" + "opHistId=" + opHistId + ", scheduleNbr='" + scheduleNbr + '\'' + ", lot='" + lot + '\'' + ", sublot='" + sublot + '\'' + ", invSuffix='" + invSuffix
                + '\'' + ", operationSeq=" + operationSeq + ", parentLot='" + parentLot + '\'' + ", parentSublot='" + parentSublot + '\'' + ", parentInvSuffix='" + parentInvSuffix + '\''
                + ", parentOperationSeq=" + parentOperationSeq + ", finishedDate=" + finishedDate + ", finishedTime=" + finishedTime + ", shift='" + shift + '\'' + ", exitArbor=" + exitArbor
                + ", thickness=" + thickness + ", thicknessIn=" + thicknessIn + ", width=" + width + ", length=" + length + ", weight=" + weight + ", weightIn=" + weightIn + ", buildup=" + buildup
                + ", cutId=" + cutId + ", dropId='" + dropId + '\'' + ", piecesOut=" + piecesOut + ", firstPass='" + firstPass + '\'' + ", lastPass='" + lastPass + '\'' + ", reworkReason='"
                + reworkReason + '\'' + ", finishingCc='" + finishingCc + '\'' + ", customerName='" + customerName + '\'' + ", customerNbr='" + customerNbr + '\'' + ", outputType='" + outputType
                + '\'' + ", numberPlates=" + numberPlates + ", multipleScalped=" + multipleScalped + ", sharpened='" + sharpened + '\'' + ", scrapWeight1=" + scrapWeight1 + ", scrapWeight2="
                + scrapWeight2 + ", standardScrapFactor=" + standardScrapFactor + '}';
    }
}
