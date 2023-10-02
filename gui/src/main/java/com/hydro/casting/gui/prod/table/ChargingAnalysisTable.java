package com.hydro.casting.gui.prod.table;

import com.google.inject.Inject;
import com.hydro.casting.common.Casting;
import com.hydro.casting.gui.analysis.control.AnalysisTable;
import com.hydro.casting.gui.prod.task.LoadAnalysisDetailTask;
import com.hydro.casting.server.contract.dto.AnalysisDTO;
import com.hydro.casting.server.contract.dto.CastingInstructionDTO;
import com.hydro.casting.server.contract.dto.FurnaceInstructionDTO;
import com.hydro.casting.server.contract.dto.MeltingInstructionDTO;
import com.hydro.core.common.cache.ClientCache;
import com.hydro.core.gui.CacheManager;
import com.hydro.core.gui.TaskManager;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChargingAnalysisTable extends AnalysisTable
{
    private final static Logger log = LoggerFactory.getLogger( ChargingAnalysisTable.class );

    private CastingInstructionDTO castingInstruction;
    private MeltingInstructionDTO meltingInstruction;

    private boolean active = false;

    private CacheManager cacheManager;

    private List<AnalysisDTO> checkAnalysisList;

    @Inject
    private LoadAnalysisDetailTask loadAnalysisDetailTask;

    @Inject
    private TaskManager taskManager;

    private boolean withTimestampInName = false;
    private final StringProperty altAlloy = new SimpleStringProperty( null );
    private final BooleanProperty compressAnalysis = new SimpleBooleanProperty( false );
    private boolean oldCompressAnalysis = true;

    public ChargingAnalysisTable()
    {
        weight.setVisible( false );
        name.setPrefWidth( 270 );
    }

    public boolean isCompressAnalysis()
    {
        return compressAnalysis.get();
    }

    public BooleanProperty compressAnalysisProperty()
    {
        return compressAnalysis;
    }

    public void setCompressAnalysis( boolean compressAnalysis )
    {
        this.compressAnalysis.set( compressAnalysis );
    }

    public void connect( CacheManager cacheManager )
    {
        this.cacheManager = cacheManager;

        cacheManager.addCacheListener( Casting.CACHE.PLANNING_CACHE_NAME, Casting.CACHE.PLANNING_VERSION_CACHE_NAME, key -> {
            if ( active )
            {
                reloadFromCache();
            }
        } );
    }

    public void setActive( boolean active )
    {
        this.active = active;
        if ( active )
        {
            reloadFromCache();
        }
    }

    public void reload()
    {
        reloadFromCache( true );
    }

    public boolean isWithTimestampInName()
    {
        return withTimestampInName;
    }

    public void setWithTimestampInName( boolean withTimestampInName )
    {
        this.withTimestampInName = withTimestampInName;
    }

    public void setCastingInstruction( CastingInstructionDTO castingInstruction )
    {
        this.meltingInstruction = null;
        this.castingInstruction = castingInstruction;
        reloadFromCache();
    }

    public void setMeltingInstructionDTO( MeltingInstructionDTO meltingInstructionDTO )
    {
        this.castingInstruction = null;
        this.meltingInstruction = meltingInstructionDTO;
        reloadFromCache();
    }

    public StringProperty altAlloyProperty()
    {
        return altAlloy;
    }

    private void reloadFromCache()
    {
        reloadFromCache( false );
    }

    private void reloadFromCache( boolean forceReloadFromServer )
    {
        String instructionChargeCounter;
        // Setting chargeCounter to last 2 digits from either casting or melting instruction, or returns if none is set
        if ( castingInstruction == null || castingInstruction.getCharge() == null || castingInstruction.getCharge().length() <= 2 )
        {
            if ( meltingInstruction == null || meltingInstruction.getCharge() == null || meltingInstruction.getCharge().length() <= 2 )
            {
                getItems().clear();
                checkAnalysisList = null;
                return;
            }

            instructionChargeCounter = meltingInstruction.getCharge().substring( 2 );
        }
        else
        {
            instructionChargeCounter = castingInstruction.getCharge().substring( 2 );
        }

        final ClientCache<List<Long>> sequenceClientCache = cacheManager.getCache( Casting.CACHE.PLANNING_CACHE_NAME );
        final List<Long> sequence = sequenceClientCache.get( Casting.CACHE.ANALYSIS_PATH );
        if ( sequence == null || sequence.isEmpty() )
        {
            getItems().clear();
            return;
        }
        final List<AnalysisDTO> currentAnalysis = new ArrayList<>();
        final ClientCache<AnalysisDTO> dataClientCache = cacheManager.getCache( Casting.CACHE.PLANNING_CACHE_NAME );
        final Map<Long, AnalysisDTO> rawData = dataClientCache.getAll( Casting.CACHE.ANALYSIS_PATH + "/data/", sequence );
        for ( Long id : sequence )
        {
            final AnalysisDTO analysisDTO = rawData.get( id );
            if ( analysisDTO != null && analysisDTO.getCharge() != null && analysisDTO.getCharge().endsWith( instructionChargeCounter ) )
            {
                currentAnalysis.add( analysisDTO );
            }
        }

        if ( checkAnalysisList == null || !Objects.deepEquals( checkAnalysisList, currentAnalysis ) || compressAnalysis.get() != oldCompressAnalysis || forceReloadFromServer )
        {
            loadCacheDetails( currentAnalysis );
        }
        oldCompressAnalysis = compressAnalysis.get();
        checkAnalysisList = currentAnalysis;
    }

    private void loadCacheDetails( List<AnalysisDTO> analysisDTOS )
    {
        if ( analysisDTOS.isEmpty() )
        {
            return;
        }
        final AnalysisDTO firstAnalysisDTO = analysisDTOS.get( 0 );

        final AnalysisDTO masterAnalysis = new AnalysisDTO();
        masterAnalysis.setName( firstAnalysisDTO.getName() );
        masterAnalysis.setCharge( firstAnalysisDTO.getCharge() );
        masterAnalysis.setYear( firstAnalysisDTO.getYear() );

        boolean isCasting = castingInstruction != null;
        boolean isFurnace = isCasting && castingInstruction instanceof FurnaceInstructionDTO;
        if ( altAlloy.get() != null )
        {
            loadAnalysisDetailTask.setData( masterAnalysis, this, isFurnace, withTimestampInName, isCasting, compressAnalysis.get(), altAlloy.get() );
        }
        else
        {
            loadAnalysisDetailTask.setData( masterAnalysis, this, isFurnace, withTimestampInName, isCasting, compressAnalysis.get() );
        }
        taskManager.executeTask( loadAnalysisDetailTask );
    }
}
