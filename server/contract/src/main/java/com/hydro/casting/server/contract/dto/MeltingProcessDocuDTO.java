package com.hydro.casting.server.contract.dto;

import com.hydro.core.common.util.StringTools;
import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.time.LocalDateTime;
import java.util.Objects;
public class MeltingProcessDocuDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private long id;
    private String charge;
    private String alloy;
    private LocalDateTime inProgressTS;
    private LocalDateTime successTS;

    public MeltingProcessDocuDTO()
    {
    }

    public MeltingProcessDocuDTO( long id )
    {
        this.id = id;
    }

    @Override
    public long getId()
    {
        return id;
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

    public void setChargeWithoutYear( String chargeWithoutYear )
    {
        // only getter needed
    }

    public String getCharge()
    {
        return charge;
    }

    public void setCharge( String charge )
    {
        this.charge = charge;
    }

    public String getAlloy()
    {
        return alloy;
    }

    public void setAlloy( String alloy )
    {
        this.alloy = alloy;
    }

    public LocalDateTime getInProgressTS()
    {
        return inProgressTS;
    }

    public void setInProgressTS( LocalDateTime inProgressTS )
    {
        this.inProgressTS = inProgressTS;
    }

    public LocalDateTime getSuccessTS()
    {
        return successTS;
    }

    public void setSuccessTS( LocalDateTime successTS )
    {
        this.successTS = successTS;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        MeltingProcessDocuDTO that = (MeltingProcessDocuDTO) o;
        return getId() == that.getId() && Objects.equals( getCharge(), that.getCharge() ) && Objects.equals( getAlloy(), that.getAlloy() ) && Objects.equals( getInProgressTS(),
                that.getInProgressTS() ) && Objects.equals( getSuccessTS(), that.getSuccessTS() );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( getId(), getCharge(), getAlloy(), getInProgressTS(), getSuccessTS() );
    }

    @Override
    public String toString()
    {
        return "MeltingProcessDocuDTO{" + "id=" + id + ", charge='" + charge + '\'' + ", alloy='" + alloy + '\'' + ", inProgressTS=" + inProgressTS + ", successTS=" + successTS + '}';
    }
}
