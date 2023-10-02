package com.hydro.casting.gui.model.calc;

import com.hydro.casting.gui.model.Batch;
import com.hydro.casting.gui.model.Furnace;
import com.hydro.casting.gui.model.Transfer;
import javafx.beans.binding.DoubleBinding;

public class FurnaceHeightMaxBinding extends DoubleBinding
{
    private final Furnace furnace;

    public FurnaceHeightMaxBinding( Furnace furnace )
    {
        this.furnace = furnace;
    }

    @Override
    protected double computeValue()
    {
        double heightMax = 0;
        if ( furnace.getCaster() != null )
        {
            for ( Batch batch : furnace.getCaster().getBatches() )
            {
                double maxWeight = batch.getFurnaceTargetWeight();
                if ( batch.getSumTransferWeights() > maxWeight )
                {
                    maxWeight = batch.getSumTransferWeights();
                }
                if ( maxWeight > heightMax )
                {
                    heightMax = maxWeight;
                }
            }
        }
        else
        {
            for ( Transfer transfer : furnace.getTransfers() )
            {
                if ( transfer.getTargetWeight() > heightMax )
                {
                    heightMax = transfer.getTargetWeight();
                }
            }
        }
        return heightMax;// + 2000.0;
    }
}
