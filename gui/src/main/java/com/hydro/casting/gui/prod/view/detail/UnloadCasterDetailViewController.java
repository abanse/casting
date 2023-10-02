package com.hydro.casting.gui.prod.view.detail;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.prod.task.UnloadSlabsTask;
import com.hydro.casting.server.contract.dto.CasterInstructionDTO;
import com.hydro.casting.server.contract.dto.UnloadSlabDTO;
import com.hydro.casting.server.contract.prod.ProductionView;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.*;
import com.hydro.core.gui.comp.IntegerTextField;
import com.hydro.core.gui.comp.ServerTableView;
import com.hydro.core.gui.comp.numpad.NumPad;
import com.hydro.core.gui.validation.MESValidationSupport;
import com.hydro.core.gui.view.ViewControllerBase;
import com.hydro.core.gui.view.ViewDeclaration;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.util.List;

@ViewDeclaration( id = UnloadCasterDetailViewController.ID, fxmlFile = "/com/hydro/casting/gui/prod/view/detail/UnloadCasterDetailView.fxml", type = ViewType.DETAIL )
public class UnloadCasterDetailViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.PROD.DETAILS.UNLOAD_CASTER.VIEW;

    @FXML
    private UnloadSlabsTask unloadSlabsTask;

    @Inject
    private TaskManager taskManager;

    @Inject
    private Injector injector;

    @Inject
    private ViewManager viewManager;

    @Inject
    private SecurityManager securityManager;

    @Inject
    private BusinessManager businessManager;

    @FXML
    private ServerTableView<UnloadSlabDTO> slabList;

    @FXML
    private IntegerTextField castingLength;

    @FXML
    private NumPad numPad;

    private CasterInstructionDTO casterInstructionDTO;

    private MESValidationSupport validationSupport = new MESValidationSupport();
    //private IntegerProperty maxCastingLength = new SimpleIntegerProperty();

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        injector.injectMembers( unloadSlabsTask );
        slabList.connect( injector, () -> {
            if ( casterInstructionDTO == null )
            {
                return null;
            }
            final ProductionView productionView = businessManager.getSession( ProductionView.class );
            final List<UnloadSlabDTO> unloadSlabs = productionView.loadUnloadSlabs( casterInstructionDTO.getCastingBatchOID() );
//            if ( unloadSlabs != null )
//            {
//                double thisMaxCastingLength = 0;
//                for ( UnloadSlabDTO unloadSlab : unloadSlabs )
//                {
//                    if ( unloadSlab.getCastingLength() > thisMaxCastingLength )
//                    {
//                        thisMaxCastingLength = unloadSlab.getCastingLength();
//                    }
//                }
//                maxCastingLength.set( (int) Math.round( thisMaxCastingLength ) );
//            }
            Platform.runLater( () -> validationSupport.recalculate() );
            return unloadSlabs;
        } );

        castingLength.intValueProperty().bindBidirectional( numPad.intValueProperty() );

        castingLength.addValidationGreaterThan( validationSupport, new SimpleIntegerProperty( 1 ), new SimpleIntegerProperty( 9999 ), "Gießlänge", true );

        validationSupport.validationResultProperty().addListener( ( observable, oldValue, newValue ) -> {
            if ( newValue != null && newValue.getErrors().isEmpty() )
            {
                unloadSlabsTask.setDisabled( false );
                unloadSlabsTask.setRemark( null );
            }
            else
            {
                unloadSlabsTask.setDisabled( true );
                unloadSlabsTask.setRemark( "Gießlänge muss eingegeben werden" );
            }
        } );
    }

    @FXML
    void cancel( ActionEvent event )
    {
        viewManager.backward();
    }

    @Override
    public void setStartObject( Object startObject )
    {
        if ( startObject instanceof CasterInstructionDTO )
        {
            casterInstructionDTO = (CasterInstructionDTO) startObject;
        }
        else
        {
            casterInstructionDTO = null;
        }
        castingLength.setIntValue( 0 );
    }

    @Override
    public void beforeShown( View view )
    {
        unloadSlabsTask.setLocked( !securityManager.hasRole( unloadSlabsTask.getId() ) );
        if ( casterInstructionDTO != null )
        {
            view.setTitle( "Rückmelden Charge " + casterInstructionDTO.getChargeWithoutYear() );
        }
    }

    @Override
    public void activateView( View view )
    {
        slabList.loadData();
    }

    @Override
    public void deactivateView( View view )
    {
    }

    @Override
    public void reloadView( Object source, View view )
    {
        slabList.loadData();
    }

    @FXML
    public void unload( ActionEvent actionEvent )
    {
        unloadSlabsTask.setData( casterInstructionDTO, castingLength.getIntValue() );
        taskManager.executeTask( unloadSlabsTask );
    }
}
