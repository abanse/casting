package com.hydro.casting.gui.prod.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.prod.control.ChargingControlController;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.task.AbstractTask;
import javafx.application.Platform;
public class DeleteChargingMaterialsTask extends AbstractTask
{
    @Inject
    private NotifyManager notifyManager;

    private ChargingControlController chargingController;

    public void setChargingController( ChargingControlController chargingController )
    {
        this.chargingController = chargingController;
    }

    @Override
    public void doWork() throws Exception
    {
        Platform.runLater( () -> chargingController.deleteSelectedRow() );

        notifyManager.showSplashMessage( "Die Einträge wurden gelöscht" );
    }

    @Override
    public String getId()
    {
        return SecurityCasting.PROD.ACTION.DELETE_CHARGING_MATERIALS;
    }
}
