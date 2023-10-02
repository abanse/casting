package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.util.Objects;

public class CasterOccupancyDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private String caster;
    private String charge;

    private CasterOccupancyPosDTO pos1;
    private CasterOccupancyPosDTO pos2;
    private CasterOccupancyPosDTO pos3;
    private CasterOccupancyPosDTO pos4;
    private CasterOccupancyPosDTO pos5;

    @Override
    public long getId()
    {
        if ( charge == null )
        {
            return 0;
        }
        return charge.hashCode();
    }

    public String getCaster()
    {
        return caster;
    }

    public void setCaster( String caster )
    {
        this.caster = caster;
    }

    public String getCharge()
    {
        return charge;
    }

    public void setCharge( String charge )
    {
        this.charge = charge;
    }

    public CasterOccupancyPosDTO getPos1()
    {
        return pos1;
    }

    public void setPos1( CasterOccupancyPosDTO pos1 )
    {
        this.pos1 = pos1;
    }

    public CasterOccupancyPosDTO getPos2()
    {
        return pos2;
    }

    public void setPos2( CasterOccupancyPosDTO pos2 )
    {
        this.pos2 = pos2;
    }

    public CasterOccupancyPosDTO getPos3()
    {
        return pos3;
    }

    public void setPos3( CasterOccupancyPosDTO pos3 )
    {
        this.pos3 = pos3;
    }

    public CasterOccupancyPosDTO getPos4()
    {
        return pos4;
    }

    public void setPos4( CasterOccupancyPosDTO pos4 )
    {
        this.pos4 = pos4;
    }

    public CasterOccupancyPosDTO getPos5()
    {
        return pos5;
    }

    public void setPos5( CasterOccupancyPosDTO pos5 )
    {
        this.pos5 = pos5;
    }

    public CasterOccupancyPosDTO getPos( int pos )
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

    public void setPos( int pos, CasterOccupancyPosDTO casterOccupancyPosDTO )
    {
        if ( pos == 1 )
        {
            setPos1( casterOccupancyPosDTO );
        }
        else if ( pos == 2 )
        {
            setPos2( casterOccupancyPosDTO );
        }
        else if ( pos == 3 )
        {
            setPos3( casterOccupancyPosDTO );
        }
        else if ( pos == 4 )
        {
            setPos4( casterOccupancyPosDTO );
        }
        else if ( pos == 5 )
        {
            setPos5( casterOccupancyPosDTO );
        }
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        CasterOccupancyDTO that = (CasterOccupancyDTO) o;
        return Objects.equals( caster, that.caster ) && Objects.equals( charge, that.charge ) && Objects.equals( pos1, that.pos1 ) && Objects.equals( pos2, that.pos2 ) && Objects.equals( pos3,
                that.pos3 ) && Objects.equals( pos4, that.pos4 ) && Objects.equals( pos5, that.pos5 );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( caster, charge, pos1, pos2, pos3, pos4, pos5 );
    }

    @Override
    public String toString()
    {
        return "CasterOccupancyDTO{" + "caster='" + caster + '\'' + ", charge='" + charge + '\'' + ", pos1=" + pos1 + ", pos2=" + pos2 + ", pos3=" + pos3 + ", pos4=" + pos4 + ", pos5=" + pos5 + '}';
    }
}
