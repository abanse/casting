package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.util.List;
import java.util.Objects;

/**
 * DTO used to store and transport information related to analysis details displayed when selecting a header or child element in AnalysisTable. Always contains a specification (containing a min
 * composition and a max composition, might contain one or more analyses (each analysis is a composition), depending on the use case.
 */
public class AnalysisDetailDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;
    private String name;
    private CompositionDTO minComp;
    private CompositionDTO maxComp;
    private boolean isLeaf;
    private List<CompositionDTO> analysisList;

    @Override
    public long getId()
    {
        return this.hashCode();
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public CompositionDTO getMinComp()
    {
        return minComp;
    }

    public void setMinComp( CompositionDTO minComp )
    {
        this.minComp = minComp;
    }

    public CompositionDTO getMaxComp()
    {
        return maxComp;
    }

    public void setMaxComp( CompositionDTO maxComp )
    {
        this.maxComp = maxComp;
    }

    public List<CompositionDTO> getAnalysisList()
    {
        return analysisList;
    }

    public void  setAnalysisList( List<CompositionDTO> analysisList )
    {
        this.analysisList = analysisList;
    }

    public boolean isLeaf()
    {
        return isLeaf;
    }

    public void setIsLeaf( boolean isLeaf )
    {
        this.isLeaf = isLeaf;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( !( o instanceof AnalysisDetailDTO ) )
            return false;
        AnalysisDetailDTO that = (AnalysisDetailDTO) o;
        return isLeaf == that.isLeaf && Objects.equals( name, that.name ) && Objects.equals( minComp, that.minComp ) && Objects.equals( maxComp, that.maxComp ) && Objects.equals( analysisList,
                that.analysisList );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, minComp, maxComp, isLeaf, analysisList );
    }

    @Override
    public String toString()
    {
        return "AnalysisDetailDTO{" + "name='" + name + '\'' + ", minComp=" + minComp + ", maxComp=" + maxComp + ", isLeaf=" + isLeaf + ", analysisList=" + analysisList + '}';
    }
}
