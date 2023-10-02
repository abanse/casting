package com.hydro.casting.gui.model.calc;

import com.hydro.casting.gui.model.Transfer;
import javafx.beans.binding.BooleanBinding;

public class TransferMakeEmptyEditableBinding extends BooleanBinding
{
    private final Transfer transfer;

    public TransferMakeEmptyEditableBinding( Transfer transfer )
    {
        this.transfer = transfer;
    }

    @Override
    protected boolean computeValue()
    {
        if ( transfer.getBatch() != null && transfer.getBatch().isEditable() == false )
        {
            return false;
        }
        if ( transfer.isMakeEmpty() )
        {
            return true;
        }
        final double maxMakeEmpty = 5;

        final double calcSump = transfer.getTargetWeight() - transfer.getWeight();

        return calcSump < ( maxMakeEmpty * 1000.0 );
    }
}
