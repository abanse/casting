package com.hydro.casting.gui.model.calc;

import com.hydro.casting.gui.model.Transfer;
import com.hydro.casting.gui.model.TransferMaterial;
import javafx.beans.binding.DoubleBinding;

public class TransferMaterialWeightBinding extends DoubleBinding
{
    private final TransferMaterial transferMaterial;

    public TransferMaterialWeightBinding( TransferMaterial transferMaterial )
    {
        this.transferMaterial = transferMaterial;
    }

    @Override
    protected double computeValue()
    {
        final Transfer transfer = transferMaterial.getTransfer();
        if ( transfer == null )
        {
            return transferMaterial.getWeight();
        }
        return transfer.getTargetWeight() * transferMaterial.getWeight();
    }
}
