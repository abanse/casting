package com.hydro.casting.gui.melting.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.server.contract.melting.MeltingBusiness;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.NotifyManager;
import javafx.application.Platform;
import javafx.util.Callback;
public class CreateNewMeltingChargeTask extends AbstractNewChargeTask
{
    @Inject
    private BusinessManager businessManager;
    @Inject
    private NotifyManager notifyManager;

    private Callback<Void, Void> callbackAfterFinish;

    @Override
    public String getId()
    {
        return SecurityCasting.MELTING.ACTION.CREATE_NEW_MELTING_CHARGE;
    }

    public void setCallbackAfterFinish( Callback<Void, Void> callbackAfterFinish )
    {
        this.callbackAfterFinish = callbackAfterFinish;
    }

    @Override
    public void doWork() throws Exception
    {
        final MeltingBusiness meltingBusiness = businessManager.getSession( MeltingBusiness.class );
        meltingBusiness.createNewMeltingCharge( getMachineApk(), getNewMeltingChargeResult().getAlloy() );

        Platform.runLater( () -> {
            if ( callbackAfterFinish != null )
            {
                callbackAfterFinish.call( null );
            }
        } );

        notifyManager.showSplashMessage( "Neue Schmelzcharge wurde angelegt!" );
    }
}
