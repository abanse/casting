package com.hydro.casting.server.ejb.analysis.detail;

import com.hydro.casting.server.contract.dto.AnalysisDTO;
import com.hydro.casting.server.contract.dto.AnalysisDetailDTO;
import com.hydro.casting.server.ejb.analysis.service.AnalysisService;
import com.hydro.casting.server.model.mat.Analysis;
import com.hydro.core.server.contract.workplace.DetailProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Stateless( name = "AnalysisDetailProvider" )
public class AnalysisDetailProvider implements DetailProvider<AnalysisDTO, AnalysisDetailDTO>
{
    private final static Logger log = LoggerFactory.getLogger( AnalysisDetailProvider.class );

    @EJB
    AnalysisService analysisService;

    @Override
    public AnalysisDetailDTO loadDetail( AnalysisDTO master, Map<String, String> context )
    {
        log.trace( "loadDetail" );
        final long start = System.currentTimeMillis();
        List<Analysis> analysisList;
        boolean isLeaf = true;
        String name = master.getName();

        if ( master.getPreregistrationTime() != null )
        {
            // If the master dto has a preregistration time, it specified an individual sample, so we only retrieve that specific sample analysis
            Analysis analysis = analysisService.findAnalysisById( master.getAnalysisId() );
            analysisList = Collections.singletonList( analysis );
            name += " " + analysis.getSampleNumber();
        }
        else
        {
            // If the master dto has no preregistration time, it does not specify an individual sample, so we retrieve all sample analysis that match the master dto
            analysisList = analysisService.findAllAnalysesForChargeAndYear( master.getCharge(), master.getYear() );
            // isLeaf is relevant to determine if the AnalysisDetailDTO contains only data for one sample or for multiple samples later on
            isLeaf = false;
        }

        String specificationAlloy = context.getOrDefault( "alloy", null );
        final AnalysisDetailDTO analysisDetailDTO = analysisService.loadFullAnalysisDetail( analysisList, name, specificationAlloy, isLeaf );
        log.trace( "loadDetail end " + ( System.currentTimeMillis() - start ) );
        return analysisDetailDTO;
    }
}
