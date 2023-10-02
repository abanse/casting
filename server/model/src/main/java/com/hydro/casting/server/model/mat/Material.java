package com.hydro.casting.server.model.mat;

import com.hydro.casting.server.model.sched.Operation;
import com.hydro.casting.server.model.wms.HandlingUnit;
import com.hydro.core.server.common.model.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Entity
@Table( name = "material" )
@Inheritance( strategy = InheritanceType.SINGLE_TABLE )
@DiscriminatorColumn( name = "discriminator" )
@DiscriminatorValue( "material" )
@NamedQuery( name = "material.name", query = "select m from Material m where m.name = :name" )
@NamedQuery( name = "material.findByConsumingOperation", query = "select m from Material m where m.consumingOperation.objid = :consumingOperationOID" )
public class Material extends BaseEntity
{
    @Column( name = "name", length = 50 )
    private String name;
    @Column( name = "weight" )
    private double weight;
    @Column( name = "generation_state" )
    private int generationState = 100;
    @Column( name = "generation_index" )
    private int generationIndex;
    @Column( name = "generation_planned_ts" )
    private LocalDateTime generationPlannedTS;
    @Column( name = "generation_in_progress_ts" )
    private LocalDateTime generationInProgressTS;
    @Column( name = "generation_success_ts" )
    private LocalDateTime generationSuccessTS;
    @Column( name = "generation_failed_ts" )
    private LocalDateTime generationFailedTS;
    @Column( name = "generation_canceled_ts" )
    private LocalDateTime generationCanceledTS;
    @Column( name = "consumption_state" )
    private int consumptionState = 100;
    @Column( name = "consumption_index" )
    private int consumptionIndex;
    @Column( name = "consumption_planned_ts" )
    private LocalDateTime consumptionPlannedTS;
    @Column( name = "consumption_in_progress_ts" )
    private LocalDateTime consumptionInProgressTS;
    @Column( name = "consumption_success_ts" )
    private LocalDateTime consumptionSuccessTS;
    @Column( name = "consumption_failed_ts" )
    private LocalDateTime consumptionFailedTS;
    @Column( name = "consumption_canceledTS" )
    private LocalDateTime consumptionCanceledTS;
    @Column( name = "source", length = 30 )
    private String source;
    @ManyToOne
    @JoinColumn( name = "generating_operation_oid" )
    private Operation generatingOperation;
    @ManyToOne
    @JoinColumn( name = "consuming_operation_oid" )
    private Operation consumingOperation;
    @ManyToOne
    @JoinColumn( name = "material_type_oid" )
    private MaterialType materialType;
    @OneToMany( mappedBy = "material" )
    private Set<MaterialLock> locks;
    @OneToMany( mappedBy = "material" )
    private Set<MaterialCharacteristic> characteristics;
    @ManyToOne
    @JoinColumn( name = "alloy_oid" )
    private Alloy alloy;
    @ManyToOne
    @JoinColumn( name = "analysis_oid" )
    private Analysis analysis;

    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "parent_oid" )
    private Material parent;

    @ManyToOne
    @JoinColumn( name = "handling_unit_oid" )
    private HandlingUnit handlingUnit;

    public String getName()
    {
        return name;
    }

    public void setName( String newName )
    {
        this.name = newName;
    }

    public double getWeight()
    {
        return weight;
    }

    public void setWeight( double newWeight )
    {
        this.weight = newWeight;
    }

    public int getGenerationState()
    {
        return generationState;
    }

    public void setGenerationState( int newGenerationState )
    {
        this.generationState = newGenerationState;
    }

    public int getGenerationIndex()
    {
        return generationIndex;
    }

    public void setGenerationIndex( int newGenerationIndex )
    {
        this.generationIndex = newGenerationIndex;
    }

    public LocalDateTime getGenerationPlannedTS()
    {
        return generationPlannedTS;
    }

    public void setGenerationPlannedTS( LocalDateTime newGenerationPlannedTS )
    {
        this.generationPlannedTS = newGenerationPlannedTS;
    }

    public LocalDateTime getGenerationInProgressTS()
    {
        return generationInProgressTS;
    }

    public void setGenerationInProgressTS( LocalDateTime newGenerationInProgressTS )
    {
        this.generationInProgressTS = newGenerationInProgressTS;
    }

    public LocalDateTime getGenerationSuccessTS()
    {
        return generationSuccessTS;
    }

    public void setGenerationSuccessTS( LocalDateTime newGenerationSuccessTS )
    {
        this.generationSuccessTS = newGenerationSuccessTS;
    }

    public LocalDateTime getGenerationFailedTS()
    {
        return generationFailedTS;
    }

    public void setGenerationFailedTS( LocalDateTime newGenerationFailedTS )
    {
        this.generationFailedTS = newGenerationFailedTS;
    }

    public LocalDateTime getGenerationCanceledTS()
    {
        return generationCanceledTS;
    }

    public void setGenerationCanceledTS( LocalDateTime newGenerationCanceledTS )
    {
        this.generationCanceledTS = newGenerationCanceledTS;
    }

    public int getConsumptionState()
    {
        return consumptionState;
    }

    public void setConsumptionState( int newConsumptionState )
    {
        this.consumptionState = newConsumptionState;
    }

    public int getConsumptionIndex()
    {
        return consumptionIndex;
    }

    public void setConsumptionIndex( int newConsumptionIndex )
    {
        this.consumptionIndex = newConsumptionIndex;
    }

    public LocalDateTime getConsumptionPlannedTS()
    {
        return consumptionPlannedTS;
    }

    public void setConsumptionPlannedTS( LocalDateTime newConsumptionPlannedTS )
    {
        this.consumptionPlannedTS = newConsumptionPlannedTS;
    }

    public LocalDateTime getConsumptionInProgressTS()
    {
        return consumptionInProgressTS;
    }

    public void setConsumptionInProgressTS( LocalDateTime newConsumptionInProgressTS )
    {
        this.consumptionInProgressTS = newConsumptionInProgressTS;
    }

    public LocalDateTime getConsumptionSuccessTS()
    {
        return consumptionSuccessTS;
    }

    public void setConsumptionSuccessTS( LocalDateTime newConsumptionSuccessTS )
    {
        this.consumptionSuccessTS = newConsumptionSuccessTS;
    }

    public LocalDateTime getConsumptionFailedTS()
    {
        return consumptionFailedTS;
    }

    public void setConsumptionFailedTS( LocalDateTime newConsumptionFailedTS )
    {
        this.consumptionFailedTS = newConsumptionFailedTS;
    }

    public LocalDateTime getConsumptionCanceledTS()
    {
        return consumptionCanceledTS;
    }

    public void setConsumptionCanceledTS( LocalDateTime newConsumptionCanceledTS )
    {
        this.consumptionCanceledTS = newConsumptionCanceledTS;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource( String source )
    {
        this.source = source;
    }

    public Operation getGeneratingOperation()
    {
        return generatingOperation;
    }

    public void setGeneratingOperation( Operation newGeneratingOperation )
    {
        Operation oldGeneratingOperation = this.generatingOperation;
        this.generatingOperation = newGeneratingOperation;
        if ( oldGeneratingOperation != newGeneratingOperation )
        {
            if ( oldGeneratingOperation != null )
            {
                oldGeneratingOperation.oneSided_removeFromGeneratedMaterials( this );
            }
            if ( newGeneratingOperation != null )
            {
                newGeneratingOperation.oneSided_addToGeneratedMaterials( this );
            }
        }
    }

    public void oneSided_setGeneratingOperation( Operation newGeneratingOperation )
    {
        this.generatingOperation = newGeneratingOperation;
    }

    public Operation getConsumingOperation()
    {
        return consumingOperation;
    }

    public void setConsumingOperation( Operation newConsumingOperation )
    {
        Operation oldConsumingOperation = this.consumingOperation;
        this.consumingOperation = newConsumingOperation;
        if ( oldConsumingOperation != newConsumingOperation )
        {
            if ( oldConsumingOperation != null )
            {
                oldConsumingOperation.oneSided_removeFromConsumedMaterials( this );
            }
            if ( newConsumingOperation != null )
            {
                newConsumingOperation.oneSided_addToConsumedMaterials( this );
            }
        }
    }

    public void oneSided_setConsumingOperation( Operation newConsumingOperation )
    {
        this.consumingOperation = newConsumingOperation;
    }

    public MaterialType getMaterialType()
    {
        return materialType;
    }

    public void setMaterialType( MaterialType newType )
    {
        this.materialType = newType;
    }

    public Set<MaterialLock> getLocks()
    {
        if ( locks == null )
        {
            locks = new HashSet<MaterialLock>();
        }
        return locks;
    }

    public boolean containsInLocks( MaterialLock materialLock )
    {
        return ( this.locks != null ) && this.locks.contains( materialLock );
    }

    public int numberOfLocks()
    {
        return ( locks == null ) ? 0 : locks.size();
    }

    public void flushLocks()
    {
        if ( locks != null )
        {
            Iterator<MaterialLock> iter = locks.iterator();
            while ( iter.hasNext() )
            {
                MaterialLock c = iter.next();
                c.oneSided_setMaterial( null );
                c.removeAllAssociations();
            }
            locks.clear();
        }
    }

    public void addToLocks( MaterialLock materialLock )
    {
        getLocks().add( materialLock );
        Material oldMaterial = materialLock.getMaterial();
        if ( ( oldMaterial != this ) && ( oldMaterial != null ) )
        {
            oldMaterial.oneSided_removeFromLocks( materialLock );
        }
        materialLock.oneSided_setMaterial( this );
    }

    public void removeFromLocks( MaterialLock materialLock )
    {
        getLocks().remove( materialLock );
        materialLock.oneSided_setMaterial( null );
    }

    public void oneSided_addToLocks( MaterialLock materialLock )
    {
        getLocks().add( materialLock );
    }

    public void oneSided_removeFromLocks( MaterialLock materialLock )
    {
        getLocks().remove( materialLock );
    }

    public Set<MaterialCharacteristic> getCharacteristics()
    {
        if ( characteristics == null )
        {
            characteristics = new HashSet<MaterialCharacteristic>();
        }
        return characteristics;
    }

    public boolean containsInCharacteristics( MaterialCharacteristic materialCharacteristic )
    {
        return ( this.characteristics != null ) && this.characteristics.contains( materialCharacteristic );
    }

    public int numberOfCharacteristics()
    {
        return ( characteristics == null ) ? 0 : characteristics.size();
    }

    public void flushCharacteristics()
    {
        if ( characteristics != null )
        {
            Iterator<MaterialCharacteristic> iter = characteristics.iterator();
            while ( iter.hasNext() )
            {
                MaterialCharacteristic c = iter.next();
                c.oneSided_setMaterial( null );
                c.removeAllAssociations();
            }
            characteristics.clear();
        }
    }

    public void addToCharacteristics( MaterialCharacteristic materialCharacteristic )
    {
        getCharacteristics().add( materialCharacteristic );
        Material oldMaterial = materialCharacteristic.getMaterial();
        if ( ( oldMaterial != this ) && ( oldMaterial != null ) )
        {
            oldMaterial.oneSided_removeFromCharacteristics( materialCharacteristic );
        }
        materialCharacteristic.oneSided_setMaterial( this );
    }

    public void removeFromCharacteristics( MaterialCharacteristic materialCharacteristic )
    {
        getCharacteristics().remove( materialCharacteristic );
        materialCharacteristic.oneSided_setMaterial( null );
    }

    public void oneSided_addToCharacteristics( MaterialCharacteristic materialCharacteristic )
    {
        getCharacteristics().add( materialCharacteristic );
    }

    public void oneSided_removeFromCharacteristics( MaterialCharacteristic materialCharacteristic )
    {
        getCharacteristics().remove( materialCharacteristic );
    }

    public Analysis getAnalysis()
    {
        return analysis;
    }

    public void setAnalysis( Analysis newAnalysis )
    {
        this.analysis = newAnalysis;
    }

    public Alloy getAlloy()
    {
        return alloy;
    }

    public void setAlloy( Alloy alloy )
    {
        this.alloy = alloy;
    }

    public void oneSided_setAnalysis( Analysis newAnalysis )
    {
        this.analysis = newAnalysis;
    }

    public Material getParent()
    {
        return parent;
    }

    public void setParent( Material parent )
    {
        this.parent = parent;
    }

    public HandlingUnit getHandlingUnit()
    {
        return handlingUnit;
    }

    public void setHandlingUnit( HandlingUnit newHandlingUnit )
    {
        HandlingUnit oldHandlingUnit = this.handlingUnit;
        this.handlingUnit = newHandlingUnit;
        if ( oldHandlingUnit != newHandlingUnit )
        {
            if ( oldHandlingUnit != null )
            {
                oldHandlingUnit.oneSided_removeFromMaterials( this );
            }
            if ( newHandlingUnit != null )
            {
                newHandlingUnit.oneSided_addToMaterials( this );
            }
        }
    }

    public void oneSided_setHandlingUnit( HandlingUnit newHandlingUnit )
    {
        this.handlingUnit = newHandlingUnit;
    }

    public void assignChild( Material child )
    {
        //child.setName( getName() );
        child.setSource( getSource() );
        child.setMaterialType( getMaterialType() );
        child.setAnalysis( getAnalysis() );
        child.setAlloy( getAlloy() );
        child.setParent( this );
    }

    public void removeAllAssociations()
    {
        setGeneratingOperation( null );
        setConsumingOperation( null );
        setMaterialType( null );
        setAnalysis( null );
        setAlloy( null );
        setParent( null );
        flushLocks();
        flushCharacteristics();
        setHandlingUnit( null );
    }
}