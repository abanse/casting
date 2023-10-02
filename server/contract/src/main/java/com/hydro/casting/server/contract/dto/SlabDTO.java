package com.hydro.casting.server.contract.dto;

import com.hydro.casting.server.contract.locking.material.dto.LockableMaterialDTO;
import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.time.LocalDateTime;
import java.util.Objects;

public class SlabDTO implements LockableMaterialDTO
{
    private static final long serialVersionUID = 1L;

    private String slab;
    private String alloy;
    private String quality;
    private double height;
    private double width;
    private double length;
    private String experimentNumber;
    private double weight;
    private double effLength;
    private LocalDateTime yardTS;
    private String customerId;
    private String errorCode;
    private String error;
    private String location;
    private String norfAlloy;
    private int countLocks;
    private int countActiveLocks;

    @Override
    public long getId()
    {
        if ( slab != null )
        {
            return slab.hashCode();
        }
        return 0;
    }

    @Override
    public String getMaterialName()
    {
        return slab;
    }

    public String getSlab()
    {
        return slab;
    }

    public void setSlab( String slab )
    {
        this.slab = slab;
    }

    public String getAlloy()
    {
        return alloy;
    }

    public void setAlloy( String alloy )
    {
        this.alloy = alloy;
    }

    public String getQuality()
    {
        return quality;
    }

    public void setQuality( String quality )
    {
        this.quality = quality;
    }

    public double getHeight()
    {
        return height;
    }

    public void setHeight( double height )
    {
        this.height = height;
    }

    public double getWidth()
    {
        return width;
    }

    public void setWidth( double width )
    {
        this.width = width;
    }

    public double getLength()
    {
        return length;
    }

    public void setLength( double length )
    {
        this.length = length;
    }

    public String getExperimentNumber()
    {
        return experimentNumber;
    }

    public void setExperimentNumber( String experimentNumber )
    {
        this.experimentNumber = experimentNumber;
    }

    public double getWeight()
    {
        return weight;
    }

    public void setWeight( double weight )
    {
        this.weight = weight;
    }

    public double getEffLength()
    {
        return effLength;
    }

    public void setEffLength( double effLength )
    {
        this.effLength = effLength;
    }

    public LocalDateTime getYardTS()
    {
        return yardTS;
    }

    public void setYardTS( LocalDateTime yardTS )
    {
        this.yardTS = yardTS;
    }

    public String getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId( String customerId )
    {
        this.customerId = customerId;
    }

    public String getErrorCode()
    {
        return errorCode;
    }

    public void setErrorCode( String errorCode )
    {
        this.errorCode = errorCode;
    }

    public String getError()
    {
        return error;
    }

    public void setError( String error )
    {
        this.error = error;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation( String location )
    {
        this.location = location;
    }

    public String getNorfAlloy()
    {
        return norfAlloy;
    }

    public void setNorfAlloy( String norfAlloy )
    {
        this.norfAlloy = norfAlloy;
    }

    public int getCountLocks()
    {
        return countLocks;
    }

    public void setCountLocks( int countLocks )
    {
        this.countLocks = countLocks;
    }

    public int getCountActiveLocks()
    {
        return countActiveLocks;
    }

    public void setCountActiveLocks( int countActiveLocks )
    {
        this.countActiveLocks = countActiveLocks;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( !( o instanceof SlabDTO ) )
            return false;
        SlabDTO slabDTO = (SlabDTO) o;
        return Double.compare( slabDTO.height, height ) == 0 && Double.compare( slabDTO.width, width ) == 0 && Double.compare( slabDTO.length, length ) == 0
                && Double.compare( slabDTO.weight, weight ) == 0 && Double.compare( slabDTO.effLength, effLength ) == 0 && countLocks == slabDTO.countLocks
                && countActiveLocks == slabDTO.countActiveLocks && Objects.equals( slab, slabDTO.slab ) && Objects.equals( alloy, slabDTO.alloy ) && Objects.equals( quality, slabDTO.quality )
                && Objects.equals( experimentNumber, slabDTO.experimentNumber ) && Objects.equals( yardTS, slabDTO.yardTS ) && Objects.equals( customerId, slabDTO.customerId ) && Objects.equals(
                errorCode, slabDTO.errorCode ) && Objects.equals( error, slabDTO.error ) && Objects.equals( location, slabDTO.location ) && Objects.equals( norfAlloy, slabDTO.norfAlloy );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( slab, alloy, quality, height, width, length, experimentNumber, weight, effLength, yardTS, customerId, errorCode, error, location, norfAlloy, countLocks,
                countActiveLocks );
    }

    @Override
    public String toString()
    {
        return "SlabDTO{" + "slab='" + slab + '\'' + ", alloy='" + alloy + '\'' + ", quality='" + quality + '\'' + ", height=" + height + ", width=" + width + ", length=" + length
                + ", experimentNumber='" + experimentNumber + '\'' + ", weight=" + weight + ", effLength=" + effLength + ", yardTS=" + yardTS + ", customerId='" + customerId + '\'' + ", errorCode='"
                + errorCode + '\'' + ", error='" + error + '\'' + ", location='" + location + '\'' + ", norfAlloy='" + norfAlloy + '\'' + ", countLocks=" + countLocks + ", countActiveLocks="
                + countActiveLocks + '}';
    }
}