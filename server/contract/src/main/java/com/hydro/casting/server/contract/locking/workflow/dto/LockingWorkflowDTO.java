package com.hydro.casting.server.contract.locking.workflow.dto;

import com.hydro.casting.common.Casting;
import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@XmlRootElement
public class LockingWorkflowDTO implements ViewDTO, Cloneable
{
    private static final long serialVersionUID = 1L;

    public final static String OWNER_PROD = "PROD";
    public final static String OWNER_AV = "AV";
    public final static String OWNER_TCS = "TCS";
    public final static String OWNER_SAP = "SAP";
    public final static String OWNER_QS = "QS";
    public final static String OWNER_DATA_ERROR = "DATA_ERROR";

    // Achtung dieses Feld darf nicht im HashCode oder equals aufgenommen werden
    private long replicTime;

    private String owner;
    private String kst;
    private String material;
    private String scheduleNbr;
    private String alloy;
    private String temper;
    private String qualityCode;
    private Double gaugeOut;
    private Double widthOut;
    private Double lengthOut;
    private String defectTypeCat;
    private String defectTypeLoc;
    private String defectTypeRea;
    private String scrapCodeDescription;
    private String scrapAreaCodeDescription;
    private String customerName;
    private String customerOrderNr;
    private String orderDescription;
    private Double finishGauge;
    private Double orderedWidth;
    private String materialNo;
    private String nextCostCenter;
    private String openOperations;
    private LocalDateTime lockDate;
    private String castDropNo;
    private String castHouseNo;
    private Integer yearCastDrop;
    private String castSampleNbr;
    private LocalDateTime freeDate;
    private String userId;
    private String materialStatus;
    private LocalDateTime prodStartTs;
    private LocalDateTime prodEndTs;
    private LocalDateTime qsStartTs;
    private LocalDateTime qsEndTs;
    private LocalDateTime avStartTs;
    private LocalDateTime avEndTs;
    private LocalDateTime tcsStartTs;
    private LocalDateTime tcsEndTs;
    private long lockRecId;
    private String lockComment;
    private String opMessage;
    private Integer scheduledOrder;
    private String operationText;
    private String cbuCode;
    private String kdServiceName;
    private String kdServiceTel;
    private String code;
    private String ocDescription;
    private Double weightOut;
    private Double outputGauge;
    private Double outputWidth;
    private Double outputLength;
    private String partNrCustomer;
    private String purchaseOrderNr;
    private Double buildup;
    private Integer buildupMin;
    private Integer buildupMax;
    private Double weight;
    private Double pdWeight;
    private Integer exitArbor;
    private String spool;
    private Integer delWeekDemanded;
    private Integer delYearDemanded;
    private long opHistId;
    private String endCostCenter;
    private String scrapClass;
    private boolean strip;
    private int packing;
    private long finishedGoodsId;
    private String prodOrderNo;

    private List<LockingWorkflowDTO> childs;
    private transient LockingWorkflowDTO parent;

    public LockingWorkflowDTO getParent()
    {
        return parent;
    }

    public void setParent( LockingWorkflowDTO parent )
    {
        this.parent = parent;
    }

    @Override
    public long getId()
    {
        return lockRecId;
    }

    public long getReplicTime()
    {
        return replicTime;
    }

    public void setReplicTime( long replicTime )
    {
        this.replicTime = replicTime;
    }

    public String getKst()
    {
        return kst;
    }

    public void setKst( String kst )
    {
        this.kst = kst;
    }

    public String getMaterial()
    {
        return material;
    }

    public void setMaterial( String material )
    {
        this.material = material;
    }

    public String getScheduleNbr()
    {
        return scheduleNbr;
    }

    public void setScheduleNbr( String scheduleNbr )
    {
        this.scheduleNbr = scheduleNbr;
    }

    public String getAlloy()
    {
        return alloy;
    }

    public void setAlloy( String alloy )
    {
        this.alloy = alloy;
    }

    public String getTemper()
    {
        return temper;
    }

    public void setTemper( String temper )
    {
        this.temper = temper;
    }

