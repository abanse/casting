package com.hydro.casting.gui.prod.view.content.context;

public class ChargingContext
{
    private boolean showDetails = false;
    private boolean compressAnalysis = true;

    public ChargingContext( boolean showDetails, boolean compressAnalysis )
    {
        this.showDetails = showDetails;
        this.compressAnalysis = compressAnalysis;
    }

    public boolean isShowDetails()
    {
        return showDetails;
    }

    public boolean isCompressAnalysis()
    {
        return compressAnalysis;
    }
}
