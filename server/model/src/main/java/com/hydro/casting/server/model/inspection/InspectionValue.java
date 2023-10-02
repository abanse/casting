package com.hydro.casting.server.model.inspection;

import com.hydro.casting.server.model.sched.Schedulable;
import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table( name = "inspection_value" )
@Inheritance( strategy = InheritanceType.SINGLE_TABLE )
@DiscriminatorColumn( name = "discriminator" )
@NamedQuery( name = "inspectionValue.findBySchedulableAndCategory", query = "select iv from InspectionValue iv where iv.schedulable = :schedulable and iv.inspectionRule.inspectionCategory = :inspectionCategory order by iv.inspectionRule.pos" )
@NamedQuery( name = "inspectionValue.findBySchedulable", query = "select iv from InspectionValue iv where iv.schedulable = :schedulable" )
@NamedQuery( name = "inspectionValue.getInspectionResult", query = "select max(iv.result) from InspectionValue iv where iv.inspectionRule.inspectionCategory.apk = :inspectionCategoryApk and iv.schedulable = :schedulable" )
public class InspectionValue extends BaseEntity
{
    @Column( name = "add_ts" )
    private LocalDateTime addTS;
    @Column( name = "add_user", length = 50 )
    private String addUser;
    @Column( name = "upd_ts" )
    private LocalDateTime updTS;
    @Column( name = "upd_user", length = 50 )
    private String updUser;
    @Column( name = "result" )
    private int result;
    @ManyToOne
    @JoinColumn( name = "schedulable_oid" )
    private Schedulable schedulable;
    @ManyToOne
    @JoinColumn( name = "inspection_rule_oid" )
    private InspectionRule inspectionRule;

    public LocalDateTime getAddTS()
    {
        return addTS;
    }

    public void setAddTS( LocalDateTime addTS )
    {
        this.addTS = addTS;
    }

    public String getAddUser()
    {
        return addUser;
    }

    public void setAddUser( String addUser )
    {
        this.addUser = addUser;
    }

    public LocalDateTime getUpdTS()
    {
        return updTS;
    }

    public void setUpdTS( LocalDateTime updTS )
    {
        this.updTS = updTS;
    }

    public String getUpdUser()
    {
        return updUser;
    }

    public void setUpdUser( String updUser )
    {
        this.updUser = updUser;
    }

    public int getResult()
    {
        return result;
    }

    public void setResult( int result )
    {
        this.result = result;
    }

    public Schedulable getSchedulable()
    {
        return schedulable;
    }

    public void setSchedulable( Schedulable schedulable )
    {
        this.schedulable = schedulable;
    }

    public InspectionRule getInspectionRule()
    {
        return inspectionRule;
    }

    public void setInspectionRule( InspectionRule newInspectionRule )
    {
        this.inspectionRule = newInspectionRule;
    }

    public void removeAllAssociations()
    {
        setSchedulable( null );
        setInspectionRule( null );
    }
}