    public String getQualityCode()
    {
        return qualityCode;
    }

    public void setQualityCode( String qualityCode )
    {
        this.qualityCode = qualityCode;
    }

    public Double getGaugeOut()
    {
        return gaugeOut;
    }

    public void setGaugeOut( Double gaugeOut )
    {
        this.gaugeOut = gaugeOut;
    }

    public Double getWidthOut()
    {
        return widthOut;
    }

    public void setWidthOut( Double widthOut )
    {
        this.widthOut = widthOut;
    }

    public Double getLengthOut()
    {
        return lengthOut;
    }

    public void setLengthOut( Double lengthOut )
    {
        this.lengthOut = lengthOut;
    }

    public String getDefectTypeCat()
    {
        return defectTypeCat;
    }

    public void setDefectTypeCat( String defectTypeCat )
    {
        this.defectTypeCat = defectTypeCat;
    }

    public String getDefectTypeLoc()
    {
        return defectTypeLoc;
    }

    public void setDefectTypeLoc( String defectTypeLoc )
    {
        this.defectTypeLoc = defectTypeLoc;
    }

    public String getDefectTypeRea()
    {
        return defectTypeRea;
    }

    public void setDefectTypeRea( String defectTypeRea )
    {
        this.defectTypeRea = defectTypeRea;
    }

    public String getScrapCodeDescription()
    {
        return scrapCodeDescription;
    }

    public void setScrapCodeDescription( String scrapCodeDescription )
    {
        this.scrapCodeDescription = scrapCodeDescription;
    }

    public String getScrapAreaCodeDescription()
    {
        return scrapAreaCodeDescription;
    }

    public void setScrapAreaCodeDescription( String scrapAreaCodeDescription )
    {
        this.scrapAreaCodeDescription = scrapAreaCodeDescription;
    }

    public String getCustomerName()
    {
        return customerName;
    }

    public void setCustomerName( String customerName )
    {
        this.customerName = customerName;
    }

    public String getCustomerOrderNr()
    {
        return customerOrderNr;
    }

    public void setCustomerOrderNr( String customerOrderNr )
    {
        this.customerOrderNr = customerOrderNr;
    }

    public String getOrderDescription()
    {
        return orderDescription;
    }

    public void setOrderDescription( String orderDescription )
    {
        this.orderDescription = orderDescription;
    }

    public Double getFinishGauge()
    {
        return finishGauge;
    }

    public void setFinishGauge( Double finishGauge )
    {
        this.finishGauge = finishGauge;
    }

    public Double getOrderedWidth()
    {
        return orderedWidth;
    }

    public void setOrderedWidth( Double orderedWidth )
    {
        this.orderedWidth = orderedWidth;
    }

    public String getMaterialNo()
    {
        return materialNo;
    }

    public void setMaterialNo( String materialNo )
    {
        this.materialNo = materialNo;
    }

    public String getNextCostCenter()
    {
        return nextCostCenter;
    }

    public void setNextCostCenter( String nextCostCenter )
    {
        this.nextCostCenter = nextCostCenter;
    }

    public String getOpenOperations()
    {
        return openOperations;
    }

    public void setOpenOperations( String openOperations )
    {
        this.openOperations = openOperations;
    }

    public LocalDateTime getLockDate()
    {
        return lockDate;
    }

    public void setLockDate( LocalDateTime lockDate )
    {
        this.lockDate = lockDate;
    }

    public String getCastDropNo()
    {
        return castDropNo;
    }

    public void setCastDropNo( String castDropNo )
    {
        this.castDropNo = castDropNo;
    }

    public String getCastHouseNo()
    {
        return castHouseNo;
    }

    public void setCastHouseNo( String castHouseNo )
    {
        this.castHouseNo = castHouseNo;
    }

    public Integer getYearCastDrop()
    {
        return yearCastDrop;
    }

    public void setYearCastDrop( Integer yearCastDrop )
    {
        this.yearCastDrop = yearCastDrop;
    }

    public String getCastSampleNbr()
    {
        return castSampleNbr;
    }

