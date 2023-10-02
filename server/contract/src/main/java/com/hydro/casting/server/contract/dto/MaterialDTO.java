package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MaterialDTO implements ViewDTO
{

    public enum Shaping
    {PlannedConsumed, Consumed}

    private static final long serialVersionUID = 1L;

    private long id;

    private Shaping shaping;
    private String type;
    private String name;
    private String group;
    private String source;
    private double weight;
    private Long consumedOperation;
    private Double unitWeight;

    private Long analysisObjid;
    private String analysisName;
    private List<MaterialAnalysisElementDTO> materialAnalysisElements;

    private String place;

    private LocalDateTime generationSuccessTS;

    private String materialTypeApk;
    private String materialTypeDescription;
    private String alloyName;

    private int generationState;
    private int consumptionState;

    private String tags;

    private String deliveryCharge;

    @Override
    public long getId()
    {
        return id;
    }

    public void setId( long id )
    {
        this.id = id;
    }

    public Shaping getShaping()
    {
        return shaping;
    }

    public void setShaping( Shaping shaping )
    {
        this.shaping = shaping;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getGroup()
    {
        return group;
    }

    public void setGroup( String group )
    {
        this.group = group;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource( String source )
    {
        this.source = source;
    }

    public double getWeight()
    {
        return weight;
    }

    public void setWeight( double weight )
    {
        this.weight = weight;
    }

    public Long getConsumedOperation()
    {
        return consumedOperation;
    }

    public void setConsumedOperation( Long consumedOperation )
    {
        this.consumedOperation = consumedOperation;
    }

    public Double getUnitWeight()
    {
        return unitWeight;
    }

    public void setUnitWeight( Double unitWeight )
    {
        this.unitWeight = unitWeight;
    }

    public Long getAnalysisObjid()
    {
        return analysisObjid;
    }

    public void setAnalysisObjid( Long analysisObjid )
    {
        this.analysisObjid = analysisObjid;
    }

    public String getAnalysisName()
    {
        return analysisName;
    }

    public void setAnalysisName( String analysisName )
    {
        this.analysisName = analysisName;
    }

    public List<MaterialAnalysisElementDTO> getMaterialAnalysisElements()
    {
        return materialAnalysisElements;
    }

    public void setMaterialAnalysisElements( List<MaterialAnalysisElementDTO> materialAnalysisElements )
    {
        this.materialAnalysisElements = materialAnalysisElements;
    }

    public String getPlace()
    {
        return place;
    }

    public void setPlace( String place )
    {
        this.place = place;
    }

    public LocalDateTime getGenerationSuccessTS()
    {
        return generationSuccessTS;
    }

    public void setGenerationSuccessTS( LocalDateTime generationSuccessTS )
    {
        this.generationSuccessTS = generationSuccessTS;
    }

    public String getMaterialTypeApk()
    {
        return materialTypeApk;
    }

    public void setMaterialTypeApk( String materialTypeApk )
    {
        this.materialTypeApk = materialTypeApk;
    }

    public String getMaterialTypeDescription()
    {
        return materialTypeDescription;
    }

    public void setMaterialTypeDescription( String materialTypeDescription )
    {
        this.materialTypeDescription = materialTypeDescription;
    }

    public String getAlloyName()
    {
        return alloyName;
    }

    public void setAlloyName( String alloyName )
    {
        this.alloyName = alloyName;
    }

    public int getGenerationState()
    {
        return generationState;
    }

    public void setGenerationState( int generationState )
    {
        this.generationState = generationState;
    }

    public int getConsumptionState()
    {
        return consumptionState;
    }

    public void setConsumptionState( int consumptionState )
    {
        this.consumptionState = consumptionState;
    }

    public String getTags()
    {
        return tags;
    }

    public void setTags( String tags )
    {
        this.tags = tags;
    }

    public String getDeliveryCharge()
    {
        return deliveryCharge;
    }

    public void setDeliveryCharge( String deliveryCharge )
    {
        this.deliveryCharge = deliveryCharge;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( !( o instanceof MaterialDTO ) )
            return false;
        MaterialDTO that = (MaterialDTO) o;
        return id == that.id && Double.compare( that.weight, weight ) == 0 && generationState == that.generationState && consumptionState == that.consumptionState && shaping == that.shaping
                && Objects.equals( type, that.type ) && Objects.equals( name, that.name ) && Objects.equals( group, that.group ) && Objects.equals( source, that.source ) && Objects.equals(
                consumedOperation, that.consumedOperation ) && Objects.equals( unitWeight, that.unitWeight ) && Objects.equals( analysisObjid, that.analysisObjid ) && Objects.equals( analysisName,
                that.analysisName ) && Objects.equals( materialAnalysisElements, that.materialAnalysisElements ) && Objects.equals( place, that.place ) && Objects.equals( generationSuccessTS,
                that.generationSuccessTS ) && Objects.equals( materialTypeApk, that.materialTypeApk ) && Objects.equals( materialTypeDescription, that.materialTypeDescription ) && Objects.equals(
                alloyName, that.alloyName ) && Objects.equals( tags, that.tags ) && Objects.equals( deliveryCharge, that.deliveryCharge );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, shaping, type, name, group, source, weight, consumedOperation, unitWeight, analysisObjid, analysisName, materialAnalysisElements, place, generationSuccessTS,
                materialTypeApk, materialTypeDescription, alloyName, generationState, consumptionState, tags, deliveryCharge );
    }

    @Override
    public String toString()
    {
        return "MaterialDTO{" + "id=" + id + ", shaping=" + shaping + ", type='" + type + '\'' + ", name='" + name + '\'' + ", group='" + group + '\'' + ", source='" + source + '\'' + ", weight="
                + weight + ", consumedOperation=" + consumedOperation + ", unitWeight=" + unitWeight + ", analysisObjid=" + analysisObjid + ", analysisName='" + analysisName + '\''
                + ", materialAnalysisElements=" + materialAnalysisElements + ", place='" + place + '\'' + ", generationSuccessTS=" + generationSuccessTS + ", materialTypeApk='" + materialTypeApk
                + '\'' + ", materialTypeDescription='" + materialTypeDescription + '\'' + ", alloyName='" + alloyName + '\'' + ", generationState=" + generationState + ", consumptionState="
                + consumptionState + ", tags='" + tags + '\'' + ", deliveryCharge='" + deliveryCharge + '\'' + '}';
    }

    public MaterialDTO clone()
    {
        final MaterialDTO clone = new MaterialDTO();
        clone.setId( getId() );
        clone.setShaping( getShaping() );
        clone.setType( getType() );
        clone.setName( getName() );
        clone.setGroup( getGroup() );
        clone.setSource( getSource() );
        clone.setWeight( getWeight() );
        clone.setConsumedOperation( getConsumedOperation() );
        clone.setUnitWeight( getUnitWeight() );
        clone.setAnalysisObjid( getAnalysisObjid() );
        clone.setAnalysisName( getAnalysisName() );
        clone.setPlace( getPlace() );
        clone.setGenerationSuccessTS( getGenerationSuccessTS() );
        clone.setMaterialTypeApk( getMaterialTypeApk() );
        clone.setMaterialTypeDescription( getMaterialTypeDescription() );
        clone.setAlloyName( getAlloyName() );
        clone.setGenerationState( getGenerationState() );
        clone.setConsumptionState( getConsumptionState() );
        clone.setTags( getTags() );
        clone.setDeliveryCharge( getDeliveryCharge() );

        if ( materialAnalysisElements != null )
        {
            final List<MaterialAnalysisElementDTO> cloneMaterialAnalysisElements = new ArrayList<>();
            materialAnalysisElements.forEach( materialAnalysisElementDTO -> cloneMaterialAnalysisElements.add( materialAnalysisElementDTO.clone() ) );
            clone.setMaterialAnalysisElements( cloneMaterialAnalysisElements );
        }

        return clone;
    }
}