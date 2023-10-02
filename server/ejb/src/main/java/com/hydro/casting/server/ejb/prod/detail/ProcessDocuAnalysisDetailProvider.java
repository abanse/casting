package com.hydro.casting.server.ejb.prod.detail;

import com.hydro.casting.server.contract.dto.AnalysisDTO;
import com.hydro.casting.server.contract.dto.AnalysisDetailDTO;
import com.hydro.casting.server.contract.dto.ProcessDocuDTO;
import com.hydro.core.server.contract.workplace.DetailProvider;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.LocalDateTime;
import java.util.Map;

@Stateless( name = "ProcessDocuAnalysisDetailProvider" )
public class ProcessDocuAnalysisDetailProvider implements DetailProvider<ProcessDocuDTO, AnalysisDetailDTO>
{
    @EJB( beanName = "AnalysisDetailProvider" )
    private DetailProvider<AnalysisDTO, AnalysisDetailDTO> analysisDetailProvider;

    @Override
    public AnalysisDetailDTO loadDetail( ProcessDocuDTO master, Map<String, String> context )
    {
        // If the castingTS is not set, casting was not successfully finished yet. Thus casting is ongoing, and the current year can be used
        int year;
        if ( master.getCastingTS() != null )
        {
            year = master.getCastingTS().getYear();
        }
        else
        {
            year = LocalDateTime.now().getYear();
        }

        AnalysisDTO newMaster = new AnalysisDTO();
        newMaster.setCharge( master.getChargeWithoutYear() );
        newMaster.setYear( year );
        return analysisDetailProvider.loadDetail( newMaster, context );
    }
}
