package com.hydro.casting.gui.model.calc;

import com.hydro.casting.gui.model.Transfer;
import com.hydro.casting.gui.model.common.AnalysisCalculator;
import javafx.beans.binding.StringBinding;

public class TransferSpecConformBinding extends StringBinding
{
    private final Transfer transfer;

    public TransferSpecConformBinding( Transfer transfer )
    {
        this.transfer = transfer;
    }

    @Override
    protected String computeValue()
    {
        if ( transfer == null || transfer.getBatch() == null )
        {
            return null;
        }
        final String analysisResult = AnalysisCalculator.specConform( transfer );
        if ( transfer.getWeight() > transfer.getTargetWeight() )
        {
            if ( analysisResult == null )
            {
                return "Überführung > Füllmenge";
            }
            else
            {
                return "Überführung > Füllmenge " + analysisResult;
            }
        }
        return analysisResult;
    }
}
