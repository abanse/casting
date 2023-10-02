package com.hydro.casting.gui.prod.task;

import com.google.inject.Inject;
import com.hydro.casting.gui.analysis.control.AnalysisTable;
import com.hydro.casting.gui.analysis.util.AnalysisCompositionHelper;
import com.hydro.casting.server.contract.analysis.AnalysisManagementView;
import com.hydro.casting.server.contract.dto.AnalysisDTO;
import com.hydro.casting.server.contract.dto.AnalysisDetailDTO;
import com.hydro.casting.server.contract.dto.CompositionDTO;
import com.hydro.casting.server.contract.dto.CompositionElementDTO;
import com.hydro.core.common.SecurityCore;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.task.AbstractTask;
import javafx.application.Platform;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LoadAnalysisDetailTask extends AbstractTask
{
    @Inject
    private BusinessManager businessManager;

    private AnalysisDTO masterAnalysis;
    private AnalysisTable analysisTable;
    private boolean forFurnace;
    private boolean withTimestampInName;
    private boolean filterAnalysisForMachine;
    private boolean compressAnalysis;
    private String alloy;

    public void setData( AnalysisDTO masterAnalysis, AnalysisTable analysisTable, boolean forFurnace, boolean withTimestampInName, boolean filterAnalysisForMachine, boolean compressAnalysis )
    {
        this.setData( masterAnalysis, analysisTable, forFurnace, withTimestampInName, filterAnalysisForMachine, compressAnalysis, null );
    }

    public void setData( AnalysisDTO masterAnalysis, AnalysisTable analysisTable, boolean forFurnace, boolean withTimestampInName, boolean filterAnalysisForMachine, boolean compressAnalysis,
            String alloy )
    {
        this.masterAnalysis = masterAnalysis;
        this.analysisTable = analysisTable;
        this.forFurnace = forFurnace;
        this.withTimestampInName = withTimestampInName;
        this.filterAnalysisForMachine = filterAnalysisForMachine;
        this.compressAnalysis = compressAnalysis;
        this.alloy = alloy;
    }

    @Override
    public String getId()
    {
        return SecurityCore.LOGON;
    }

    @Override
    public void doWork() throws Exception
    {
        final AnalysisManagementView analysisManagementView = businessManager.getSession( AnalysisManagementView.class );
        Map<String, String> context = new HashMap<>();
        if ( alloy != null )
        {
            context.put( "alloy", alloy );
        }
        final AnalysisDetailDTO detail = analysisManagementView.loadDetail( AnalysisDetailDTO.class, masterAnalysis, context );

        String lastName = "Ø FA";
        final List<CompositionDTO> analysisList = detail.getAnalysisList();

        if ( analysisList != null && !analysisList.isEmpty() )
        {
            Stream<CompositionDTO> analysisStream = analysisList.stream().filter( compositionDTO -> !withoutContent( compositionDTO ) );

            if ( forFurnace )
            {
                analysisStream = analysisStream.filter(
                                compositionDTO -> compositionDTO.getName() != null && ( compositionDTO.getName().startsWith( "0" ) || compositionDTO.getName().startsWith( "9" ) ) )
                        .sorted( Comparator.comparing( CompositionDTO::getName ) );
            }
            else
            {
                if ( filterAnalysisForMachine )
                {
                    analysisStream = analysisStream.filter(
                            compositionDTO -> compositionDTO.getName() != null && !( compositionDTO.getName().startsWith( "0" ) || compositionDTO.getName().startsWith( "9" ) ) );
                }

                analysisStream = analysisStream.sorted( Comparator.comparing( CompositionDTO::getName ) );
            }

            List<CompositionDTO> filteredList = analysisStream.collect( Collectors.toList() );
            if ( forFurnace )
            {
                if ( filteredList.size() > 0 )
                {
                    final CompositionDTO lastComposition = filteredList.get( filteredList.size() - 1 );
                    lastComposition.setWeight( 1.0 );
                    lastName = lastComposition.getName();
                }
            }
            // If the analysis list is not filtered, every analysis should count into the average
            if ( !filterAnalysisForMachine )
            {
                filteredList.forEach( compositionDTO -> compositionDTO.setWeight( 1.0 ) );
            }
            detail.setAnalysisList( filteredList );
        }

        //            AnalysisCompositionHelper.compress( detail );
        AnalysisCompositionHelper.switchToPPM( detail );
        if ( withTimestampInName )
        {
            AnalysisCompositionHelper.appendTimestampToName( detail );
        }
        String finalLastName = lastName;
        Platform.runLater( () -> AnalysisCompositionHelper.setDetailOnAnalysisTable( detail, analysisTable, finalLastName, compressAnalysis ) );
    }

    private boolean withoutContent( CompositionDTO composition )
    {
        if ( composition == null )
        {
            return true;
        }
        final List<CompositionElementDTO> elements = composition.getCompositionElementDTOList();
        if ( elements == null || elements.isEmpty() )
        {
            return true;
        }
        for ( CompositionElementDTO element : elements )
        {
            if ( element.getValue() != null && element.getValue() != 0 )
            {
                return false;
            }
        }
        return true;
    }
}
