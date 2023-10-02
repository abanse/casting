package com.hydro.casting.server.model.downtime;

import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table( name = "downtime_request" )
@NamedQuery( name = "downtimeRequest.findOpenByContent", query = "select dtr from DowntimeRequest dtr where dtr.costCenter = (:costCenter) and dtr.phase = (:phase) and dtr.description = (:description) and dtr.releaseTS is null and dtr.endTS is null" )
@NamedQuery( name = "downtimeRequest.findAllOpenForCostCenterWithoutEnd", query = "select dtr from DowntimeRequest dtr where dtr.costCenter = (:costCenter) and dtr.endTS is null and dtr.releaseTS is null" )
public class DowntimeRequest extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Column( name = "cost_center", length = 10, nullable = false )
    private String costCenter;

    @Column( name = "machine", length = 10 )
    private String machine;

    @Column( name = "from_ts", nullable = false )
    private LocalDateTime fromTS;

    @Column( name = "end_ts" )
    private LocalDateTime endTS;

    @Column( name = "description", length = 255 )
    private String description;

    @Column( name = "phase", length = 30 )
    private String phase;

    @Column( name = "create_ts" )
    private LocalDateTime createTS;

    @Column( name = "release_user", length = 100 )
    private String releaseUser;

    @Column( name = "release_ts" )
    private LocalDateTime releaseTS;

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

    public LocalDateTime getFromTS()
    {
        return fromTS;
    }

    public void setFromTS( LocalDateTime fromTS )
    {
        this.fromTS = fromTS;
    }

    public LocalDateTime getEndTS()
    {
        return endTS;
    }

    public void setEndTS( LocalDateTime endTS )
    {
        this.endTS = endTS;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getPhase()
    {
        return phase;
    }

    public void setPhase( String phase )
    {
        this.phase = phase;
    }

    public LocalDateTime getCreateTS()
    {
        return createTS;
    }

    public void setCreateTS( LocalDateTime createTS )
    {
        this.createTS = createTS;
    }

    public String getReleaseUser()
    {
        return releaseUser;
    }

    public void setReleaseUser( String releaseUser )
    {
        this.releaseUser = releaseUser;
    }

    public LocalDateTime getReleaseTS()
    {
        return releaseTS;
    }

    public void setReleaseTS( LocalDateTime releaseTS )
    {
        this.releaseTS = releaseTS;
    }
}
