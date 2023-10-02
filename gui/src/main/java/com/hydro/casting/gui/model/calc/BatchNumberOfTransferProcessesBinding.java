package com.hydro.casting.gui.model.calc;

import com.hydro.casting.gui.model.Batch;
import javafx.beans.binding.IntegerBinding;

public class BatchNumberOfTransferProcessesBinding extends IntegerBinding
{
    private final Batch batch;

    public BatchNumberOfTransferProcessesBinding( Batch batch )
    {
        this.batch = batch;
    }

    @Override
    protected int computeValue()
    {
        return batch.getTransfers().size();
    }
}
