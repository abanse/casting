package com.hydro.casting.gui.model.calc;

import com.hydro.casting.gui.model.Transfer;
import javafx.beans.binding.DoubleBinding;

public class TransferBottomWeightBinding extends DoubleBinding
{
    private final Transfer transfer;

    public TransferBottomWeightBinding( Transfer transfer )
    {
        this.transfer = transfer;
    }

    @Override
    protected double computeValue()
    {
        Transfer prevFilledTransfer = findPreviousFilledTransfer( transfer );
        if ( prevFilledTransfer == null )
        {
            return transfer.getFrom().getActualWeight();
        }
        else if ( prevFilledTransfer.getBatch().equals( transfer.getBatch() ) == false && prevFilledTransfer.getBatch().isAlloyChange() )
        {
            return 0;
        }
        else if ( prevFilledTransfer.isMakeEmpty() )
        {
            return 0;
        }
        else
        {
            double sump = prevFilledTransfer.getTargetWeight() - prevFilledTransfer.getWeight();
            if ( sump < 0 )
            {
                sump = 0;
            }
            return sump;
        }
    }

    private Transfer findPreviousFilledTransfer( Transfer current )
    {
        if ( current == null )
        {
            return null;
        }
        Transfer previous = current.getBatch().getPrevious( current );
        if ( previous == null )
        {
            return null;
        }
        if ( previous.isRemoved() == false )
        {
            return previous;
        }
        return findPreviousFilledTransfer( previous );
    }

}
