package com.hydro.casting.gui.model.calc;

import com.hydro.casting.gui.model.Batch;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.ObservableList;

public class CasterLengthSumBinding extends DoubleBinding
{
    private final ObservableList<Batch> batches;

    public CasterLengthSumBinding( ObservableList<Batch> batches )
    {
        this.batches = batches;
    }

    @Override
    protected double computeValue()
    {
        double lengthSum = 0;
        for ( Batch batch : batches )
        {
            lengthSum = lengthSum + batch.getLength();
        }
        return lengthSum;
    }
}
