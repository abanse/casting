package com.hydro.casting.server.contract.dto;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.util.List;
import java.util.Objects;

public class MaterialCharacteristicsDTO implements ViewDTO
{
    private static final long serialVersionUID = 1L;

    private List<MaterialCharacteristicDTO> materialCharacteristics;

    @Override
    public long getId()
    {
        return Objects.hash( materialCharacteristics );
    }

    public List<MaterialCharacteristicDTO> getMaterialCharacteristics()
    {
        return materialCharacteristics;
    }

    public void setMaterialCharacteristics( List<MaterialCharacteristicDTO> materialCharacteristics )
    {
        this.materialCharacteristics = materialCharacteristics;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( !( o instanceof MaterialCharacteristicsDTO ) )
            return false;
        MaterialCharacteristicsDTO that = (MaterialCharacteristicsDTO) o;
        return Objects.equals( materialCharacteristics, that.materialCharacteristics );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( materialCharacteristics );
    }

    @Override
    public String toString()
    {
        return "MaterialCharacteristicsListDTO{" + "materialCharacteristics=" + materialCharacteristics + '}';
    }
}