package com.hydro.casting.gui.locking.material.view;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.locking.material.control.LockMaterialControlController;
import com.hydro.casting.gui.locking.material.task.LockMaterialTask;
import com.hydro.casting.server.contract.locking.material.LockMaterialView;
import com.hydro.casting.server.contract.locking.material.dto.LockMaterialRequestDTO;
import com.hydro.casting.server.contract.locking.material.dto.LockableMaterialDTO;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.*;
import com.hydro.core.gui.control.DetailListController;
import com.hydro.core.gui.validation.MESValidationSupport;
import com.hydro.core.gui.view.ViewDeclaration;
import com.hydro.core.server.contract.workplace.ViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.controlsfx.validation.ValidationMessage;

import java.util.Collections;
import java.util.List;

@ViewDeclaration( id = LockMaterialViewController.ID, fxmlFile = "/com/hydro/casting/gui/locking/material/view/LockMaterialView.fxml", type = ViewType.DETAIL )
public class LockMaterialViewController extends DetailListController<LockableMaterialDTO, LockMaterialRequestDTO> implements ViewController
{
    public final static String ID = SecurityCasting.LOCKING.MATERIAL.LOCK_MATERIAL.VIEW;

    private MESValidationSupport validationSupport = new MESValidationSupport();

    private ViewModel<LockMaterialRequestDTO> viewModel = new ViewModel<>();

    @Inject
    private Injector injector;

    @Inject
    private TaskManager taskManager;

    @Inject
    private ViewManager viewManager;

    @Inject
    private SecurityManager securityManager;

    @FXML
    private LockMaterialControlController lockOutputMaterialController;

    @FXML
    private LockMaterialTask lockOutputMaterialTask;

    public LockMaterialViewController()
    {
        super( LockMaterialView.class, LockMaterialRequestDTO.class );
    }

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        injector.injectMembers( lockOutputMaterialTask );
        lockOutputMaterialTask.setViewModel( viewModel );

        lockOutputMaterialController.setValidationSupport( validationSupport );

        validationSupport.validationResultProperty().addListener( ( p, o, n ) -> {
            if ( n.getErrors().isEmpty() )
            {
                lockOutputMaterialTask.setDisabled( false );
                lockOutputMaterialTask.setRemark( null );
            }
            else
            {
                lockOutputMaterialTask.setDisabled( true );
                StringBuilder remark = new StringBuilder();
                for ( ValidationMessage error : n.getErrors() )
                {
                    if ( remark.length() > 0 )
                    {
                        remark.append( "\n" );
                    }
                    remark.append( error.getText() );
                }
                lockOutputMaterialTask.setRemark( remark.toString() );
            }
            lockOutputMaterialTask.setLocked( !securityManager.hasRole( lockOutputMaterialTask.getSecurityId( LockMaterialViewController.class ) ) );
        } );
    }

    @FXML
    void cancel( ActionEvent event )
    {
        viewManager.backward();
    }

    @FXML
    void save( ActionEvent event )
    {
        taskManager.executeTask( lockOutputMaterialTask );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public void setStartObject( Object startObject )
    {
        if ( startObject instanceof LockableMaterialDTO )
        {
            final LockableMaterialDTO lockableMaterialDTO = (LockableMaterialDTO) startObject;
            setMasterList( Collections.singletonList( lockableMaterialDTO ) );
        }
        else if ( startObject instanceof List )
        {
            setMasterList( (List) startObject );
        }
    }

    @Override
    public void beforeShown( View view )
    {
        lockOutputMaterialTask.setLocked( !securityManager.hasRole( lockOutputMaterialTask.getSecurityId( view.getViewContext() ) ) );
    }

    @Override
    public void activateView( View view )
    {
    }

    @Override
    public void deactivateView( View view )
    {
    }

    @Override
    public void reloadView( Object source, View view )
    {
    }

    @Override
    public void loadDetail( LockMaterialRequestDTO detail )
    {
        viewModel.setCurrentDTOs( detail );
        lockOutputMaterialController.loadData( detail );
    }
}
