package com.hydro.casting.server.ejb.prod.detail;

import com.hydro.casting.server.contract.dto.EquipmentConditionDTO;
import com.hydro.casting.server.contract.dto.ProcessDocuDTO;
import com.hydro.casting.server.model.inspection.InspectionCategory;
import com.hydro.casting.server.model.inspection.dao.InspectionCategoryHome;
import com.hydro.core.server.contract.workplace.DetailProvider;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Map;

@Stateless( name = "EquipmentConditionProvider" )
public class EquipmentConditionProvider extends InspectionDetailProvider<ProcessDocuDTO, EquipmentConditionDTO> implements DetailProvider<ProcessDocuDTO, EquipmentConditionDTO>
{
    @EJB
    private InspectionCategoryHome inspectionCategoryHome;

    @Override
    public EquipmentConditionDTO loadDetail( ProcessDocuDTO master, Map<String, String> context )
    {
        EquipmentConditionDTO equipmentConditionDTO = new EquipmentConditionDTO();
        InspectionCategory inspectionCategory = inspectionCategoryHome.findByApk( EquipmentConditionDTO.CATEGORY_APK );
        return loadInspectionDetails( master, equipmentConditionDTO, inspectionCategory, context );
    }
}
