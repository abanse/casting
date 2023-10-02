package com.hydro.casting.gui.model.calc;

import com.hydro.casting.gui.model.Batch;
import javafx.beans.binding.DoubleBinding;

public class BatchLengthBinding extends DoubleBinding
{
    private final Batch batch;

    public BatchLengthBinding( Batch batch )
    {
        this.batch = batch;
    }

    @Override
    protected double computeValue()
    {
        if ( batch.getNumberOfTransferProcesses() <= 1 )
        {
            return 450.0;
        }
        else if ( batch.getNumberOfTransferProcesses() == 2 )
        {
            return batch.getNumberOfTransferProcesses() * 250.0;
        }
        else
        {
            return batch.getNumberOfTransferProcesses() * 250.0;
        }
    }
}
