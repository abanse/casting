package com.hydro.casting.gui.model.calc;

import com.hydro.casting.gui.model.Batch;
import javafx.beans.binding.DoubleBinding;

public class BatchBottomWeightBinding extends DoubleBinding
{
    private final Batch batch;

    public BatchBottomWeightBinding( Batch batch )
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
        final Batch previous = batch.getCaster().getPrevious( batch );
        if ( previous == null )
        {
            if ( batch.getFrom() == null )
            {
                return 0.;
            }
            return batch.getFrom().getActualWeight();
        }

        double sump = previous.getFurnaceTargetWeight() - previous.getCastingWeight();

        if ( sump < 0 || previous.isMakeEmpty() || previous.isAlloyChange() )
        {
            sump = 0;
        }
        return sump;
    }

    private double calcGroupFurnace()
    {
        final Batch previous = batch.getCaster().getPrevious( batch );
        if ( previous == null )
        {
            if ( batch.getFrom() == null )
            {
                return 0.;
            }
            return batch.getFrom().getActualWeight();
        }

        double sump = previous.getFurnaceTargetWeight() - previous.getCastingWeight();

        if ( sump < 0 || previous.isMakeEmpty() || previous.isAlloyChange() )
        {
            sump = 0;
        }
        return sump;
    }
}
