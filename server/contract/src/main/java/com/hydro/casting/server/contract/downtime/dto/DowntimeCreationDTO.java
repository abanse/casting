package com.hydro.casting.server.contract.downtime.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.time.LocalDateTime;
import java.util.List;

public class DowntimeCreationDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private long id;

    private List<DowntimeKindDTO> downtimeKinds;

    private List<DowntimeModuleDTO> downtimeModules;

    private String costCenter;
    private String machine;
    private String description;
    private LocalDateTime startTS;
    private LocalDateTime endTS;
    private DowntimeKindDTO downtimeKind;
    private DowntimeModuleDTO downtimeModule;

    @Override
    public long getId()
    {
        return id;
    }

    public void setId( long id )
    {
        this.id = id;
    }

    public List<DowntimeKindDTO> getDowntimeKinds()
    {
        return downtimeKinds;
    }

    public void setDowntimeKinds( List<DowntimeKindDTO> downtimeKinds )
    {
        this.downtimeKinds = downtimeKinds;
    }

    public List<DowntimeModuleDTO> getDowntimeModules()
    {
        return downtimeModules;
    }

    public void setDowntimeModules( List<DowntimeModuleDTO> downtimeModules )
    {
        this.downtimeModules = downtimeModules;
    }

    public String getCostCenter()
    {
        return costCenter;
    }

    public void setCostCenter( String costCenter )
    {
        this.costCenter = costCenter;
    }

    public String getMachine()
    {
        return machine;
    }

    public void setMachine( String machine )
    {
        this.machine = machine;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public LocalDateTime getStartTS()
    {
        return startTS;
    }

    public void setStartTS( LocalDateTime startTS )
    {
        this.startTS = startTS;
    }

    public LocalDateTime getEndTS()
    {
        return endTS;
    }

    public void setEndTS( LocalDateTime endTS )
    {
        this.endTS = endTS;
    }

    public DowntimeKindDTO getDowntimeKind()
    {
        return downtimeKind;
    }

    public void setDowntimeKind( DowntimeKindDTO downtimeKind )
    {
        this.downtimeKind = downtimeKind;
    }

    public DowntimeModuleDTO getDowntimeModule()
    {
        return downtimeModule;
    }

    public void setDowntimeModule( DowntimeModuleDTO downtimeModule )
    {
        this.downtimeModule = downtimeModule;
    }
}
