package com.hydro.casting.server.ejb.prod.detail;

import com.hydro.casting.server.contract.dto.CastingPreparationDTO;
import com.hydro.casting.server.contract.dto.ProcessDocuDTO;
import com.hydro.casting.server.model.inspection.InspectionCategory;
import com.hydro.casting.server.model.inspection.dao.InspectionCategoryHome;
import com.hydro.core.server.contract.workplace.DetailProvider;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Map;

@Stateless( name = "CastingPreparationDetailProvider" )
public class CastingPreparationDetailProvider extends InspectionDetailProvider<ProcessDocuDTO, CastingPreparationDTO>
        implements DetailProvider<com.hydro.casting.server.contract.dto.ProcessDocuDTO, com.hydro.casting.server.contract.dto.CastingPreparationDTO>
{
    @EJB
    private InspectionCategoryHome inspectionCategoryHome;

    @Override
    public CastingPreparationDTO loadDetail( ProcessDocuDTO master, Map<String, String> context )
    {
        CastingPreparationDTO castingPreparationDTO = new CastingPreparationDTO();
        InspectionCategory inspectionCategory = inspectionCategoryHome.findByApk( CastingPreparationDTO.CATEGORY_APK );
        return loadInspectionDetails( master, castingPreparationDTO, inspectionCategory, context );
    }
}
