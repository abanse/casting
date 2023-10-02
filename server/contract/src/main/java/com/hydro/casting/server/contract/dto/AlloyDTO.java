package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.time.LocalDateTime;
import java.util.Objects;
public class AlloyDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private String name;
    private int version;
    private String description;
    private boolean active;
    private LocalDateTime activationTime;
    private boolean meltingRelevant;

    @Override
    public long getId()
    {
        return this.hashCode();
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public int getVersion()
    {
        return version;
    }

    public void setVersion( int version )
    {
        this.version = version;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive( boolean active )
    {
        this.active = active;
    }

    public LocalDateTime getActivationTime()
    {
        return activationTime;
    }

    public void setActivationTime( LocalDateTime activationTime )
    {
        this.activationTime = activationTime;
    }

    public boolean isMeltingRelevant()
    {
        return meltingRelevant;
    }

    public void setMeltingRelevant( boolean meltingRelevant )
    {
        this.meltingRelevant = meltingRelevant;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        AlloyDTO alloyDTO = (AlloyDTO) o;
        return getVersion() == alloyDTO.getVersion() && isActive() == alloyDTO.isActive() && isMeltingRelevant() == alloyDTO.isMeltingRelevant() && Objects.equals( getName(), alloyDTO.getName() )
                && Objects.equals( getDescription(), alloyDTO.getDescription() ) && Objects.equals( getActivationTime(), alloyDTO.getActivationTime() );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( getName(), getVersion(), getDescription(), isActive(), getActivationTime(), isMeltingRelevant() );
    }

    @Override
    public String toString()
    {
        return "AlloyDTO{" + "name='" + name + '\'' + ", version=" + version + ", description='" + description + '\'' + ", active=" + active + ", activationTime=" + activationTime
                + ", meltingRelevant=" + meltingRelevant + '}';
    }
}
