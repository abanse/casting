package com.hydro.casting.server.contract.locking.workflow.dto;

import java.io.Serializable;

public class LWStripHandleEntryDTO implements Serializable
{
    private static final long serialVersionUID = 1L;

    private LockingWorkflowDTO dto;
    private boolean enabled;
    private String invSuffix;
    private Integer paletteId;
    private Integer cutId;
    private String dropId;
    private String defectTypeCat;
    private String pack;
    private boolean strip;
    private String stripNameShort;
    private String stripNameFull;
    private String stripNameFullPrint;
    private String delim1 = " ";
    private String delim2 = " ";
    private String stripLabel;
    private transient boolean disabled = false;

    public LWStripHandleEntryDTO( LockingWorkflowDTO dto, boolean enabled, String pack, String titleColumn1, String titleColumn2 )
    {
        super();
        this.dto = dto;
        this.enabled = enabled;
        this.pack = pack;
        this.stripNameShort = "";

        for ( int i = 0; i < titleColumn1.length(); i++ )
        {
            delim1 += " ";
        }

        for ( int i = 0; i < titleColumn2.length() + 2; i++ )
        {
            delim2 += " ";
        }

        if ( dto != null )
        {
//            this.invSuffix = dto.getInvSuffix() == null ? " " : dto.getInvSuffix();
            //            this.paletteId = dto.getPaletteId() == null ? 0 : dto.getPaletteId();
            //            this.cutId = dto.getCutId() == null ? 0 : dto.getCutId();
            //            this.dropId = dto.getDropId() == null ? " " : dto.getDropId();
            this.defectTypeCat = dto.getDefectTypeCat() == null ? " " : dto.getDefectTypeCat();
            this.stripNameShort = createStripNameShort();
            this.stripNameFull = createStripNameFull( this.stripNameShort );
            this.stripNameFullPrint = createStripNamePrint( this.stripNameFull );
            this.stripLabel = this.stripNameFullPrint + delim2 + delim2 + pack;
        }
    }

    public LWStripHandleEntryDTO( boolean enabled, String titleCol1, String titleCol2, String titleCol3 )
    {
        super();
        this.dto = null;
        this.enabled = enabled;
        this.pack = " ";// something
        for ( int i = 0; i < titleCol1.length(); i++ )
        {
            delim1 += " ";
        }
        for ( int i = 0; i < titleCol2.length() + 2; i++ )
        {
            delim2 += " ";
        }
        this.stripNameShort = titleCol1 + delim1 + titleCol2;
        this.stripNameFull = this.stripNameShort;
        this.stripNameFullPrint = this.stripNameShort;
        this.stripLabel = this.stripNameFullPrint + delim2 + titleCol3;
    }

    public String createStripNameShort()
    {
        String nameShort = "";
        if ( this.invSuffix != null && this.invSuffix.length() > 0 )
        {
            nameShort = this.invSuffix.trim();
        }
        if ( this.paletteId == null || this.paletteId == 0 )
        {
            if ( this.dropId != null && this.dropId.length() > 0 )
            {
                nameShort = nameShort + this.dropId.trim();
            }
            if ( this.cutId != null && this.cutId != 0 )
            {
                if ( this.cutId <= 9 && this.cutId >= 1 )
                {
                    nameShort += "0";
                }
                nameShort += Integer.toString( this.cutId );
            }
        }
        else
        {
            if ( this.paletteId <= 9 && this.paletteId >= 1 )
            {
                nameShort += "0";
            }
            nameShort += Integer.toString( this.paletteId );
        }
        return nameShort;
    }

    public String createStripNameFull( String nameShort )
    {
        String nameFull;
        if ( nameShort != null )
        {
            nameFull = nameShort.trim() + delim1 + delim1 + this.defectTypeCat;
        }
        else
        {
            nameFull = delim1 + this.defectTypeCat;
        }
        return nameFull;
    }

    public String createStripNamePrint( String name )
    {
        if ( name.charAt( 0 ) == '0' )
        {
            return name.substring( 1 );
        }
        else
        {
            return cut0( name );
        }
    }

    public static String cut0( String parenStripNameParam )
    {
        String ret = parenStripNameParam;
        if ( parenStripNameParam == null )
        {
            return "";
        }
        int n = parenStripNameParam.indexOf( "0" );
        if ( n != -1 && n != 0 && n < parenStripNameParam.length() - 1 )
        {
            char prevChar = parenStripNameParam.charAt( n - 1 );
            char nextChar = parenStripNameParam.charAt( n + 1 );
            if ( ( ( prevChar >= 'A' && prevChar <= 'Z' ) || ( prevChar >= 'a' && prevChar <= 'z' ) ) && ( nextChar >= '0' && nextChar <= '9' ) )
            {
                ret = parenStripNameParam.substring( 0, n ) + parenStripNameParam.substring( n + 1 );
                return ret;
            }
        }
        return parenStripNameParam;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled( boolean enabled )
    {
        this.enabled = enabled;
    }

    public static long getSerialversionuid()
    {
        return serialVersionUID;
    }

    public String getStripNameFull()
    {
        return stripNameFull;
    }

    public String getStripNameShort()
    {
        return stripNameShort;
    }

    public String getInvSuffix()
    {
        return invSuffix;
    }

    public void setInvSuffix( String invSuffix )
    {
        this.invSuffix = invSuffix;
    }

    public Integer getPaletteId()
    {
        return paletteId;
    }

    public void setPaletteId( Integer paletteId )
    {
        this.paletteId = paletteId;
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

    public String getDefectTypeCat()
    {
        return defectTypeCat;
    }

    public void setDefectTypeCat( String defectTypeCat )
    {
        this.defectTypeCat = defectTypeCat;
    }

    public boolean isStrip()
    {
        return strip;
    }

    public void setStrip( boolean strip )
    {
        this.strip = strip;
    }

    public String getPack()
    {
        return pack;
    }

    public void setPack( String pack )
    {
        this.pack = pack;
    }

    public LockingWorkflowDTO getDto()
    {
        return dto;
    }

    public void setDto( LockingWorkflowDTO dto )
    {
        this.dto = dto;
    }

    public String getDelim1()
    {
        return delim1;
    }

    public String getDelim2()
    {
        return delim2;
    }

    public String getStripLabel()
    {
        return stripLabel;
    }

    public void setStripNameShort( String stripNameShort )
    {
        this.stripNameShort = stripNameShort;
    }

    public void setStripNameFull( String stripNameFull )
    {
        this.stripNameFull = stripNameFull;
    }

    public void setDelim1( String delim1 )
    {
        this.delim1 = delim1;
    }

    public void setDelim2( String delim2 )
    {
        this.delim2 = delim2;
    }

    public void setStripLabel( String stripLabel )
    {
        this.stripLabel = stripLabel;
    }

    public String getStripNameFullPrint()
    {
        return stripNameFullPrint;
    }

    public void setStripNameFullPrint( String stripNameFullPrint )
    {
        this.stripNameFullPrint = stripNameFullPrint;
    }

    public boolean isDisabled()
    {
        return disabled;
    }

    public void setDisabled( boolean disabled )
    {
        this.disabled = disabled;
    }
}
