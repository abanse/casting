package com.hydro.casting.server.model.mat;

import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table( name = "material_lock_location" )
@NamedQuery( name = "materialLockLocation.apk", query = "select mll from MaterialLockLocation mll where mll.apk = :apk" )
public class MaterialLockLocation extends BaseEntity
{
    @Column( name = "apk", length = 20 )
    private String apk;
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

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String newDescription )
    {
        this.description = newDescription;
    }

}