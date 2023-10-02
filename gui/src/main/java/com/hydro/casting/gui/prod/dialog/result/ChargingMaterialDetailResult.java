package com.hydro.casting.gui.prod.dialog.result;

import com.hydro.casting.server.contract.dto.MaterialAnalysisElementDTO;

import java.util.List;
public class ChargingMaterialDetailResult
{
    private List<MaterialAnalysisElementDTO> elementDTOList;

    public List<MaterialAnalysisElementDTO> getElementDTOList()
    {
        return elementDTOList;
    }

    public void setElementDTOList( List<MaterialAnalysisElementDTO> elementDTOList )
    {
        this.elementDTOList = elementDTOList;
    }
}
