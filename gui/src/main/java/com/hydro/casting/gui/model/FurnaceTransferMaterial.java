package com.hydro.casting.gui.model;

import com.hydro.casting.gui.model.common.AnalysisCalculator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class FurnaceTransferMaterial extends TransferMaterial
{
    private static final long serialVersionUID = 1L;

    private final ObjectProperty<Transfer> sourceTransfer;
    private final ObjectProperty<NamedModelElement> target;

    private String savedTargetBatchApk;
    private String savedTargetTransferApk;

    public FurnaceTransferMaterial()
    {
        sourceTransfer = new SimpleObjectProperty<>();
        target = new SimpleObjectProperty<>();
    }

    public final ObjectProperty<Transfer> sourceTransferProperty()
    {
        return this.sourceTransfer;
    }

    public final Transfer getSourceTransfer()
    {
        return this.sourceTransferProperty().get();
    }

    public final void setSourceTransfer( final Transfer sourceTransfer )
    {
        this.sourceTransferProperty().set( sourceTransfer );
    }

    public final ObjectProperty<NamedModelElement> targetProperty()
    {
        return this.target;
    }

    public final NamedModelElement getTarget()
    {
        return this.targetProperty().get();
    }

    public final void setTarget( final NamedModelElement target )
    {
        this.targetProperty().set( target );
    }

    @Override
    public Analysis getAnalysis()
    {
        if ( getSourceTransfer() != null )
        {
            if ( getSourceTransfer().getBatch() != null )
            {
                return AnalysisCalculator.createAverage( "ÜF " + getSourceTransfer().getBatch().getName() + " " + getSourceTransfer().getName(), getSourceTransfer(), false );
            }
            return AnalysisCalculator.createAverage( "ÜF " + getSourceTransfer().getName(), getSourceTransfer(), false );
        }
        return null;
    }

    public String getSavedTargetBatchApk()
    {
        return savedTargetBatchApk;
    }

    public void setSavedTargetBatchApk( String savedTargetBatchApk )
    {
        this.savedTargetBatchApk = savedTargetBatchApk;
    }

    public String getSavedTargetTransferApk()
    {
        return savedTargetTransferApk;
    }

    public void setSavedTargetTransferApk( String savedTargetTransferApk )
    {
        this.savedTargetTransferApk = savedTargetTransferApk;
    }
}
