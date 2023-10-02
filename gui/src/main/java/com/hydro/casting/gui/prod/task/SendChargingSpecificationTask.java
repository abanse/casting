package com.hydro.casting.gui.prod.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.prod.ChargingBusiness;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.task.AbstractMultiSelectTask;

public class SendChargingSpecificationTask extends AbstractMultiSelectTask<CasterScheduleDTO>
{
    @Inject
    private BusinessManager businessManager;

    @Inject
    private SecurityManager securityManager;

    @Inject
    private NotifyManager notifyManager;

    private Long castingBatchOID;

    public void setCastingBatchOID( Long castingBatchOID )
    {
        this.castingBatchOID = castingBatchOID;
    }

    @Override
    public String getId()
    {
        return SecurityCasting.PROD.ACTION.SEND_CHARGING_SPECIFICATION;
    }

    @Override
    public void doWork() throws Exception
    {
        if ( castingBatchOID == null )
        {
            return;
        }
        final ChargingBusiness chargingBusiness = businessManager.getSession( ChargingBusiness.class );
        chargingBusiness.sendChargingSpecification( securityManager.getCurrentUser(), castingBatchOID );

        notifyManager.showSplashMessage( "Die Daten wurde erfolgreich versendet" );
    }

}
