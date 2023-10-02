package com.hydro.casting.server.contract.locking.workflow.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.util.Date;

public class LockHistoryElementDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private long lockRecId;

    private String materialStatus;
    private String text;
    private String lot;
    private String sublot;
    private String invSuffix;
    private Integer opSeq;
    private String kst;
    private String userId;
    private String defectTypeCat;
    private String defectTypeRea;
    private Date freeDate;
    private String name;
    private Date massnahmeTs;
    private String dropId;
    private Integer cutId;
    private Integer paletteId;

    @Override
    public long getId()
    {
        return lockRecId;
    }

    public long getLockRecId()
    {
        return lockRecId;
    }

    public void setLockRecId( long lockRecId )
    {
        this.lockRecId = lockRecId;
    }

    public String getMaterialStatus()
    {
        return materialStatus;
    }

    public void setMaterialStatus( String materialStatus )
    {
        this.materialStatus = materialStatus;
    }

    public String getText()
    {
        return text;
    }

    public void setText( String text )
    {
        this.text = text;
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

    public Integer getOpSeq()
    {
        return opSeq;
    }

    public void setOpSeq( Integer opSeq )
    {
        this.opSeq = opSeq;
    }

    public String getKst()
    {
        return kst;
    }

    public void setKst( String kst )
    {
        this.kst = kst;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId( String userId )
    {
        this.userId = userId;
    }

    public String getDefectTypeCat()
    {
        return defectTypeCat;
    }

    public void setDefectTypeCat( String defectTypeCat )
    {
        this.defectTypeCat = defectTypeCat;
    }

    public String getDefectTypeRea()
    {
        return defectTypeRea;
    }

    public void setDefectTypeRea( String defectTypeRea )
    {
        this.defectTypeRea = defectTypeRea;
    }

    public Date getFreeDate()
    {
        return freeDate;
    }

    public void setFreeDate( Date freeDate )
    {
        this.freeDate = freeDate;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public Date getMassnahmeTs()
    {
        return massnahmeTs;
    }

    public void setMassnahmeTs( Date massnahmeTs )
    {
        this.massnahmeTs = massnahmeTs;
    }

    public String getDropId()
    {
        return dropId;
    }

    public void setDropId( String dropId )
    {
        this.dropId = dropId;
    }

    public Integer getCutId()
    {
        return cutId;
    }

    public void setCutId( Integer cutId )
    {
        this.cutId = cutId;
    }

    public Integer getPaletteId()
    {
        return paletteId;
    }

    public void setPaletteId( Integer paletteId )
    {
        this.paletteId = paletteId;
    }

    public String getWorkStepId()
    {
        StringBuilder stb = new StringBuilder();
        if ( getLot() != null )
        {
            appendIfNotNull( stb, getLot().trim() );
        }
        appendIfNotNull( stb, getSublot() );
        appendIfNotNull( stb, getInvSuffix() );
        stb.append( " -" );
        appendIfNotNull( stb, getOpSeq() );
        return stb.toString();
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

}
