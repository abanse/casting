package com.hydro.casting.gui.prod.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.common.constant.CasterStep;
import com.hydro.casting.server.contract.dto.CasterInstructionDTO;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.prod.ProductionBusiness;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.ViewManager;
import com.hydro.core.gui.task.AbstractMultiSelectTask;
import javafx.application.Platform;

public class UnloadSlabsTask extends AbstractMultiSelectTask<CasterScheduleDTO>
{
    @Inject
    private BusinessManager businessManager;

    @Inject
    private ViewManager viewManager;

    @Inject
    private SecurityManager securityManager;

    @Inject
    private NotifyManager notifyManager;

    private CasterInstructionDTO casterInstructionDTO;
    private int castingLength;

    public void setData( CasterInstructionDTO casterInstructionDTO, int castingLength )
    {
        this.casterInstructionDTO = casterInstructionDTO;
        this.castingLength = castingLength;
    }

    @Override
    public String getId()
    {
        return SecurityCasting.PROD.ACTION.UNLOAD_CASTER;
    }

    @Override
    public void doWork() throws Exception
    {
        final ProductionBusiness productionBusiness = businessManager.getSession( ProductionBusiness.class );
        productionBusiness.unloadSlabs( securityManager.getCurrentUser(), casterInstructionDTO.getCastingBatchOID(), castingLength );

        notifyManager.showSplashMessage( "Die Barren wurden erfolgreich gebucht" );

        Platform.runLater( () -> viewManager.backward() );
    }

}
