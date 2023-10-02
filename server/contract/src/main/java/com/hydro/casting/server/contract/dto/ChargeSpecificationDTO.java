package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChargeSpecificationDTO implements ViewDTO, Cloneable
{
    private static final long serialVersionUID = 1L;
    private String charge;
    private String name;

    private List<SpecificationElementDTO> elements;

    @Override
    public long getId()
    {
        if ( charge == null )
        {
            return 0;
        }
        return charge.hashCode();
    }

    public String getCharge()
    {
        return charge;
    }

    public void setCharge( String charge )
    {
        this.charge = charge;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public List<SpecificationElementDTO> getElements()
    {
        return elements;
    }

    public void setElements( List<SpecificationElementDTO> elements )
    {
        this.elements = elements;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        ChargeSpecificationDTO that = (ChargeSpecificationDTO) o;
        return Objects.equals( charge, that.charge ) && Objects.equals( name, that.name ) && Objects.equals( elements, that.elements );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( charge, name, elements );
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        final ChargeSpecificationDTO clone = new ChargeSpecificationDTO();
        clone.setCharge( charge );
        clone.setName( name );
        if ( elements != null )
        {
            final ArrayList<SpecificationElementDTO> cloneElements = new ArrayList<>();
            for ( SpecificationElementDTO element : elements )
            {
                cloneElements.add( (SpecificationElementDTO) element.clone() );
            }
            clone.setElements( cloneElements );
        }

        return clone;
    }

    public SpecificationElementDTO getElement( String elementName )
    {
        if ( elements == null )
        {
            return null;
        }
        return elements.stream().filter( specificationElementDTO -> Objects.equals( specificationElementDTO.getName(), elementName ) ).findFirst().orElse( null );
    }
}
