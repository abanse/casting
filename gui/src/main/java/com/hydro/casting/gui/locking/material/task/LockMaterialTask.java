package com.hydro.casting.gui.locking.material.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.server.contract.locking.material.LockMaterialBusiness;
import com.hydro.casting.server.contract.locking.material.dto.LockMaterialDTO;
import com.hydro.casting.server.contract.locking.material.dto.LockMaterialRequestDTO;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.SecurityManager;
import com.hydro.core.gui.ViewManager;
import com.hydro.core.gui.task.AbstractTask;
import com.hydro.core.server.contract.workplace.ViewModel;
import javafx.application.Platform;
import javafx.scene.control.ButtonType;

public class LockMaterialTask extends AbstractTask
{
    @Inject
    private ViewManager viewManager;

    @Inject
    private NotifyManager notifyManager;

    @Inject
    private SecurityManager securityManager;

    @Inject
    private BusinessManager businessManager;

    private ViewModel<LockMaterialRequestDTO> viewModel;

    @Override
    public void doWork() throws Exception
    {
        final ButtonType result = notifyManager.showQuestionMessage( "Sicherheitsabfrage", "Wollen Sie wirklich die Barren sperren?", ButtonType.YES, ButtonType.NO );
        if ( result != ButtonType.YES )
        {
            return;
        }

        final LockMaterialBusiness materialBusiness = businessManager.getSession( LockMaterialBusiness.class );

        LockMaterialDTO newLock = viewModel.getFirstCurrentDTO().getNewLock();
        newLock.setUserId( securityManager.getCurrentUser() );

        materialBusiness.lockMaterial( newLock );

        Platform.runLater( () -> {
            viewManager.backward();
            notifyManager.showSplashMessage( "Die Sperren wurde erfolgreich gespeichert" );
            viewManager.reloadCurrentView( getId() );
        } );
    }

    public ViewModel<LockMaterialRequestDTO> getViewModel()
    {
        return viewModel;
    }

    public void setViewModel( ViewModel<LockMaterialRequestDTO> viewModel )
    {
        this.viewModel = viewModel;
    }

    @Override
    public String getId()
    {
        return SecurityCasting.LOCKING.ACTION.LOCK_MATERIAL;
    }
}
