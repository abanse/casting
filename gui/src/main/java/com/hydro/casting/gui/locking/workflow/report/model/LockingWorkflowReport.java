package com.hydro.casting.gui.locking.workflow.report.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@XmlRootElement
public class LockingWorkflowReport
{
    public String title;

    public String owner;
    public String kst;
    public String lot;
    public String sublot;
    public String invSuffix;
    public String scheduleNbr;
    public String alloy;
    public String temper;
    public String qualityCode;
    public Double gaugeOut;
    public Double widthOut;
    public Double lengthOut;
    public String defectTypeCat;
    public String defectTypeLoc;
    public String defectTypeRea;
    public String scrapCodeDescription;
    public String scrapAreaCodeDescription;
    public String customerName;
    public String customerOrderNr;
    public String orderDescription;
    public Double finishGauge;
    public Double orderedWidth;
    public String materialNo;
    public String nextCostCenter;
    public Date lockDate;
    public String castDropNo;
    public String castHouseNo;
    public Integer yearCastDrop;
    public String castSampleNbr;
    public String dropId;
    public Integer cutId;
    public Integer paletteId;
    public Date freeDate;
    public Integer opSeq;
    public String userId;
    public String materialStatus;
    public LocalDateTime prodStartTs;
    public LocalDateTime prodEndTs;
    public LocalDateTime avStartTs;
    public LocalDateTime avEndTs;
    public LocalDateTime tcsStartTs;
    public LocalDateTime tcsEndTs;
    public long lockRecId;
    public String opMessage;
    public Integer scheduledOrder;
    public String operationText;
    public String cbuCode;
    public String kdServiceName;
    public String kdServiceTel;
    public String code;
    public String ocDescription;
    public Long weightOut;
    public Double outputGauge;
    public Double outputWidth;
    public Double outputLength;
    public String partNrCustomer;
    public String purchaseOrderNr;
    public Double buildup;
    public Integer buildupMin;
    public Integer buildupMax;
    public Long weight;
    public Long pdWeight;
    public Integer exitArbor;
    public String spool;
    public Integer delWeekDemanded;
    public Integer delYearDemanded;
    public long opHistId;
    public String endCostCenter;
    public String scrapClass;
    public String footer1;
    public String footer2;
    

    @XmlElementWrapper( name = "lockComments" )
    @XmlElement( name = "lockComment" )
    public List<LockComment> lockComments = new ArrayList<>();

    public void addLockComment( Date timestamp, String user, String comment )
    {
        LockComment lockComment = new LockComment();
        lockComment.timestamp = timestamp;
        lockComment.user = user;
        lockComment.comment = comment;
        lockComments.add( lockComment );
    }

    public static class LockComment
    {
        public Date timestamp;
        public String user;
        public String comment;
    }

}
