package com.hydro.casting.gui.model.calc;

import com.hydro.casting.gui.model.Transfer;
import javafx.beans.binding.BooleanBinding;

public class TransferRemoveAllowedBinding extends BooleanBinding
{
    private final Transfer transfer;

    public TransferRemoveAllowedBinding( Transfer transfer )
    {
        this.transfer = transfer;
    }

    @Override
    protected boolean computeValue()
    {
        if ( transfer == null || transfer.getBatch() == null )
        {
            return false;
        }
        boolean isLast = false;
        final Transfer nextTransfer = transfer.getBatch().getNext( transfer );
        if ( nextTransfer == null || !nextTransfer.getBatch().equals( transfer.getBatch() ) || nextTransfer.isRemoved() )
        {
            isLast = true;
        }
        boolean isFirst = false;
        final Transfer prevTransfer = transfer.getBatch().getPrevious( transfer );
        if ( prevTransfer != null && prevTransfer.getFurnaceTransferMaterial() != null )
        {
            isFirst = true;
        }
        return isLast && !isFirst;
    }

}
