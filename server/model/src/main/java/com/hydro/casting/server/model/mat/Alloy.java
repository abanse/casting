package com.hydro.casting.server.model.mat;

import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table( name = "alloy" )
@NamedQuery( name = "alloy.activeByName", query = "select a from Alloy a where a.name = :name and a.active = true" )
@NamedQuery( name = "alloy.byNameAndVersion", query = "select a from Alloy a where a.name = :name and a.version = :version" )
@NamedQuery( name = "alloy.byAlloyId", query = "select a from Alloy a where a.alloyId = (:alloyId)")
public class Alloy extends BaseEntity
{
    @Column( name = "version" )
    private int version = 0;
    @Column( name = "active" )
    private boolean active;
    @Column( name = "name", length = 20 )
    private String name;
    @Column( name = "alloy_id", length = 38)
    private Long alloyId;
    @Column( name = "customer_name", length = 20 )
    private String customerName;
    @Column( name = "customer_version", length = 20 )
    private String customerVersion;
    @Column( name = "aa_norm", length = 20 )
    private String aaNorm;
    @Column( name = "en_norm", length = 20 )
    private String enNorm;
    @Column( name = "iso_norm", length = 20 )
    private String isoNorm;
    @Column( name = "description", length = 200 )
    private String description;
    @Column( name = "category", length = 20 )
    private String category;
    @Column( name = "name_norf", length = 20 )
    private String nameNorf;
    @Column( name = "name_sapa", length = 20 )
    private String nameSAPA;
    @Column( name = "name_mmg", length = 20 )
    private String nameMMG;
    @Column( name = "edit_state" )
    private int editState;
    @Column( name = "deactivation_time" )
    private LocalDateTime deactivationTime;
    @Column( name = "activation_time" )
    private LocalDateTime activationTime;
    @Column( name = "melting_relevant" )
    private boolean meltingRelevant = false;
    @OneToMany( mappedBy = "alloy", cascade = CascadeType.ALL, orphanRemoval = true )
    private Set<AlloyElement> alloyElements;

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public Long getAlloyId()
    {
        return alloyId;
    }

    public void setAlloyId( Long alloyId )
    {
        this.alloyId = alloyId;
    }

    public String getCustomerName()
    {
        return customerName;
    }

    public void setCustomerName( String customerName )
    {
        this.customerName = customerName;
    }

    public String getCustomerVersion()
    {
        return customerVersion;
    }

    public void setCustomerVersion( String customerVersion )
    {
        this.customerVersion = customerVersion;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public int getVersion()
    {
        return version;
    }

    public void setVersion( int newVersion )
    {
        this.version = newVersion;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive( boolean newActive )
    {
        this.active = newActive;
    }

    public String getAaNorm()
    {
        return aaNorm;
    }

    public void setAaNorm( String newAaNorm )
    {
        this.aaNorm = newAaNorm;
    }

    public String getEnNorm()
    {
        return enNorm;
    }

    public void setEnNorm( String newEnNorm )
    {
        this.enNorm = newEnNorm;
    }

    public String getIsoNorm()
    {
        return isoNorm;
    }

    public void setIsoNorm( String newIsoNorm )
    {
        this.isoNorm = newIsoNorm;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory( String newCategory )
    {
        this.category = newCategory;
    }

    public String getNameNorf()
    {
        return nameNorf;
    }

    public void setNameNorf( String newNameNorf )
    {
        this.nameNorf = newNameNorf;
    }

    public String getNameSAPA()
    {
        return nameSAPA;
    }

    public void setNameSAPA( String newNameSAPA )
    {
        this.nameSAPA = newNameSAPA;
    }

    public String getNameMMG()
    {
        return nameMMG;
    }

    public void setNameMMG( String newNameMMG )
    {
        this.nameMMG = newNameMMG;
    }

    public int getEditState()
    {
        return editState;
    }

    public void setEditState( int newEditState )
    {
        this.editState = newEditState;
    }

    public LocalDateTime getDeactivationTime()
    {
        return deactivationTime;
    }

    public void setDeactivationTime( LocalDateTime newDeactivationTime )
    {
        this.deactivationTime = newDeactivationTime;
    }

    public LocalDateTime getActivationTime()
    {
        return activationTime;
    }

    public void setActivationTime( LocalDateTime newActivationTime )
    {
        this.activationTime = newActivationTime;
    }

    public boolean getMeltingRelevant()
    {
        return meltingRelevant;
    }

    public void setMeltingRelevant( boolean meltingRelevant )
    {
        this.meltingRelevant = meltingRelevant;
    }

    public Set<AlloyElement> getAlloyElements()
    {
        if ( alloyElements == null )
        {
            alloyElements = new HashSet<>();
        }
        return alloyElements;
    }

    public boolean containsInAlloyElements( AlloyElement element )
    {
        return ( this.alloyElements != null ) && this.alloyElements.contains( element );
    }

    public int numberOfAlloyElements()
    {
        return ( alloyElements == null ) ? 0 : alloyElements.size();
    }

    public void flushAlloyElements()
    {
        if ( alloyElements != null )
        {
            for ( AlloyElement c : alloyElements )
            {
                c.oneSided_setAlloy( null ); // avoid recursion with CME in next statement
            }
            alloyElements.clear();  // no concurrent modification
        }
    }

    public void addToAlloyElements( AlloyElement element )
    {
        getAlloyElements().add( element );
        Alloy oldComposition = element.getAlloy();
        if ( ( oldComposition != this ) && ( oldComposition != null ) )
        {
            oldComposition.oneSided_removeFromAlloyElements( element );
        }
        element.oneSided_setAlloy( this );
    }

    public void removeFromAlloyElements( AlloyElement element )
    {
        getAlloyElements().remove( element );
        element.oneSided_setAlloy( null );
    }

    public void oneSided_addToAlloyElements( AlloyElement element )
    {
        getAlloyElements().add( element );
    }

    public void oneSided_removeFromAlloyElements( AlloyElement element )
    {
        getAlloyElements().remove( element );
    }

    public void removeAllAssociations()
    {
        flushAlloyElements();
    }

    @Override
    public String toString()
    {
        return "Alloy{" + "version=" + version + ", active=" + active + ", name='" + name + '\'' + ", deactivationTime=" + deactivationTime + ", activationTime=" + activationTime + '}';
    }
}