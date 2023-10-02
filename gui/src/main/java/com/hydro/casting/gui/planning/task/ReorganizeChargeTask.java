package com.hydro.casting.gui.planning.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.planning.CasterScheduleBusiness;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.task.AbstractTask;
import javafx.scene.control.ButtonType;

import java.util.ArrayList;

public class ReorganizeChargeTask extends AbstractTask
{
    @Inject
    private NotifyManager notifyManager;
    @Inject
    private BusinessManager businessManager;

    private CasterScheduleDTO schedule;

    @Override
    public String getId()
    {
        return SecurityCasting.MABE.ACTION.REORGANIZE_CHARGE;
    }

    public void setData( CasterScheduleDTO schedule )
    {
        this.schedule = schedule;
    }

    @Override
    public boolean beforeStart() throws Exception
    {
        final ButtonType result = notifyManager.showQuestionMessage( "Chargennummer reorganisieren", "Alle folgenden Chargenummern werden aufsteigend neu vergeben. Wollen Sie wirklich fortfahren?",
                ButtonType.YES, ButtonType.NO );
        return result == ButtonType.YES;
    }

    @Override
    public void doWork() throws Exception
    {
        final CasterScheduleBusiness casterScheduleBusiness = businessManager.getSession( CasterScheduleBusiness.class );

        casterScheduleBusiness.reorganizeCharge( schedule );

        notifyManager.showSplashMessage( "Die Chargennummern wurde neu vergeben" );
    }
}
