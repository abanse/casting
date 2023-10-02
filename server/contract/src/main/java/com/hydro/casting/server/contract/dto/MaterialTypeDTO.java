package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.util.Objects;

public class MaterialTypeDTO implements ViewDTO
{
    private long id;
    private String apk;
    private String description;
    private String category;
    private String quality;
    private String qualityCode;
    private double height;
    private double width;
    private double length;
    private int amount;
    private String alloyName;
    private String tags;

    @Override
    public long getId()
    {
        return id;
    }

    public void setId( long id )
    {
        this.id = id;
    }

    public String getApk()
    {
        return apk;
    }

    public void setApk( String apk )
    {
        this.apk = apk;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory( String category )
    {
        this.category = category;
    }

    public String getQuality()
    {
        return quality;
    }

    public void setQuality( String quality )
    {
        this.quality = quality;
    }

    public String getQualityCode()
    {
        return qualityCode;
    }

    public void setQualityCode( String qualityCode )
    {
        this.qualityCode = qualityCode;
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

    public int getAmount()
    {
        return amount;
    }

    public void setAmount( int amount )
    {
        this.amount = amount;
    }

    public String getAlloyName()
    {
        return alloyName;
    }

    public void setAlloyName( String alloyName )
    {
        this.alloyName = alloyName;
    }

    public String getTags()
    {
        return tags;
    }

    public void setTags( String tags )
    {
        this.tags = tags;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( !( o instanceof MaterialTypeDTO ) )
            return false;
        MaterialTypeDTO that = (MaterialTypeDTO) o;
        return id == that.id && Double.compare( that.height, height ) == 0 && Double.compare( that.width, width ) == 0 && Double.compare( that.length, length ) == 0 && amount == that.amount
                && Objects.equals( apk, that.apk ) && Objects.equals( description, that.description ) && Objects.equals( category, that.category ) && Objects.equals( quality, that.quality )
                && Objects.equals( qualityCode, that.qualityCode ) && Objects.equals( alloyName, that.alloyName ) && Objects.equals( tags, that.tags );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, apk, description, category, quality, qualityCode, height, width, length, amount, alloyName, tags );
    }

    @Override
    public String toString()
    {
        return "MaterialTypeDTO{" + "id=" + id + ", apk='" + apk + '\'' + ", description='" + description + '\'' + ", category='" + category + '\'' + ", quality='" + quality + '\''
                + ", qualityCode='" + qualityCode + '\'' + ", height=" + height + ", width=" + width + ", length=" + length + ", amount=" + amount + ", alloyName='" + alloyName + '\'' + ", tags='"
                + tags + '\'' + '}';
    }
}