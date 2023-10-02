package com.hydro.casting.gui.prod.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.prod.control.ChargingControlController;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.task.AbstractTask;
import javafx.application.Platform;
import javafx.scene.control.ButtonType;
public class StopEditingChargingMaterialsTask extends AbstractTask
{
    @Inject
    private NotifyManager notifyManager;

    private ChargingControlController chargingController;

    public void setChargingController( ChargingControlController chargingController )
    {
        this.chargingController = chargingController;
    }

    @Override
    public boolean beforeStart() throws Exception
    {
        final ButtonType result = notifyManager.showQuestionMessage( "Editieren Abbrechen", "Wollen Sie wirklich die Daten zurücksetzen?", ButtonType.YES, ButtonType.NO );
        return result == ButtonType.YES;
    }

    @Override
    public void doWork() throws Exception
    {
        Platform.runLater( () -> chargingController.stopEditing() );

        notifyManager.showSplashMessage( "Das Editieren wurde abgebrochen" );
    }

    @Override
    public String getId()
    {
        return SecurityCasting.PROD.ACTION.SAVE_CHARGING_MATERIALS;
    }
}
