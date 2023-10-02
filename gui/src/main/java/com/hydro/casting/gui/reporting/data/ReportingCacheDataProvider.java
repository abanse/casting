package com.hydro.casting.gui.reporting.data;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.reporting.dto.ReportingDTO;
import com.hydro.casting.server.contract.reporting.dto.ReportingDowntimeSummaryDTO;
import com.hydro.casting.server.contract.reporting.dto.ReportingGaugeSummaryDTO;
import com.hydro.casting.server.contract.reporting.dto.ReportingOutputSummaryDTO;
import com.hydro.core.common.cache.ClientCache;
import com.hydro.core.gui.CacheManager;

import java.util.Map;

@Singleton
public class ReportingCacheDataProvider
{
    @Inject
    CacheManager cacheManager;

    ClientCache<ReportingDTO> reportingSummaryCache;

    @Inject
    private void init()
    {
        reportingSummaryCache = cacheManager.getCache( Casting.CACHE.REPORTING_CACHE_NAME );
    }

    public ReportingDowntimeSummaryDTO getDowntimeSummary( String costCenter, String interval )
    {
        return (ReportingDowntimeSummaryDTO) reportingSummaryCache.get( Casting.CACHE.REPORTING_SUMMARY_DOWNTIME_KEY + "/" + costCenter + interval );
    }

    public ReportingOutputSummaryDTO getOutputSummary( String costCenter, String interval )
    {
        return (ReportingOutputSummaryDTO) reportingSummaryCache.get( Casting.CACHE.REPORTING_SUMMARY_OUTPUT_KEY + "/" + costCenter + interval );
    }

    public ReportingGaugeSummaryDTO getGaugeSummary( String costCenter, String interval )
    {
        return (ReportingGaugeSummaryDTO) reportingSummaryCache.get( Casting.CACHE.REPORTING_SUMMARY_GAUGE_KEY + "/" + costCenter + interval );
    }

    public ReportingGaugeSummaryDTO getGaugeSummaryFinishing( String interval, String[] costCenters )
    {
        ReportingGaugeSummaryDTO finishingSummaryDTO = new ReportingGaugeSummaryDTO();

        double downtimeValue = 0.;
        double outputValue = 0.;
        ReportingGaugeSummaryDTO summaryDTO = null;
        for ( String costCenter : costCenters )
        {
            summaryDTO = (ReportingGaugeSummaryDTO) reportingSummaryCache.get( Casting.CACHE.REPORTING_SUMMARY_GAUGE_KEY + "/" + costCenter + interval );
            if ( summaryDTO != null )
            {
                downtimeValue += summaryDTO.getDowntimeValue();
                outputValue += summaryDTO.getOutputValue();
            }
        }
        if ( summaryDTO != null )
        {
            finishingSummaryDTO.setStart( summaryDTO.getStart() );
            finishingSummaryDTO.setEnd( summaryDTO.getEnd() );
        }
        finishingSummaryDTO.setDowntimeValue( downtimeValue / costCenters.length );
        finishingSummaryDTO.setOutputValue( outputValue );

        return finishingSummaryDTO;
    }

    public Map<String, Integer> getKPIOutputTargets( String costCenter )
    {
        return (Map<String, Integer>) reportingSummaryCache.get( Casting.CACHE.REPORTING_SUMMARY_OUTPUT_TARGETS_KEY + "/" + costCenter );
    }
}
