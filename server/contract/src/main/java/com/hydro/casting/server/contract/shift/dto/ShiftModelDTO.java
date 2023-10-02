package com.hydro.casting.server.contract.shift.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.time.LocalTime;

public class ShiftModelDTO implements ViewDTO
{

    private static final long serialVersionUID = 1L;

    private long id;
    private String costCenter;
    private LocalTime shift1Start;
    private LocalTime shift1End;
    private LocalTime shift2Start;
    private LocalTime shift2End;
    private LocalTime shift3Start;
    private LocalTime shift3End;

    @Override
    public long getId()
    {
        return id;
    }

    public String getCostCenter()
    {
        return costCenter;
    }

    public void setCostCenter( String costCenter )
    {
        this.costCenter = costCenter;
    }

    public LocalTime getShift1Start()
    {
        return shift1Start;
    }

    public void setShift1Start( LocalTime shift1Start )
    {
        this.shift1Start = shift1Start;
    }

    public LocalTime getShift1End()
    {
        return shift1End;
    }

    public void setShift1End( LocalTime shift1End )
    {
        this.shift1End = shift1End;
    }

    public LocalTime getShift2Start()
    {
        return shift2Start;
    }

    public void setShift2Start( LocalTime shift2Start )
    {
        this.shift2Start = shift2Start;
    }

    public LocalTime getShift2End()
    {
        return shift2End;
    }

    public void setShift2End( LocalTime shift2End )
    {
        this.shift2End = shift2End;
    }

    public LocalTime getShift3Start()
    {
        return shift3Start;
    }

    public void setShift3Start( LocalTime shift3Start )
    {
        this.shift3Start = shift3Start;
    }

    public LocalTime getShift3End()
    {
        return shift3End;
    }

    public void setShift3End( LocalTime shift3End )
    {
        this.shift3End = shift3End;
    }

}
