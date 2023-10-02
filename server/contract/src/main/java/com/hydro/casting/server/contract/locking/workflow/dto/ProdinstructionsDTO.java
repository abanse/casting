package com.hydro.casting.server.contract.locking.workflow.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

public class ProdinstructionsDTO implements ViewDTO, Cloneable
{
    private static final long serialVersionUID = 1L;

    public final static String LT_GROUP = "LT";
    public final static String QT_GROUP = "QT";
    public final static String RECK_GROUP = "RECK";

    private String kst;
    private String lot;
    private String sublot;
    private String invSuffix;
    private long opHistId;
    private String alloy;
    private String temper;
    private String qualityCode;
    private String customerName;
    private String customerNbr;
    private long textId;
    private String qrlText1;
    private String qrlText2;
    private String qrlText3;
    private String qrlText4;
    private String qrlText5;
    private String qrlText6;
    private String qrlText7;
    private String qrlText8;
    private String qrlText9;
    private String qrlText10;
    private String prodComment;
    private String addActionText;
    private String agfText;
    private Double outputGauge;
    private Double outputWidth;

    public String getKst()
    {
        return kst;
    }

    public void setKst( String kst )
    {
        this.kst = kst;
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

    public long getOpHistId()
    {
        return opHistId;
    }

    public void setOpHistId( long opHistId )
    {
        this.opHistId = opHistId;
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

    public long getTextId()
    {
        return textId;
    }

    public void setTextId( long textId )
    {
        this.textId = textId;
    }

    public String getQrlText1()
    {
        return qrlText1;
    }

    public void setQrlText1( String qrlText1 )
    {
        this.qrlText1 = qrlText1;
    }

    public String getQrlText2()
    {
        return qrlText2;
    }

    public void setQrlText2( String qrlText2 )
    {
        this.qrlText2 = qrlText2;
    }

    public String getQrlText3()
    {
        return qrlText3;
    }

    public void setQrlText3( String qrlText3 )
    {
        this.qrlText3 = qrlText3;
    }

    public String getQrlText4()
    {
        return qrlText4;
    }

    public void setQrlText4( String qrlText4 )
    {
        this.qrlText4 = qrlText4;
    }

    public String getQrlText5()
    {
        return qrlText5;
    }

    public void setQrlText5( String qrlText5 )
    {
        this.qrlText5 = qrlText5;
    }

    public String getQrlText6()
    {
        return qrlText6;
    }

    public void setQrlText6( String qrlText6 )
    {
        this.qrlText6 = qrlText6;
    }

    public String getQrlText7()
    {
        return qrlText7;
    }

    public void setQrlText7( String qrlText7 )
    {
        this.qrlText7 = qrlText7;
    }

    public String getQrlText8()
    {
        return qrlText8;
    }

    public void setQrlText8( String qrlText8 )
    {
        this.qrlText8 = qrlText8;
    }

    public String getQrlText9()
    {
        return qrlText9;
    }

    public void setQrlText9( String qrlText9 )
    {
        this.qrlText9 = qrlText9;
    }

    public String getQrlText10()
    {
        return qrlText10;
    }

    public void setQrlText10( String qrlText10 )
    {
        this.qrlText10 = qrlText10;
    }

    public String getProdComment()
    {
        return prodComment;
    }

    public void setProdComment( String prodComment )
    {
        this.prodComment = prodComment;
    }

    public String getAddActionText()
    {
        return addActionText;
    }

    public void setAddActionText( String addActionText )
    {
        this.addActionText = addActionText;
    }

    public String getAgfText()
    {
        return agfText;
    }

    public void setAgfText( String agfText )
    {
        this.agfText = agfText;
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

    @Override
    public long getId()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( addActionText == null ) ? 0 : addActionText.hashCode() );
        result = prime * result + ( ( agfText == null ) ? 0 : agfText.hashCode() );
        result = prime * result + ( ( alloy == null ) ? 0 : alloy.hashCode() );

        result = prime * result + ( ( customerName == null ) ? 0 : customerName.hashCode() );
        result = prime * result + ( ( customerNbr == null ) ? 0 : customerNbr.hashCode() );

        result = prime * result + ( ( invSuffix == null ) ? 0 : invSuffix.hashCode() );

        result = prime * result + ( ( kst == null ) ? 0 : kst.hashCode() );
        result = prime * result + ( ( lot == null ) ? 0 : lot.hashCode() );
        result = prime * result + (int) ( opHistId ^ ( opHistId >>> 32 ) );
        result = prime * result + ( ( prodComment == null ) ? 0 : prodComment.hashCode() );

        result = prime * result + ( ( qrlText1 == null ) ? 0 : qrlText1.hashCode() );
        result = prime * result + ( ( qrlText2 == null ) ? 0 : qrlText2.hashCode() );
        result = prime * result + ( ( qrlText3 == null ) ? 0 : qrlText3.hashCode() );
        result = prime * result + ( ( qrlText4 == null ) ? 0 : qrlText4.hashCode() );
        result = prime * result + ( ( qrlText5 == null ) ? 0 : qrlText5.hashCode() );
        result = prime * result + ( ( qrlText6 == null ) ? 0 : qrlText6.hashCode() );
        result = prime * result + ( ( qrlText7 == null ) ? 0 : qrlText7.hashCode() );
        result = prime * result + ( ( qrlText8 == null ) ? 0 : qrlText8.hashCode() );
        result = prime * result + ( ( qrlText9 == null ) ? 0 : qrlText9.hashCode() );
        result = prime * result + ( ( qrlText10 == null ) ? 0 : qrlText10.hashCode() );

        result = prime * result + ( ( qualityCode == null ) ? 0 : qualityCode.hashCode() );
        result = prime * result + ( ( sublot == null ) ? 0 : sublot.hashCode() );
        result = prime * result + ( ( temper == null ) ? 0 : temper.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        ProdinstructionsDTO other = (ProdinstructionsDTO) obj;
        if ( agfText == null )
        {
            if ( other.agfText != null )
                return false;
        }
        if ( addActionText == null )
        {
            if ( other.addActionText != null )
                return false;
        }
        if ( alloy == null )
        {
            if ( other.alloy != null )
                return false;
        }
        else if ( !alloy.equals( other.alloy ) )
            return false;

        if ( customerName == null )
        {
            if ( other.customerName != null )
                return false;
        }
        else if ( !customerName.equals( other.customerName ) )
            return false;

        if ( customerNbr == null )
        {
            if ( other.customerNbr != null )
                return false;
        }
        else if ( !customerNbr.equals( other.customerNbr ) )
            return false;
        if ( opHistId != other.opHistId )
            return false;
        if ( invSuffix == null )
        {
            if ( other.invSuffix != null )
                return false;
        }
        else if ( !invSuffix.equals( other.invSuffix ) )
            return false;
        if ( kst == null )
        {
            if ( other.kst != null )
                return false;
        }
        else if ( !kst.equals( other.kst ) )
            return false;

        if ( lot == null )
        {
            if ( other.lot != null )
                return false;
        }
        else if ( !lot.equals( other.lot ) )
            return false;

        if ( qualityCode == null )
        {
            if ( other.qualityCode != null )
                return false;
        }
        else if ( !qualityCode.equals( other.qualityCode ) )
            return false;

        if ( qrlText1 == null )
        {
            if ( other.qrlText1 != null )
                return false;
        }
        else if ( !qrlText1.equals( other.qrlText1 ) )
            return false;

        if ( qrlText2 == null )
        {
            if ( other.qrlText2 != null )
                return false;
        }
        else if ( !qrlText2.equals( other.qrlText2 ) )
            return false;

        if ( qrlText3 == null )
        {
            if ( other.qrlText3 != null )
                return false;
        }
        else if ( !qrlText3.equals( other.qrlText3 ) )
            return false;

        if ( qrlText4 == null )
        {
            if ( other.qrlText4 != null )
                return false;
        }
        else if ( !qrlText4.equals( other.qrlText4 ) )
            return false;

        if ( qrlText5 == null )
        {
            if ( other.qrlText5 != null )
                return false;
        }
        else if ( !qrlText5.equals( other.qrlText5 ) )
            return false;
        if ( qrlText6 == null )
        {
            if ( other.qrlText6 != null )
                return false;
        }
        else if ( !qrlText6.equals( other.qrlText6 ) )
            return false;

        if ( qrlText7 == null )
        {
            if ( other.qrlText7 != null )
                return false;
        }
        else if ( !qrlText7.equals( other.qrlText7 ) )
            return false;

        if ( qrlText8 == null )
        {
            if ( other.qrlText8 != null )
                return false;
        }
        else if ( !qrlText8.equals( other.qrlText8 ) )
            return false;
        if ( qrlText9 == null )
        {
            if ( other.qrlText9 != null )
                return false;
        }
        else if ( !qrlText9.equals( other.qrlText9 ) )
            return false;

        if ( qrlText10 == null )
        {
            if ( other.qrlText10 != null )
                return false;
        }
        else if ( !qrlText10.equals( other.qrlText10 ) )
            return false;

        if ( sublot == null )
        {
            if ( other.sublot != null )
                return false;
        }
        else if ( !sublot.equals( other.sublot ) )
            return false;

        if ( temper == null )
        {
            if ( other.temper != null )
                return false;
        }
        else if ( !temper.equals( other.temper ) )
            return false;

        return true;
    }

    // @Override
    // public String toString()
    // {
    // return "LockingWorkflowDTO [kst=" + kst + ", lot=" + lot + ", sublot=" + sublot + ", invSuffix=" +
    // invSuffix + ", alloy=" + alloy
    // + ", temper=" + temper + ", qualityCode=" + qualityCode
    // + ", customerName=" + customerName + ", customerNbr=" + customerNbr +", opHistId="+ opHistId + ",
    // endCostCenter=" + endCostCenter + ", scrapClass=" + scrapClass + ", childs=" + childs + "]";
    // }

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
