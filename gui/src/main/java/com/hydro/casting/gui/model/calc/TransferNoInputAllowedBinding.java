package com.hydro.casting.gui.model.calc;

import com.hydro.casting.gui.model.Transfer;
import javafx.beans.binding.BooleanBinding;

public class TransferNoInputAllowedBinding extends BooleanBinding
{
    private final Transfer transfer;

    public TransferNoInputAllowedBinding( Transfer transfer )
    {
        this.transfer = transfer;
    }

    @Override
    protected boolean computeValue()
    {
        if ( transfer == null || transfer.getBatch() == null || transfer.isRemoved() )
        {
            return false;
        }
        final double inputWeight = transfer.getTargetWeight() - transfer.getBottomWeight();
        final double nextBottomWeight = transfer.getTargetWeight() - transfer.getWeight();
        return nextBottomWeight > inputWeight;
    }

}
