package com.hydro.casting.gui.prod.control;


import com.hydro.casting.common.constant.EMaterialCalcMode;
import com.hydro.casting.gui.model.Material;
import com.hydro.casting.gui.model.Transfer;

import java.util.List;

public interface MaterialsProvider
{
    List<Material> getCurrentMaterials();

    void clearMaterialCalculation();

    void setMaterialCalcMode( EMaterialCalcMode materialCalcMode, Transfer transfer );
}