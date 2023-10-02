package com.hydro.casting.gui.planning.grid;

import com.hydro.casting.common.Casting;
import com.hydro.casting.gui.planning.view.content.MaBeHandle;
import com.hydro.core.gui.model.ClientModel;

public class Caster80GridConfig extends BaseCasterScheduleGridConfig
{
    public Caster80GridConfig( ClientModel model )
    {
        super( model );
    }

    @Override
    protected void addColumns()
    {
        addCastingSequence();
        addAlloyColumn();
        addExecutionStateColumn();
        addPos1Column();
        addPos2Column();
        addPos3Column();
        addPos4Column();
        addPos5Column();
        addProcessOrderColumn();
        addChargeColumn();
        addAnnotationColumn();
        addPlannedCalenderWeekColumn();
        addMeltingFurnaceColumn();
        addPlannedLengthColumn();
        addPlannedWeightColumn();
        addNetWeightColumn();
        addUtilizationColumn();
        addMaxWeightColumn();
        addMaxCastingLengthColumn();
        addPercentageBottomWeightColumn();
        addPercentageColumn( Casting.ALLOY_SOURCES.MELTING_FURNACE_S1);
        addPercentageColumn( Casting.ALLOY_SOURCES.MELTING_FURNACE_S2);
        addPercentageColumn( Casting.ALLOY_SOURCES.UBC_S3);
        addPercentageColumn( Casting.ALLOY_SOURCES.ELEKTROLYSE);
        addPercentageColumn( Casting.ALLOY_SOURCES.REAL_ALLOY);
        addPercentageMetalColumn();
    }
}
