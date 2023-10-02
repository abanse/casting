package com.hydro.casting.server.model.mat;

import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table( name = "material_lock" )
public class MaterialLock extends BaseEntity
{
    @Column( name = "name", length = 20 )
    private String name;
    @Column( name = "active" )
    private boolean active;
    @Column( name = "imposator", length = 20 )
    private String imposator;
    @Column( name = "imposation" )
    private LocalDateTime imposation;
    @Column( name = "imposation_comment", length = 200 )
    private String imposationComment;
    @Column( name = "imposation_machine", length = 20 )
    private String imposationMachine;
    @Column( name = "revoker", length = 20 )
    private String revoker;
    @Column( name = "revocation" )
    private LocalDateTime revocation;
    @Column( name = "revocation_comment", length = 200 )
    private String revocationComment;
    @Column( name = "lock_comment", length = 4000 )
    private String lockComment;
    @Column( name = "material_status", length = 1 )
    private String materialStatus;
    @Column( name = "prod_start_ts" )
    private LocalDateTime prodStartTs;
    @Column( name = "prod_end_ts" )
    private LocalDateTime prodEndTs;
    @Column( name = "qs_start_ts" )
    private LocalDateTime qsStartTs;
    @Column( name = "qs_end_ts" )
    private LocalDateTime qsEndTs;
    @Column( name = "av_start_ts" )
    private LocalDateTime avStartTs;
    @Column( name = "av_end_ts" )
    private LocalDateTime avEndTs;
    @Column( name = "tcs_start_ts" )
    private LocalDateTime tcsStartTs;
    @Column( name = "tcs_end_ts" )
    private LocalDateTime tcsEndTs;

    @ManyToOne
    @JoinColumn( name = "material_lock_type_oid" )
    private MaterialLockType materialLockType;
    @ManyToOne
    @JoinColumn( name = "material_lock_location_oid" )
    private MaterialLockLocation materialLockLocation;
    @ManyToOne
    @JoinColumn( name = "material_oid" )
    private Material material;

    public String getName()
    {
        return name;
    }

    public void setName( String newName )
    {
        this.name = newName;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive( boolean newActive )
    {
        this.active = newActive;
    }

    public String getImposator()
    {
        return imposator;
    }

    public void setImposator( String newImposator )
    {
        this.imposator = newImposator;
    }

    public LocalDateTime getImposation()
    {
        return imposation;
    }

    public void setImposation( LocalDateTime newImposation )
    {
        this.imposation = newImposation;
    }

    public String getImposationComment()
    {
        return imposationComment;
    }

    public void setImposationComment( String newImposationComment )
    {
        this.imposationComment = newImposationComment;
    }

    public String getImposationMachine()
    {
        return imposationMachine;
    }

    public void setImposationMachine( String imposationMachine )
    {
        this.imposationMachine = imposationMachine;
    }

    public String getRevoker()
    {
        return revoker;
    }

    public void setRevoker( String newRevoker )
    {
        this.revoker = newRevoker;
    }

    public LocalDateTime getRevocation()
    {
        return revocation;
    }

    public void setRevocation( LocalDateTime newRevocation )
    {
        this.revocation = newRevocation;
    }

    public String getRevocationComment()
    {
        return revocationComment;
    }

    public void setRevocationComment( String newRevocationComment )
    {
        this.revocationComment = newRevocationComment;
    }

    public String getLockComment()
    {
        return lockComment;
    }

    public void setLockComment( String lockComment )
    {
        this.lockComment = lockComment;
    }

    public String getMaterialStatus()
    {
        return materialStatus;
    }

    public void setMaterialStatus( String materialStatus )
    {
        this.materialStatus = materialStatus;
    }

    public LocalDateTime getProdStartTs()
    {
        return prodStartTs;
    }

    public void setProdStartTs( LocalDateTime prodStartTs )
    {
        this.prodStartTs = prodStartTs;
    }

    public LocalDateTime getProdEndTs()
    {
        return prodEndTs;
    }

    public void setProdEndTs( LocalDateTime prodEndTs )
    {
        this.prodEndTs = prodEndTs;
    }

    public LocalDateTime getQsStartTs()
    {
        return qsStartTs;
    }

    public void setQsStartTs( LocalDateTime qsStartTs )
    {
        this.qsStartTs = qsStartTs;
    }

    public LocalDateTime getQsEndTs()
    {
        return qsEndTs;
    }

    public void setQsEndTs( LocalDateTime qsEndTs )
    {
        this.qsEndTs = qsEndTs;
    }

    public LocalDateTime getAvStartTs()
    {
        return avStartTs;
    }

    public void setAvStartTs( LocalDateTime avStartTs )
    {
        this.avStartTs = avStartTs;
    }

    public LocalDateTime getAvEndTs()
    {
        return avEndTs;
    }

    public void setAvEndTs( LocalDateTime avEndTs )
    {
        this.avEndTs = avEndTs;
    }

    public LocalDateTime getTcsStartTs()
    {
        return tcsStartTs;
    }

    public void setTcsStartTs( LocalDateTime tcsStartTs )
    {
        this.tcsStartTs = tcsStartTs;
    }

    public LocalDateTime getTcsEndTs()
    {
        return tcsEndTs;
    }

    public void setTcsEndTs( LocalDateTime tcsEndTs )
    {
        this.tcsEndTs = tcsEndTs;
    }

    public MaterialLockType getMaterialLockType()
    {
        return materialLockType;
    }

    public void setMaterialLockType( MaterialLockType newType )
    {
        this.materialLockType = newType;
    }

    public MaterialLockLocation getMaterialLockLocation()
    {
        return materialLockLocation;
    }

    public void setMaterialLockLocation( MaterialLockLocation materialLockLocation )
    {
        this.materialLockLocation = materialLockLocation;
    }

    public Material getMaterial()
    {
        return material;
    }

    public void setMaterial( Material newMaterial )
    {
        Material oldMaterial = this.material;
        this.material = newMaterial;
        if ( oldMaterial != newMaterial )
        {
            if ( oldMaterial != null )
            {
                oldMaterial.oneSided_removeFromLocks( this );
            }
            if ( newMaterial != null )
            {
                newMaterial.oneSided_addToLocks( this );
            }
        }
    }

    public void oneSided_setMaterial( Material newMaterial )
    {
        this.material = newMaterial;
    }

    public void removeAllAssociations()
    {
        setMaterialLockType( null );
        setMaterialLockLocation( null );
        setMaterial( null );
    }
}

