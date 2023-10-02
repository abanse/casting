package com.hydro.casting.gui.model.calc;

import com.hydro.casting.gui.model.Batch;
import com.hydro.casting.gui.model.Transfer;
import javafx.beans.binding.BooleanBinding;

public class BatchRemovedBinding extends BooleanBinding
{
    private final Batch batch;

    public BatchRemovedBinding( Batch batch )
    {
        this.batch = batch;
    }

    @Override
    protected boolean computeValue()
    {
        if ( batch.getNumberOfTransferProcesses() == 1 )
        {
            boolean oneExist = false;
            for ( Transfer transfer : batch.getTransfers() )
            {
                if ( transfer.isRemoved() == false )
                {
                    oneExist = true;
                    break;
                }
            }
            return oneExist == false;
        }
        return false;
    }
}
