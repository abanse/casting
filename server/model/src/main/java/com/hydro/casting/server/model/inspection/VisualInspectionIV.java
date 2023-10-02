package com.hydro.casting.server.model.inspection;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue( "visual_inspection" )
public class VisualInspectionIV extends InspectionValue
{
    @Column( name = "vi_value" )
    private Integer visualInspectionValue;
    @Column( name = "vi_issue", length = 1024 )
    private String visualInspectionIssue;
    @Column( name = "vi_upd_usr_nets" )
    private String visualInspectionUpdateUserNets;
    @Column( name = "vi_upd_ts_nets" )
    private LocalDateTime visualInspectionUpdateTSNets;
    @Column( name = "vi_upd_usr_cast" )
    private String visualInspectionUpdateUserCast;
    @Column( name = "vi_upd_ts_cast" )
    private LocalDateTime visualInspectionUpdateTSCast;

    public Integer getVisualInspectionValue()
    {
        return visualInspectionValue;
    }

    public void setVisualInspectionValue( Integer visualInspectionValue )
    {
        this.visualInspectionValue = visualInspectionValue;
    }

    public String getVisualInspectionIssue()
    {
        return visualInspectionIssue;
    }

    public void setVisualInspectionIssue( String visualInspectionIssue )
    {
        this.visualInspectionIssue = visualInspectionIssue;
    }

    public String getVisualInspectionUpdateUserNets()
    {
        return visualInspectionUpdateUserNets;
    }

    public void setVisualInspectionUpdateUserNets( String visualInspectionUpdateUserNets )
    {
        this.visualInspectionUpdateUserNets = visualInspectionUpdateUserNets;
    }

    public LocalDateTime getVisualInspectionUpdateTSNets()
    {
        return visualInspectionUpdateTSNets;
    }

    public void setVisualInspectionUpdateTSNets( LocalDateTime visualInspectionUpdateTSNets )
    {
        this.visualInspectionUpdateTSNets = visualInspectionUpdateTSNets;
    }

    public String getVisualInspectionUpdateUserCast()
    {
        return visualInspectionUpdateUserCast;
    }

    public void setVisualInspectionUpdateUserCast( String visualInspectionUpdateUserCast )
    {
        this.visualInspectionUpdateUserCast = visualInspectionUpdateUserCast;
    }

    public LocalDateTime getVisualInspectionUpdateTSCast()
    {
        return visualInspectionUpdateTSCast;
    }

    public void setVisualInspectionUpdateTSCast( LocalDateTime visualInspectionUpdateTSCast )
    {
        this.visualInspectionUpdateTSCast = visualInspectionUpdateTSCast;
    }
}
