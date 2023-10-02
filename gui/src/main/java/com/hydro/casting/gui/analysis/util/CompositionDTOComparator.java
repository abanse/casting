package com.hydro.casting.gui.analysis.util;

import com.hydro.casting.server.contract.dto.CompositionDTO;

import java.util.Comparator;

/**
 * Comparator used to compare two CompositionDTO's. Comparison is done by the logical sample order.
 * <p>
 * Logical sample order: 01 - 10, 90 - 99, 11 - 89
 */
public class CompositionDTOComparator implements Comparator<CompositionDTO>
{
    @Override
    public int compare( CompositionDTO o1, CompositionDTO o2 )
    {
        return ComparatorHelper.compareSampleNumbers( o1.getSampleNumber(), o2.getSampleNumber() );
    }
}
