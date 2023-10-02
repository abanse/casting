package com.hydro.casting.gui.model.calc;

import com.hydro.casting.gui.model.Batch;
import com.hydro.casting.gui.model.Transfer;
import com.hydro.casting.gui.model.common.AnalysisCalculator;
import javafx.beans.binding.StringBinding;

public class BatchSpecConformBinding extends StringBinding
{
    private final Batch batch;

    public BatchSpecConformBinding( Batch batch )
    {
        this.batch = batch;
    }

    @Override
    protected String computeValue()
    {
        final String analysisResult = AnalysisCalculator.specConform( batch );
        boolean oneTransferHasWeightProblems = false;
        for ( Transfer transfer : batch.getTransfers() )
        {
            if ( transfer.getWeight() > transfer.getTargetWeight() )
            {
                oneTransferHasWeightProblems = true;
                break;
            }
        }
        if ( oneTransferHasWeightProblems )
        {
            if ( analysisResult == null )
            {
                return "Mengenprobleme";
            }
            else
            {
                return "Mengenprobleme " + analysisResult;
            }
        }
        return analysisResult;
    }
}