    public void setCastSampleNbr( String castSampleNbr )
    {
        this.castSampleNbr = castSampleNbr;
    }

    public LocalDateTime getFreeDate()
    {
        return freeDate;
    }

    public void setFreeDate( LocalDateTime freeDate )
    {
        this.freeDate = freeDate;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId( String userId )
    {
        this.userId = userId;
    }

    public String getMaterialStatus()
    {
        return materialStatus;
    }

    public void setMaterialStatus( String materialStatus )
    {
        this.materialStatus = materialStatus;
    }

    public LocalDateTime getProdStartTs()
    {
        return prodStartTs;
    }

    public LocalDateTime getQsStartTs()
    {
        return qsStartTs;
    }

    public void setQsStartTs( LocalDateTime qsStartTs )
    {
        this.qsStartTs = qsStartTs;
    }

    public LocalDateTime getQsEndTs()
    {
        return qsEndTs;
    }

    public void setQsEndTs( LocalDateTime qsEndTs )
    {
        this.qsEndTs = qsEndTs;
    }

    public void setProdStartTs( LocalDateTime prodStartTs )
    {
        this.prodStartTs = prodStartTs;
    }

    public LocalDateTime getProdEndTs()
    {
        return prodEndTs;
    }

    public void setProdEndTs( LocalDateTime prodEndTs )
    {
        this.prodEndTs = prodEndTs;
    }

    public LocalDateTime getAvStartTs()
    {
        return avStartTs;
    }

    public void setAvStartTs( LocalDateTime avStartTs )
    {
        this.avStartTs = avStartTs;
    }

    public LocalDateTime getAvEndTs()
    {
        return avEndTs;
    }

    public void setAvEndTs( LocalDateTime avEndTs )
    {
        this.avEndTs = avEndTs;
    }

    public LocalDateTime getTcsStartTs()
    {
        return tcsStartTs;
    }

    public void setTcsStartTs( LocalDateTime tcsStartTs )
    {
        this.tcsStartTs = tcsStartTs;
    }

    public LocalDateTime getTcsEndTs()
    {
        return tcsEndTs;
    }

    public void setTcsEndTs( LocalDateTime tcsEndTs )
    {
        this.tcsEndTs = tcsEndTs;
    }

    public long getLockRecId()
    {
        return lockRecId;
    }

    public void setLockRecId( long lockRecId )
    {
        this.lockRecId = lockRecId;
    }

    public String getLockComment()
    {
        return lockComment;
    }

    public void setLockComment( String lockComment )
    {
        this.lockComment = lockComment;
    }

    public String getOpMessage()
    {
        return opMessage;
    }

    public void setOpMessage( String opMessage )
    {
        this.opMessage = opMessage;
    }

    public Integer getScheduledOrder()
    {
        return scheduledOrder;
    }

    public void setScheduledOrder( Integer scheduledOrder )
    {
        this.scheduledOrder = scheduledOrder;
    }

    public String getOperationText()
    {
        return operationText;
    }

    public void setOperationText( String operationText )
    {
        this.operationText = operationText;
    }

    public String getCbuCode()
    {
        return cbuCode;
    }

    public void setCbuCode( String cbuCode )
    {
        this.cbuCode = cbuCode;
    }

    public String getKdServiceName()
    {
        return kdServiceName;
    }

    public void setKdServiceName( String kdServiceName )
    {
        this.kdServiceName = kdServiceName;
    }

    public String getKdServiceTel()
    {
        return kdServiceTel;
    }

    public void setKdServiceTel( String kdServiceTel )
    {
        this.kdServiceTel = kdServiceTel;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode( String code )
    {
        this.code = code;
    }

    public String getOcDescription()
    {
        return ocDescription;
    }

    public void setOcDescription( String ocDescription )
    {
        this.ocDescription = ocDescription;
    }

    public Double getWeightOut()
    {
        return weightOut;
    }

    public void setWeightOut( Double weightOut )
    {
        this.weightOut = weightOut;
    }

    public Double getOutputGauge()
    {
        return outputGauge;
    }

    public void setOutputGauge( Double outputGauge )
    {
        this.outputGauge = outputGauge;
    }

    public Double getOutputWidth()
    {
        return outputWidth;
    }

    public void setOutputWidth( Double outputWidth )
    {
        this.outputWidth = outputWidth;
    }

    public Double getOutputLength()
    {
        return outputLength;
    }

    public void setOutputLength( Double outputLength )
    {
        this.outputLength = outputLength;
    }

    public String getPartNrCustomer()
    {
        return partNrCustomer;
    }

    public void setPartNrCustomer( String partNrCustomer )
    {
        this.partNrCustomer = partNrCustomer;
    }

    public String getPurchaseOrderNr()
    {
        return purchaseOrderNr;
    }

    public void setPurchaseOrderNr( String purchaseOrderNr )
    {
        this.purchaseOrderNr = purchaseOrderNr;
    }

    public Double getBuildup()
    {
        return buildup;
    }

    public void setBuildup( Double buildup )
    {
        this.buildup = buildup;
    }

    public Integer getBuildupMin()
    {
        return buildupMin;
    }

    public void setBuildupMin( Integer buildupMin )
    {
        this.buildupMin = buildupMin;
    }

    public Integer getBuildupMax()
    {
        return buildupMax;
    }

    public void setBuildupMax( Integer buildupMax )
    {
        this.buildupMax = buildupMax;
    }

    public Double getWeight()
    {
        return weight;
    }

    public void setWeight( Double weight )
    {
        this.weight = weight;
    }

    public Double getPdWeight()
    {
        return pdWeight;
    }

    public void setPdWeight( Double pdWeight )
    {
        this.pdWeight = pdWeight;
    }

    public Integer getExitArbor()
    {
        return exitArbor;
    }

    public void setExitArbor( Integer exitArbor )
    {
        this.exitArbor = exitArbor;
    }

    public String getSpool()
    {
        return spool;
    }

    public void setSpool( String spool )
    {
        this.spool = spool;
    }

    public Integer getDelWeekDemanded()
    {
        return delWeekDemanded;
    }

    public void setDelWeekDemanded( Integer delWeekDemanded )
    {
        this.delWeekDemanded = delWeekDemanded;
    }

    public Integer getDelYearDemanded()
    {
        return delYearDemanded;
    }

    public void setDelYearDemanded( Integer delYearDemanded )
    {
        this.delYearDemanded = delYearDemanded;
    }

    public long getOpHistId()
    {
        return opHistId;
    }

    public void setOpHistId( long opHistId )
    {
        this.opHistId = opHistId;
    }

    public String getOwner()
    {
        return owner;
    }

    public void setOwner( String owner )
    {
        this.owner = owner;
    }

    public long getFinishedGoodsId()
    {
        return finishedGoodsId;
    }

    public void setFinishedGoodsId( long finishedGoodsId )
    {
        this.finishedGoodsId = finishedGoodsId;
    }

    public Double getOuterDiameter()
    {
        if ( getExitArbor() == null || getBuildup() == null )
        {
            return null;
        }
        return new Double( ( getBuildup() * 2.0 ) + getExitArbor() );
    }

    public void setOuterDiameter( Double outerDiameter )
    {
        // do nothing;
    }

    public String getWorkStepId()
    {
        return material;
    }

    public String getCastingId()
    {
        StringBuilder stb = new StringBuilder();
        if ( getCastHouseNo() != null )
        {
            stb.append( getCastHouseNo() );
        }
        if ( getCastDropNo() != null )
        {
            stb.append( getCastDropNo() );
        }
        appendIfNotNull( stb, getYearCastDrop() );
        return stb.toString();
    }

    public void setCastingId( String castingId )
    {
        // do nothing
    }

    public String getDelivery()
    {
        if ( getDelWeekDemanded() == null && getDelYearDemanded() == null )
        {
            return null;
        }
        StringBuilder stb = new StringBuilder();
        if ( getDelWeekDemanded() != null )
        {
            stb.append( getDelWeekDemanded() );
        }
        stb.append( "/" );
        if ( getDelYearDemanded() != null )
        {
            stb.append( getDelYearDemanded() );
        }
        return stb.toString();
    }

    public void setDelivery( String delivery )
    {
        // do nothing;
    }

    public String getEndCostCenter()
    {
        return endCostCenter;
    }

    public void setEndCostCenter( String endCostCenter )
    {
        this.endCostCenter = endCostCenter;
    }

    public String getScrapClass()
    {
        return scrapClass;
    }

    public void setScrapClass( String scrapClass )
    {
        this.scrapClass = scrapClass;
    }

    public List<LockingWorkflowDTO> getChilds()
    {
        return childs;
    }

    public void setChilds( List<LockingWorkflowDTO> childs )
    {
        this.childs = childs;
    }

    public int getPacking()
    {
        return packing;
    }

    public void setPacking( int packing )
    {
        this.packing = packing;
    }

    public String getProdOrderNo()
    {
        return prodOrderNo;
    }

    public void setProdOrderNo( String prodOrderNo )
    {
        this.prodOrderNo = prodOrderNo;
    }

    private void appendIfNotNull( StringBuilder stb, Number value )
    {
        if ( value == null )
        {
            return;
        }
        appendIfNotNull( stb, value.toString() );
    }

    private void appendIfNotNull( StringBuilder stb, String value )
    {
        if ( value == null )
        {
            return;
        }
        if ( stb.length() > 0 )
        {
            stb.append( " " );
        }
        stb.append( value );
    }

    //    public boolean createStrip()
    //    {
    //        this.strip = ( this.cutId != null && cutId != 0 ) || ( this.paletteId != null && this.paletteId != 0 ) || ( this.dropId != null && this.dropId.trim().length() > 0 );
    //        return this.strip;
    //    }
    //
    //    public String createStripName()
    //    {
    //        this.strip = createStrip();
    //        String name = "";
    //        if ( !strip )
    //        {
    //            return lot;
    //        }
    //        if ( invSuffix != null && invSuffix.length() > 0 )
    //        {
    //            name = invSuffix.trim();
    //        }
    //        if ( paletteId == null || paletteId == 0 )
    //        {
    //            if ( dropId != null && dropId.length() > 0 )
    //            {
    //                name += dropId.trim();
    //            }
    //            if ( cutId != null && cutId != 0 )
    //            {
    //                name += Integer.toString( cutId );
    //            }
    //        }
    //        else
    //        {
    //            name += Integer.toString( paletteId );
    //        }
    //        return name;
    //    }

    public String createMailFileName()
    {
        String mailFileName = getMaterial().trim();
        return mailFileName;
    }

    public boolean checkData()
    {
        if ( materialStatus.length() == 1 && materialStatus.startsWith( Casting.LOCKING_WORKFLOW.FREE_MARK ) )
        {
            return freeDate != null;
        }
        return true;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        LockingWorkflowDTO that = (LockingWorkflowDTO) o;
        return lockRecId == that.lockRecId && opHistId == that.opHistId && strip == that.strip && packing == that.packing && finishedGoodsId == that.finishedGoodsId && Objects.equals( owner,
                that.owner ) && Objects.equals( kst, that.kst ) && Objects.equals( material, that.material ) && Objects.equals( scheduleNbr, that.scheduleNbr ) && Objects.equals( alloy, that.alloy )
                && Objects.equals( temper, that.temper ) && Objects.equals( qualityCode, that.qualityCode ) && Objects.equals( gaugeOut, that.gaugeOut ) && Objects.equals( widthOut, that.widthOut )
                && Objects.equals( lengthOut, that.lengthOut ) && Objects.equals( defectTypeCat, that.defectTypeCat ) && Objects.equals( defectTypeLoc, that.defectTypeLoc ) && Objects.equals(
                defectTypeRea, that.defectTypeRea ) && Objects.equals( scrapCodeDescription, that.scrapCodeDescription ) && Objects.equals( scrapAreaCodeDescription, that.scrapAreaCodeDescription )
                && Objects.equals( customerName, that.customerName ) && Objects.equals( customerOrderNr, that.customerOrderNr ) && Objects.equals( orderDescription, that.orderDescription )
                && Objects.equals( finishGauge, that.finishGauge ) && Objects.equals( orderedWidth, that.orderedWidth ) && Objects.equals( materialNo, that.materialNo ) && Objects.equals(
                nextCostCenter, that.nextCostCenter ) && Objects.equals( openOperations, that.openOperations ) && Objects.equals( lockDate, that.lockDate ) && Objects.equals( castDropNo,
                that.castDropNo ) && Objects.equals( castHouseNo, that.castHouseNo ) && Objects.equals( yearCastDrop, that.yearCastDrop ) && Objects.equals( castSampleNbr, that.castSampleNbr )
                && Objects.equals( freeDate, that.freeDate ) && Objects.equals( userId, that.userId ) && Objects.equals( materialStatus, that.materialStatus ) && Objects.equals( prodStartTs,
                that.prodStartTs ) && Objects.equals( prodEndTs, that.prodEndTs ) && Objects.equals( qsStartTs, that.qsStartTs ) && Objects.equals( qsEndTs, that.qsEndTs ) && Objects.equals(
                avStartTs, that.avStartTs ) && Objects.equals( avEndTs, that.avEndTs ) && Objects.equals( tcsStartTs, that.tcsStartTs ) && Objects.equals( tcsEndTs, that.tcsEndTs ) && Objects.equals(
                lockComment, that.lockComment ) && Objects.equals( opMessage, that.opMessage ) && Objects.equals( scheduledOrder, that.scheduledOrder ) && Objects.equals( operationText,
                that.operationText ) && Objects.equals( cbuCode, that.cbuCode ) && Objects.equals( kdServiceName, that.kdServiceName ) && Objects.equals( kdServiceTel, that.kdServiceTel )
                && Objects.equals( code, that.code ) && Objects.equals( ocDescription, that.ocDescription ) && Objects.equals( weightOut, that.weightOut ) && Objects.equals( outputGauge,
                that.outputGauge ) && Objects.equals( outputWidth, that.outputWidth ) && Objects.equals( outputLength, that.outputLength ) && Objects.equals( partNrCustomer, that.partNrCustomer )
                && Objects.equals( purchaseOrderNr, that.purchaseOrderNr ) && Objects.equals( buildup, that.buildup ) && Objects.equals( buildupMin, that.buildupMin ) && Objects.equals( buildupMax,
                that.buildupMax ) && Objects.equals( weight, that.weight ) && Objects.equals( pdWeight, that.pdWeight ) && Objects.equals( exitArbor, that.exitArbor ) && Objects.equals( spool,
                that.spool ) && Objects.equals( delWeekDemanded, that.delWeekDemanded ) && Objects.equals( delYearDemanded, that.delYearDemanded ) && Objects.equals( endCostCenter,
                that.endCostCenter ) && Objects.equals( scrapClass, that.scrapClass ) && Objects.equals( prodOrderNo, that.prodOrderNo ) && Objects.equals( childs, that.childs );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( owner, kst, material, scheduleNbr, alloy, temper, qualityCode, gaugeOut, widthOut, lengthOut, defectTypeCat, defectTypeLoc, defectTypeRea, scrapCodeDescription,
                scrapAreaCodeDescription, customerName, customerOrderNr, orderDescription, finishGauge, orderedWidth, materialNo, nextCostCenter, openOperations, lockDate, castDropNo, castHouseNo,
                yearCastDrop, castSampleNbr, freeDate, userId, materialStatus, prodStartTs, prodEndTs, qsStartTs, qsEndTs, avStartTs, avEndTs, tcsStartTs, tcsEndTs, lockRecId, lockComment, opMessage,
                scheduledOrder, operationText, cbuCode, kdServiceName, kdServiceTel, code, ocDescription, weightOut, outputGauge, outputWidth, outputLength, partNrCustomer, purchaseOrderNr, buildup,
                buildupMin, buildupMax, weight, pdWeight, exitArbor, spool, delWeekDemanded, delYearDemanded, opHistId, endCostCenter, scrapClass, strip, packing, finishedGoodsId, prodOrderNo,
                childs );
    }

    @Override
    public String toString()
    {
        return "LockingWorkflowDTO{" + "replicTime=" + replicTime + ", owner='" + owner + '\'' + ", kst='" + kst + '\'' + ", material='" + material + '\'' + ", scheduleNbr='" + scheduleNbr + '\'' + ", alloy='"
                + alloy + '\'' + ", temper='" + temper + '\'' + ", qualityCode='" + qualityCode + '\'' + ", gaugeOut=" + gaugeOut + ", widthOut=" + widthOut + ", lengthOut=" + lengthOut
                + ", defectTypeCat='" + defectTypeCat + '\'' + ", defectTypeLoc='" + defectTypeLoc + '\'' + ", defectTypeRea='" + defectTypeRea + '\'' + ", scrapCodeDescription='"
                + scrapCodeDescription + '\'' + ", scrapAreaCodeDescription='" + scrapAreaCodeDescription + '\'' + ", customerName='" + customerName + '\'' + ", customerOrderNr='" + customerOrderNr
                + '\'' + ", orderDescription='" + orderDescription + '\'' + ", finishGauge=" + finishGauge + ", orderedWidth=" + orderedWidth + ", materialNo='" + materialNo + '\''
                + ", nextCostCenter='" + nextCostCenter + '\'' + ", openOperations='" + openOperations + '\'' + ", lockDate=" + lockDate + ", castDropNo='" + castDropNo + '\'' + ", castHouseNo='"
                + castHouseNo + '\'' + ", yearCastDrop=" + yearCastDrop + ", castSampleNbr='" + castSampleNbr + '\'' + ", freeDate=" + freeDate + ", userId='" + userId + '\'' + ", materialStatus='"
                + materialStatus + '\'' + ", prodStartTs=" + prodStartTs + ", prodEndTs=" + prodEndTs + ", qsStartTs=" + qsStartTs + ", qsEndTs=" + qsEndTs + ", avStartTs=" + avStartTs + ", avEndTs="
                + avEndTs + ", tcsStartTs=" + tcsStartTs + ", tcsEndTs=" + tcsEndTs + ", lockRecId=" + lockRecId + ", lockComment='" + lockComment + '\'' + ", opMessage='" + opMessage + '\''
                + ", scheduledOrder=" + scheduledOrder + ", operationText='" + operationText + '\'' + ", cbuCode='" + cbuCode + '\'' + ", kdServiceName='" + kdServiceName + '\'' + ", kdServiceTel='"
                + kdServiceTel + '\'' + ", code='" + code + '\'' + ", ocDescription='" + ocDescription + '\'' + ", weightOut=" + weightOut + ", outputGauge=" + outputGauge + ", outputWidth="
                + outputWidth + ", outputLength=" + outputLength + ", partNrCustomer='" + partNrCustomer + '\'' + ", purchaseOrderNr='" + purchaseOrderNr + '\'' + ", buildup=" + buildup
                + ", buildupMin=" + buildupMin + ", buildupMax=" + buildupMax + ", weight=" + weight + ", pdWeight=" + pdWeight + ", exitArbor=" + exitArbor + ", spool='" + spool + '\''
                + ", delWeekDemanded=" + delWeekDemanded + ", delYearDemanded=" + delYearDemanded + ", opHistId=" + opHistId + ", endCostCenter='" + endCostCenter + '\'' + ", scrapClass='"
                + scrapClass + '\'' + ", strip=" + strip + ", packing=" + packing + ", finishedGoodsId=" + finishedGoodsId + ", prodOrderNo='" + prodOrderNo + '\'' + ", childs=" + childs + '}';
    }

    public Object clone()
    {
        try
        {
            return super.clone();
        }
        catch ( CloneNotSupportedException e )
        {
            e.printStackTrace();
        }
        return null;
    }
}