package com.hydro.casting.server.model.shift;

import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.*;

@Entity
@Table( name = "shift_assignment", uniqueConstraints = @UniqueConstraint( columnNames = { "cost_center" } ) )
@NamedQuery( name = "shiftAssignment.costCenter", query = "select sa from ShiftAssignment sa where sa.costCenter = :costCenter")
public class ShiftAssignment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Column( name = "cost_center", unique = true, nullable = false, length = 4 )
    private String costCenter;
    @Column( name = "crew_size" )
    private Integer crewSize;
    @Column( name = "loc_status", length = 5 )
    private String locStatus;
    @Column( name = "shift_type", length = 1 )
    private String shiftType;

    public String getCostCenter()
    {
        return this.costCenter;
    }

    public void setCostCenter( String costCenter )
    {
        this.costCenter = costCenter;
    }

    public Integer getCrewSize()
    {
        return this.crewSize;
    }

    public void setCrewSize( Integer crewSize )
    {
        this.crewSize = crewSize;
    }

    public String getLocStatus()
    {
        return this.locStatus;
    }

    public void setLocStatus( String locStatus )
    {
        this.locStatus = locStatus;
    }

    public String getShiftType()
    {
        return this.shiftType;
    }

    public void setShiftType( String shiftType )
    {
        this.shiftType = shiftType;
    }

}
