package com.hydro.casting.server.ejb.prod.detail;

import com.hydro.casting.server.contract.dto.ProcessDocuDTO;
import com.hydro.casting.server.contract.dto.VisualInspectionDTO;
import com.hydro.casting.server.model.inspection.InspectionCategory;
import com.hydro.casting.server.model.inspection.dao.InspectionCategoryHome;
import com.hydro.core.server.contract.workplace.DetailProvider;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Map;

@Stateless( name = "VisualInspectionDetailProvider" )
public class VisualInspectionDetailProvider extends InspectionDetailProvider<ProcessDocuDTO, VisualInspectionDTO> implements DetailProvider<ProcessDocuDTO, VisualInspectionDTO>
{
    @EJB
    private InspectionCategoryHome inspectionCategoryHome;

    @Override
    public VisualInspectionDTO loadDetail( ProcessDocuDTO master, Map<String, String> context )
    {
        VisualInspectionDTO visualInspectionDTO = new VisualInspectionDTO();
        InspectionCategory inspectionCategory = inspectionCategoryHome.findByApk( VisualInspectionDTO.CATEGORY_APK );
        return loadInspectionDetails( master, visualInspectionDTO, inspectionCategory, context );
    }
}
