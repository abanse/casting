package com.hydro.casting.server.model.shift;

import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.*;

@Entity
@Table( name = "shift_record", uniqueConstraints = @UniqueConstraint( columnNames = { "shift_type", "shift_number" } ) )
@NamedQuery( name = "shiftRecord.shiftType", query = "select sr from ShiftRecord sr where sr.shiftType = :shiftType order by sr.startTime")
public class ShiftRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Column( name = "shift_type", nullable = false, length = 5 )
    private String shiftType;
    @Column( name = "shift_number", nullable = false, length = 5 )
    private String shiftNumber;
    @Column( name = "supervisor", length = 40 )
    private String supervisor;
    @Column( name = "plant_number", length = 5 )
    private String plantNumber;
    @Column( name = "scheduled_hours" )
    private Integer scheduledHours;
    @Column( name = "start_time" )
    private Integer startTime;
    @Column( name = "end_time" )
    private Integer endTime;
    @Column( name = "adj_date", length = 5 )
    private String adjDate;

    public String getShiftType()
    {
        return this.shiftType;
    }

    public void setShiftType( String shiftType )
    {
        this.shiftType = shiftType;
    }

    public String getShiftNumber()
    {
        return this.shiftNumber;
    }

    public void setShiftNumber( String shiftNumber )
    {
        this.shiftNumber = shiftNumber;
    }

    public String getSupervisor()
    {
        return this.supervisor;
    }

    public void setSupervisor( String supervisor )
    {
        this.supervisor = supervisor;
    }

    public String getPlantNumber()
    {
        return this.plantNumber;
    }

    public void setPlantNumber( String plantNumber )
    {
        this.plantNumber = plantNumber;
    }

    public Integer getScheduledHours()
    {
        return this.scheduledHours;
    }

    public void setScheduledHours( Integer scheduledHours )
    {
        this.scheduledHours = scheduledHours;
    }

    public Integer getStartTime()
    {
        return this.startTime;
    }

    public void setStartTime( Integer startTime )
    {
        this.startTime = startTime;
    }

    public Integer getEndTime()
    {
        return this.endTime;
    }

    public void setEndTime( Integer endTime )
    {
        this.endTime = endTime;
    }

    public String getAdjDate()
    {
        return this.adjDate;
    }

    public void setAdjDate( String adjDate )
    {
        this.adjDate = adjDate;
    }

}
