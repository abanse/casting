package com.hydro.casting.gui.prod.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.task.AbstractTask;
import javafx.application.Platform;
import javafx.util.Callback;
import org.controlsfx.control.PopOver;

public class ModifyKPIOutputTargetsTask extends AbstractTask
{
    @Inject
    private BusinessManager businessManager;

    @Inject
    private NotifyManager notifyManager;

    private PopOver parentPopOver;
    private Callback<Boolean, Void> refreshCallback;

    private String costCenter;
    private int shiftTargetValues;
    private int weekTargetValues;
    private int monthTargetValues;

    public PopOver getParentPopOver()
    {
        return parentPopOver;
    }

    public void setParentPopOver( PopOver parentPopOver )
    {
        this.parentPopOver = parentPopOver;
    }

    public Callback<Boolean, Void> getRefreshCallback()
    {
        return refreshCallback;
    }

    public void setRefreshCallback( Callback<Boolean, Void> refreshCallback )
    {
        this.refreshCallback = refreshCallback;
    }

    public String getCostCenter()
    {
        return costCenter;
    }

    public void setCostCenter( String costCenter )
    {
        this.costCenter = costCenter;
    }

    public void setData( int shiftTargetValues, int weekTargetValues, int monthTargetValues )
    {
        this.shiftTargetValues = shiftTargetValues;
        this.weekTargetValues = weekTargetValues;
        this.monthTargetValues = monthTargetValues;
    }

    @Override
    public void doWork() throws Exception
    {
        // FIXME Reporting
        //ReportingBusiness reportingBusiness = businessManager.getSession( ReportingBusiness.class );

        //reportingBusiness.setKPIOutputTargets( costCenter, shiftTargetValues, weekTargetValues, monthTargetValues );

        Platform.runLater( () -> {
            // reload current values
            refreshCallback.call( Boolean.TRUE );
            // close popup
            parentPopOver.hide();
        } );

        notifyManager.showSplashMessage( "KPI Zielvorgaben wurden geändert" );
    }

    @Override
    public String getId()
    {
        return SecurityCasting.REPORTING.ACTION.MODIFY_KPI_OUTPUT_TARGETS;
    }
}
