package com.hydro.casting.gui.locking.workflow.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.core.common.util.StringTools;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.TaskManager;
import com.hydro.core.gui.task.AbstractTask;
import com.hydro.core.server.contract.workplace.dto.KeyDescriptionDTO;
import com.hydro.core.server.contract.workplace.dto.SimpleKeyDescriptionDTO;
import com.hydro.casting.gui.locking.workflow.dialog.SelectKeyDescriptionPopOver;
import com.hydro.casting.server.contract.locking.workflow.LockingWorkflowBusiness;
import com.hydro.casting.server.contract.locking.workflow.dto.LockingWorkflowDTO;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.util.Callback;

import java.util.*;

public class LWChangeMaterialLockTypeTask extends AbstractTask
{
    @Inject
    private BusinessManager businessManager;

    @Inject
    private TaskManager taskManager;

    @Inject
    private LWUpdateMaterialLockTypeTask updateTask;

    private Node targetNode;
    private LockingWorkflowDTO lockingWorkflowDTO;

    @Override
    public void doWork() throws Exception
    {
        LockingWorkflowBusiness lockingWorkflowBusiness = businessManager.getSession( LockingWorkflowBusiness.class );

        Map<String, String> allDefectTypeCatHT = lockingWorkflowBusiness.createMaterialLockTypeHashTable();

        List<KeyDescriptionDTO> entries = new ArrayList<>();
        KeyDescriptionDTO currentEntry = null;
        for ( String key : allDefectTypeCatHT.keySet() )
        {
            final SimpleKeyDescriptionDTO kd = new SimpleKeyDescriptionDTO( key, allDefectTypeCatHT.get( key ) );
            entries.add( kd );
            if ( StringTools.isFilled( lockingWorkflowDTO.getDefectTypeCat() ) && lockingWorkflowDTO.getDefectTypeCat().equals( key ) )
            {
                currentEntry = kd;
            }
        }

        Collections.sort( entries, new Comparator<KeyDescriptionDTO>()
        {
            @Override
            public int compare( KeyDescriptionDTO o1, KeyDescriptionDTO o2 )
            {
                return o1.getKey().compareTo( o2.getKey() );
            }
        } );

        final KeyDescriptionDTO finalCurrentEntry = currentEntry;
        Platform.runLater( () -> {
            new SelectKeyDescriptionPopOver( "Sperrcode ändern", entries, finalCurrentEntry, targetNode, new Callback<String, Boolean>()
            {
                @Override
                public Boolean call( String newKey )
                {
                    updateTask.setLockingWorkflowDTO( lockingWorkflowDTO );
                    updateTask.setNewDefectTypeCat( newKey );
                    taskManager.executeTask( updateTask );
                    return true;
                }
            } );
        } );
    }

    public Node getTargetNode()
    {
        return targetNode;
    }

    public void setTargetNode( Node targetNode )
    {
        this.targetNode = targetNode;
    }

    public LockingWorkflowDTO getLockingWorkflowDTO()
    {
        return lockingWorkflowDTO;
    }

    public void setLockingWorkflowDTO( LockingWorkflowDTO lockingWorkflowDTO )
    {
        this.lockingWorkflowDTO = lockingWorkflowDTO;
    }

    @Override
    public String getId()
    {
        return SecurityCasting.LOCKING.ACTION.CHANGE_SCRAP_CODE;
    }
}
