package com.hydro.casting.server.model.mat;

import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.*;

@Entity
@Table( name = "material_type" )
@NamedQuery( name = "materialType.apk", query = "select mt from MaterialType mt where mt.apk = :apk" )
@NamedQuery( name = "materialType.categoryAttributes", query = "select mt from MaterialType mt where mt.category = :category and mt.quality = :quality and mt.qualityCode = :qualityCode and mt.height = :height and mt.width = :width and mt.length = :length and mt.alloy.name = :alloy" )
public class MaterialType extends BaseEntity
{
    @Column( name = "apk", length = 20, unique = true )
    private String apk;
    @Column( name = "description", length = 200 )
    private String description;
    @Column( name = "category", length = 200 )
    private String category;
    @Column( name = "quality", length = 10 )
    private String quality;
    @Column( name = "quality_code", length = 10 )
    private String qualityCode;
    @Column( name = "height" )
    private double height;
    @Column( name = "width" )
    private double width;
    @Column( name = "length" )
    private double length;
    @Column( name = "amount" )
    private int amount;
    @ManyToOne
    @JoinColumn( name = "alloy_oid" )
    private Alloy alloy;
    @Column( name = "tags", length = 200 )
    private String tags;

    public String getApk()
    {
        return apk;
    }

    public void setApk( String newApk )
    {
        this.apk = newApk;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String newDescription )
    {
        this.description = newDescription;
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

    public Alloy getAlloy()
    {
        return alloy;
    }

    public void setAlloy( Alloy alloy )
    {
        this.alloy = alloy;
    }

    public int getAmount()
    {
        return amount;
    }

    public void setAmount( int amount )
    {
        this.amount = amount;
    }

    public String getTags()
    {
        return tags;
    }

    public void setTags( String tags )
    {
        this.tags = tags;
    }

    public void removeAllAssociations()
    {
        setAlloy( null );
    }
}