package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * DTO used to store and transport a composition from the server to the client. A composition can be the min / max composition for a specification, or an analysis. Is used in AnalysisDetailDTO.
 */
public class CompositionDTO implements ViewDTO
{
    private static final long serialVersionUID = 1;

    private long objid;
    private String name;
    private double weight;
    private double originalWeight;
    private int sampleNumber;
    private LocalDateTime sampleTS;
    private List<CompositionElementDTO> compositionElementDTOList = new ArrayList<>();

    @Override
    public long getId()
    {
        return this.hashCode();
    }

    public long getObjid()
    {
        return objid;
    }

    public void setObjid( long objid )
    {
        this.objid = objid;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public List<CompositionElementDTO> getCompositionElementDTOList()
    {
        return compositionElementDTOList;
    }

    public double getWeight()
    {
        return weight;
    }

    public void setWeight( double weight )
    {
        this.weight = weight;
    }

    public double getOriginalWeight()
    {
        return originalWeight;
    }

    public void setOriginalWeight( double originalWeight )
    {
        this.originalWeight = originalWeight;
    }

    public int getSampleNumber()
    {
        return sampleNumber;
    }

    public void setSampleNumber( int sampleNumber )
    {
        this.sampleNumber = sampleNumber;
    }

    public LocalDateTime getSampleTS()
    {
        return sampleTS;
    }

    public void setSampleTS( LocalDateTime sampleTS )
    {
        this.sampleTS = sampleTS;
    }

    public void setCompositionElementDTOList( List<CompositionElementDTO> compositionElementDTOList )
    {
        this.compositionElementDTOList = compositionElementDTOList;
    }

    public void addToCompositionElementDTOList( CompositionElementDTO compositionElementDTO )
    {
        this.compositionElementDTOList.add( compositionElementDTO );
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( !( o instanceof CompositionDTO ) )
            return false;
        CompositionDTO that = (CompositionDTO) o;
        return that.objid == objid && Double.compare( that.weight, weight ) == 0 && Double.compare( that.originalWeight, originalWeight ) == 0 && sampleNumber == that.sampleNumber && Objects.equals( name, that.name )
                && Objects.equals( sampleTS, that.sampleTS ) && Objects.equals( compositionElementDTOList, that.compositionElementDTOList );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( objid, name, weight, originalWeight, sampleNumber, sampleTS, compositionElementDTOList );
    }

    @Override
    public String toString()
    {
        return "CompositionDTO{" +"objid=" +objid + ", name='" + name + '\'' + ", weight=" + weight + ", originalWeight=" + originalWeight + ", sampleNumber=" + sampleNumber + ", sampleTS=" + sampleTS
                + ", compositionElementDTOList=" + compositionElementDTOList + '}';
    }

    @Override
    public CompositionDTO clone()
    {
        final CompositionDTO clone = new CompositionDTO();
        clone.setObjid( getObjid() );
        clone.setName( getName() );
        clone.setWeight( getWeight() );
        clone.setOriginalWeight( getOriginalWeight() );
        clone.setSampleNumber( getSampleNumber() );
        clone.setSampleTS( getSampleTS() );
        if ( getCompositionElementDTOList() != null )
        {
            final List<CompositionElementDTO> elements = new ArrayList<>();
            getCompositionElementDTOList().forEach( compositionElementDTO -> elements.add( compositionElementDTO.clone() ) );

            clone.setCompositionElementDTOList( elements );
        }

        return clone;
    }
}
