package com.hydro.casting.gui.main.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.dto.ProductionOrderDTO;
import com.hydro.casting.server.contract.main.ProductionOrderBusiness;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.ViewManager;
import com.hydro.core.gui.task.AbstractMultiSelectTask;
import javafx.beans.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CreateChargeTask extends AbstractMultiSelectTask<ProductionOrderDTO>
{
    @Inject
    private BusinessManager businessManager;

    @Inject
    private SecurityManager securityManager;

    @Inject
    private NotifyManager notifyManager;

    @Inject
    private ViewManager viewManager;

    @Override
    public String getId()
    {
        return SecurityCasting.MAIN.ACTION.CREATE_CHARGE;
    }

    @Override
    public void doWork() throws Exception
    {
        final ProductionOrderBusiness productionOrderBusiness = businessManager.getSession( ProductionOrderBusiness.class );
        final CasterScheduleDTO casterScheduleDTO = productionOrderBusiness.assignCharge( securityManager.getCurrentUser(), new ArrayList<>( getSelectionProvider().getSelectedValues() ) );

        if ( casterScheduleDTO != null )
        {
            notifyManager.showSplashMessage( "Die Charge " + casterScheduleDTO.getChargeWithoutYear() + " wurde erzeugt" );
        }
        else
        {
            notifyManager.showSplashMessage( "Die Charge wurde erzeugt" );
        }
        viewManager.reloadCurrentView( null );
    }

    @Override
    public void invalidated( Observable observable )
    {
        final List<ProductionOrderDTO> selectedValues = getSelectionProvider().getSelectedValues();
        boolean disabled = false;
        String info = null;
        if ( selectedValues.isEmpty() )
        {
            disabled = true;
        }
        else
        {
            for ( ProductionOrderDTO selectedValue : selectedValues )
            {
                if ( selectedValue.getCharge() != null )
                {
                    disabled = true;
                    info = "Mindestens ein Auftrag ist bereits einer Charge zugeordnet";
                    break;
                }
                if ( !Objects.equals( "P113", selectedValue.getKind() ) )
                {
                    disabled = true;
                    info = "Mindestens ein Auftrag ist kein Gießauftrag (P113)";
                    break;
                }
            }
        }
        setDisabled( disabled );
        if ( info != null )
        {
            setRemark( info );
        }
        else
        {
            setRemark( null );
        }
    }
}
