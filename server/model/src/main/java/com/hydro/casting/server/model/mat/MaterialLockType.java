package com.hydro.casting.server.model.mat;

import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table( name = "material_lock_type" )
@NamedQuery( name = "materialLockType.apk", query = "select mlt from MaterialLockType mlt where mlt.apk = :apk" )
public class MaterialLockType extends BaseEntity
{
    @Column( name = "apk", length = 20 )
    private String apk;
    @Column( name = "prohibit_transportation" )
    private boolean prohibitTransportation;
    @Column( name = "prohibit_processing" )
    private boolean prohibitProcessing;
    @Column( name = "prohibit_shipping" )
    private boolean prohibitShipping;
    @Column( name = "description", length = 200 )
    private String description;

    public String getApk()
    {
        return apk;
    }

    public void setApk( String newApk )
    {
        this.apk = newApk;
    }

    public boolean getProhibitTransportation()
    {
        return prohibitTransportation;
    }

    public void setProhibitTransportation( boolean newProhibitTransportation )
    {
        this.prohibitTransportation = newProhibitTransportation;
    }

    public boolean getProhibitProcessing()
    {
        return prohibitProcessing;
    }

    public void setProhibitProcessing( boolean newProhibitProcessing )
    {
        this.prohibitProcessing = newProhibitProcessing;
    }

    public boolean getProhibitShipping()
    {
        return prohibitShipping;
    }

    public void setProhibitShipping( boolean newProhibitShiping )
    {
        this.prohibitShipping = newProhibitShiping;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String newDescription )
    {
        this.description = newDescription;
    }

}