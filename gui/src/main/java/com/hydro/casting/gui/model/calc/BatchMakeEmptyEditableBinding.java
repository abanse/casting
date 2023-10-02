package com.hydro.casting.gui.model.calc;

import com.hydro.casting.gui.model.Batch;
import javafx.beans.binding.BooleanBinding;

public class BatchMakeEmptyEditableBinding extends BooleanBinding
{
    private final Batch batch;

    public BatchMakeEmptyEditableBinding( Batch batch )
    {
        this.batch = batch;
    }

    @Override
    protected boolean computeValue()
    {
        if ( batch.isEditable() == false )
        {
            return false;
        }
        if ( batch.isMakeEmpty() )
        {
            return true;
        }
        final double maxMakeEmpty = 5;
        // Abgussmenge muss berechnet werden
        final double calcSump = batch.getFurnaceTargetWeight() - batch.getCastingWeight();

        return calcSump < ( maxMakeEmpty * 1000.0 );
    }
}
