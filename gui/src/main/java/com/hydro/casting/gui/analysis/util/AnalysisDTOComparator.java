package com.hydro.casting.gui.analysis.util;

import com.hydro.casting.server.contract.dto.AnalysisDTO;
import javafx.scene.control.TreeItem;

import java.util.Comparator;

/**
 * Comparator used to compare two treeItems containing an AnalysisDTO. Comparison is done by the logical sample order.
 * <p>
 * Logical sample order: 01 - 10, 90 - 99, 11 - 89
 */
public class AnalysisDTOComparator implements Comparator<TreeItem<AnalysisDTO>>
{

    @Override
    public int compare( TreeItem<AnalysisDTO> o1, TreeItem<AnalysisDTO> o2 )
    {
        int sampleNumber1 = getSampleNumberFromTreeItem( o1 );
        int sampleNumber2 = getSampleNumberFromTreeItem( o2 );

        return ComparatorHelper.compareSampleNumbers( sampleNumber1, sampleNumber2 );
    }

    private static int getSampleNumberFromTreeItem( TreeItem<AnalysisDTO> treeItem )
    {
        return Integer.parseInt( treeItem.getValue().getSampleNumber() );
    }
}
