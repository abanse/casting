package com.hydro.casting.gui.prod.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.prod.control.ChargingControlController;
import com.hydro.casting.server.contract.dto.MaterialDTO;
import com.hydro.casting.server.contract.prod.ChargingBusiness;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.task.AbstractTask;
import javafx.application.Platform;

import java.util.List;
public class SaveChargingMaterialsTask extends AbstractTask
{
    @Inject
    private BusinessManager businessManager;

    @Inject
    private SecurityManager securityManager;

    @Inject
    private NotifyManager notifyManager;

    private ChargingControlController chargingController;

    private Long castingBatchOID;

    private List<MaterialDTO> furnaceContentMaterials;
    private List<MaterialDTO> chargingMaterials;

    public void setChargingController( ChargingControlController chargingController )
    {
        this.chargingController = chargingController;
    }

    public void setCastingBatchOID( Long castingBatchOID )
    {
        this.castingBatchOID = castingBatchOID;
    }

    public void setFurnaceContentMaterials( List<MaterialDTO> furnaceContentMaterials )
    {
        this.furnaceContentMaterials = furnaceContentMaterials;
    }

    public void setChargingMaterials( List<MaterialDTO> chargingMaterials )
    {
        this.chargingMaterials = chargingMaterials;
    }

    @Override
    public void doWork() throws Exception
    {
        if ( castingBatchOID == null )
        {
            return;
        }
        final ChargingBusiness chargingBusiness = businessManager.getSession( ChargingBusiness.class );
        chargingBusiness.saveChargingMaterials( securityManager.getCurrentUser(), castingBatchOID, furnaceContentMaterials, chargingMaterials );

        Platform.runLater( () -> chargingController.stopEditing() );

        notifyManager.showSplashMessage( "Die Daten wurde erfolgreich gespeichert" );
    }

    @Override
    public String getId()
    {
        return SecurityCasting.PROD.ACTION.SAVE_CHARGING_MATERIALS;
    }
}
