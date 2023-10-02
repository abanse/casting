package com.hydro.casting.server.model.downtime;

import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table( name = "downtime" )
@NamedQuery( name = "downtime.costCenter.machine", query = "SELECT dt FROM Downtime dt WHERE dt.costCenter = :costCenter AND dt.machine = :machine AND dt.endTS is null ORDER BY dt.fromTS" )
@NamedQuery( name = "downtime.costCenter", query = "SELECT dt FROM Downtime dt WHERE dt.costCenter = :costCenter AND dt.endTS is null ORDER BY dt.fromTS" )
@NamedQuery( name = "closed.downtime.costCenter.machine", query = "SELECT dt FROM Downtime dt WHERE dt.costCenter = :costCenter AND dt.machine = :machine AND dt.endTS > :after ORDER BY dt.fromTS" )
@NamedQuery( name = "closed.downtime.costCenter", query = "SELECT dt FROM Downtime dt WHERE dt.costCenter = :costCenter AND dt.endTS > :after ORDER BY dt.fromTS" )
public class Downtime extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Column( name = "cost_center", length = 10, nullable = false )
    private String costCenter;

    @Column( name = "machine", length = 10 )
    private String machine;

    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "downtime_kind_oid" )
    private DowntimeKind downtimeKind;

    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "downtime_module_oid" )
    private DowntimeModule downtimeModule;

    @Column( name = "from_ts", nullable = false )
    private LocalDateTime fromTS;

    @Column( name = "end_ts" )
    private LocalDateTime endTS;

    @Column( name = "shift", nullable = false, length = 3 )
    private String shift;

    @Column( name = "amount" )
    private Integer amount;

    @Column( name = "description", length = 255 )
    private String description;

    @Column( name = "remark", length = 255 )
    private String remark;

    @Column( name = "user_id", length = 50 )
    private String userId;

    @Column( name = "create_ts" )
    private LocalDateTime createTS;

    @Column( name = "type" )
    private String type;

    public String getCostCenter()
    {
        return costCenter;
    }

    public void setCostCenter( String kostenstelle )
    {
        this.costCenter = kostenstelle;
    }

    public String getMachine()
    {
        return machine;
    }

    public void setMachine( String maschine )
    {
        this.machine = maschine;
    }

    public DowntimeKind getDowntimeKind()
    {
        return downtimeKind;
    }

    public void setDowntimeKind( DowntimeKind stillstandsArten )
    {
        this.downtimeKind = stillstandsArten;
    }

    public DowntimeModule getDowntimeModule()
    {
        return downtimeModule;
    }

    public void setDowntimeModule( DowntimeModule baugruppe )
    {
        this.downtimeModule = baugruppe;
    }

    public LocalDateTime getFromTS()
    {
        return fromTS;
    }

    public void setFromTS( LocalDateTime datumVon )
    {
        this.fromTS = datumVon;
    }

    public LocalDateTime getEndTS()
    {
        return endTS;
    }

    public void setEndTS( LocalDateTime datumBis )
    {
        this.endTS = datumBis;
    }

    public String getShift()
    {
        return shift;
    }

    public void setShift( String schicht )
    {
        this.shift = schicht;
    }

    public Integer getAmount()
    {
        return amount;
    }

    public void setAmount( Integer anzahl )
    {
        this.amount = anzahl;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String bemerkungen )
    {
        this.description = bemerkungen;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark( String comment )
    {
        this.remark = comment;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId( String userId )
    {
        this.userId = userId;
    }

    public LocalDateTime getCreateTS()
    {
        return createTS;
    }

    public void setCreateTS( LocalDateTime datetimeStamp )
    {
        this.createTS = datetimeStamp;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public void removeAllAssociations()
    {
        setDowntimeModule( null );
        setDowntimeKind( null );
    }
}
