package com.hydro.casting.server.model.inspection;

import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.*;

@Entity
@Table( name = "inspection_rule" )
@Inheritance( strategy = InheritanceType.SINGLE_TABLE )
@DiscriminatorColumn( name = "discriminator" )
public class InspectionRule extends BaseEntity
{
    @Column( name = "name", length = 20 )
    private String name;
    @Column( name = "type", length = 20 )
    private String type;
    @Column( name = "description", length = 200 )
    private String description;
    @Column( name = "pos" )
    private int pos;
    @ManyToOne
    @JoinColumn( name = "inspection_category_oid" )
    private InspectionCategory inspectionCategory;

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String newDescription )
    {
        this.description = newDescription;
    }

    public int getPos()
    {
        return pos;
    }

    public void setPos( int pos )
    {
        this.pos = pos;
    }

    public InspectionCategory getInspectionCategory()
    {
        return inspectionCategory;
    }

    public void setInspectionCategory( InspectionCategory newInspectionCategory )
    {
        InspectionCategory oldInspectionCategory = this.inspectionCategory;
        this.inspectionCategory = newInspectionCategory;
        if ( oldInspectionCategory != newInspectionCategory )
        {
            if ( oldInspectionCategory != null )
            {
                oldInspectionCategory.oneSided_removeFromInspectionRules( this );
            }
            if ( newInspectionCategory != null )
            {
                newInspectionCategory.oneSided_addToInspectionRules( this );
            }
        }
    }

    public void oneSided_setInspectionCategory( InspectionCategory newInspectionCategory )
    {
        this.inspectionCategory = newInspectionCategory;
    }

    public void removeAllAssociations()
    {
        setInspectionCategory( null );
    }
}

