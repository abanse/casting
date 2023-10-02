package com.hydro.casting.gui.model.calc;

import com.hydro.casting.gui.model.Batch;
import com.hydro.casting.gui.model.Transfer;
import com.hydro.casting.gui.model.TransferMaterial;
import javafx.beans.binding.DoubleBinding;

public class SumTransferWeightsBinding extends DoubleBinding
{
    private final Batch batch;

    public SumTransferWeightsBinding( Batch batch )
    {
        this.batch = batch;
    }

    @Override
    protected double computeValue()
    {
        if ( batch.isSingleFurnace() )
        {
            return calcSingleFurnace();
        }
        else
        {
            return calcGroupFurnace();
        }
    }

    private double calcSingleFurnace()
    {
        double furnaceTargetWeight = 0;

        for ( Transfer transfer : batch.getTransfers() )
        {
            furnaceTargetWeight = furnaceTargetWeight + transfer.getTargetWeight();
            break;
        }

        return furnaceTargetWeight;
    }

    private double calcGroupFurnace()
    {
        if ( batch == null || batch.getName() == null )
        {
            return 0;
        }
        double furnaceTargetWeight = 0;
        for ( Transfer transfer : batch.getTransfers() )
        {
            if ( transfer.isRemoved() || transfer.getFurnaceTransferMaterial() != null )
            {
                continue;
            }
            furnaceTargetWeight = furnaceTargetWeight + transfer.getWeight();
        }

        furnaceTargetWeight = furnaceTargetWeight + batch.getBottomWeight();
        for ( TransferMaterial transferMaterial : batch.getTransferMaterials() )
        {
            furnaceTargetWeight = furnaceTargetWeight + transferMaterial.getWeight();
        }

        return furnaceTargetWeight;
    }
}
