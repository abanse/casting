package com.hydro.casting.server.contract.dto;

import java.io.Serializable;
import java.util.Objects;

public class ActualValueDTO implements Serializable
{
    private long timestamp;
    private double value;

    public ActualValueDTO()
    {
    }

    public ActualValueDTO( long timestamp, double value )
    {
        this.timestamp = timestamp;
        this.value = value;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp( long timestamp )
    {
        this.timestamp = timestamp;
    }

    public double getValue()
    {
        return value;
    }

    public void setValue( double value )
    {
        this.value = value;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( !( o instanceof ActualValueDTO ) )
            return false;
        ActualValueDTO that = (ActualValueDTO) o;
        return timestamp == that.timestamp && Double.compare( that.value, value ) == 0;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( timestamp, value );
    }
}
