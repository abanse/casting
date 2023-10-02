package com.hydro.casting.server.model.inspection;

import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Entity
@Table( name = "inspection_category" )
@NamedQuery( name = "inspectionCategory.apk", query = "select ic from InspectionCategory ic where ic.apk = :apk" )
public class InspectionCategory extends BaseEntity
{
    @Column( name = "apk", length = 20 )
    private String apk;
    @OneToMany( mappedBy = "inspectionCategory" )
    private Set<InspectionRule> inspectionRules;

    public String getApk()
    {
        return apk;
    }

    public void setApk( String newApk )
    {
        this.apk = newApk;
    }

    public Set<InspectionRule> getInspectionRules()
    {
        // lazy evaluation; the field might still be null
        if ( inspectionRules == null )
        {
            inspectionRules = new HashSet<InspectionRule>();
        }
        return inspectionRules;
    }

    public boolean containsInInspectionRule( InspectionRule inspectionRule )
    {
        return ( this.inspectionRules != null ) && this.inspectionRules.contains( inspectionRule );
    }

    public int numberOfInspectionRuless()
    {
        // handle uninitialized set, but do not initialize it now
        return ( inspectionRules == null ) ? 0 : inspectionRules.size();
    }

    public void flushInspectionRules()
    {
        if ( inspectionRules != null )
        {
            Iterator<InspectionRule> iter = inspectionRules.iterator();
            while ( iter.hasNext() )
            {
                iter.next().oneSided_setInspectionCategory( null );
            }
            inspectionRules = null;  // no concurrent modification
        }
    }

    public void addToInspectionRules( InspectionRule inspectionRule )
    {
        getInspectionRules().add( inspectionRule );
        InspectionCategory oldInspectionCategory = inspectionRule.getInspectionCategory();
        if ( ( oldInspectionCategory != this ) && ( oldInspectionCategory != null ) )
        {
            oldInspectionCategory.oneSided_removeFromInspectionRules( inspectionRule );
        }
        inspectionRule.oneSided_setInspectionCategory( this );
    }

    public void removeFromInspectionRules( InspectionRule inspectionRule )
    {
        getInspectionRules().remove( inspectionRule );
        inspectionRule.oneSided_setInspectionCategory( null );
    }

    public void oneSided_addToInspectionRules( InspectionRule inspectionRule )
    {
        getInspectionRules().add( inspectionRule );
    }

    public void oneSided_removeFromInspectionRules( InspectionRule inspectionRule )
    {
        getInspectionRules().remove( inspectionRule );
    }

    public void removeAllAssociations()
    {
        flushInspectionRules();
    }
}