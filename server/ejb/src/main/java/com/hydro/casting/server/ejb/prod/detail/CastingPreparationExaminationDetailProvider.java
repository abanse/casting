package com.hydro.casting.server.ejb.prod.detail;

import com.hydro.casting.server.contract.dto.CastingPreparationExaminationDTO;
import com.hydro.casting.server.contract.dto.ProcessDocuDTO;
import com.hydro.casting.server.model.inspection.InspectionCategory;
import com.hydro.casting.server.model.inspection.dao.InspectionCategoryHome;
import com.hydro.core.server.contract.workplace.DetailProvider;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Map;
@Stateless( name = "CastingPreparationExaminationDetailProvider" )
public class CastingPreparationExaminationDetailProvider extends InspectionDetailProvider<ProcessDocuDTO, CastingPreparationExaminationDTO>
        implements DetailProvider<ProcessDocuDTO, CastingPreparationExaminationDTO>
{
    @EJB
    private InspectionCategoryHome inspectionCategoryHome;

    @Override
    public CastingPreparationExaminationDTO loadDetail( ProcessDocuDTO master, Map<String, String> context )
    {
        CastingPreparationExaminationDTO castingPreparationExaminationDTO = new CastingPreparationExaminationDTO();
        InspectionCategory inspectionCategory = inspectionCategoryHome.findByApk( CastingPreparationExaminationDTO.CATEGORY_APK );
        return loadInspectionDetails( master, castingPreparationExaminationDTO, inspectionCategory, context );
    }
}
