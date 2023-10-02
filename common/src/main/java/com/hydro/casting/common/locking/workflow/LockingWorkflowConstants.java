package com.hydro.casting.common.locking.workflow;

import com.hydro.casting.common.SecurityCasting;
public interface LockingWorkflowConstants
{
    interface ACTION_ID
    {
        String LOAD_DETAIL = "locking.workflow.action.loadDetail";
        String VALIDATE = "locking.workflow.action.validate";

        String PRINT = SecurityCasting.LOCKING.ACTION.PRINT;
        String SEND_MAIL = SecurityCasting.LOCKING.ACTION.SEND_MAIL;
        String UNLOCK = SecurityCasting.LOCKING.ACTION.UNLOCK;
        String SCRAP = SecurityCasting.LOCKING.ACTION.SCRAP;
        String MOVE_TO_CONTAINER = SecurityCasting.LOCKING.ACTION.MOVE_TO_CONTAINER;
        String ASSIGN_PRODUCTION = SecurityCasting.LOCKING.ACTION.ASSIGN_PRODUCTION;
        String ASSIGN_QS = SecurityCasting.LOCKING.ACTION.ASSIGN_QS;
        String ASSIGN_AV = SecurityCasting.LOCKING.ACTION.ASSIGN_AV;
        String ASSIGN_TCS = SecurityCasting.LOCKING.ACTION.ASSIGN_TCS;
        String ADD_MESSAGE = SecurityCasting.LOCKING.ACTION.ADD_MESSAGE;
        String CHANGE_INITIATOR = SecurityCasting.LOCKING.ACTION.CHANGE_INITIATOR;
    }
}
