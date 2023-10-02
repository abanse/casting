package com.hydro.casting.server.contract.locking.workflow;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.locking.workflow.dto.LWAddMessageDTO;
import com.hydro.casting.server.contract.locking.workflow.dto.LockingWorkflowDTO;
import com.hydro.core.common.exception.BusinessException;

import javax.ejb.Remote;
import java.util.List;
import java.util.Map;

@Remote
public interface LockingWorkflowBusiness
{

    enum Function {AssignProd, AssignQs, AssignAv, AssignTcs, PrintJasper, Mail, Com, FreeMaterial, ScrapMaterial, MoveToContainer}

    String FREE_MESSAGE = " FREIGEBEN";
    String SCRAP_MESSAGE = " VERSCHROTTEN";
    String CONTAINER_MESSAGE = " AN CONTAINER BUCHEN";
    String TABG_MESSAGE = "TABG SETZEN";
    String PRINT_MESSAGE = "DRUCKEN";
    String MAIL_MESSAGE = "MAILEN";
    String NOT_PRINT_MESSAGE = "NICHT DRUCKEN";

    String TRANSFER_PROD = "WEITER AN DIE PRODUKTION";

    String TRANSFER_QS = "WEITER AN DIE QS";

    String TRANSFER_AV = "WEITER AN DIE AV";

    String TRANSFER_TCS = "WEITER AN DIE TCS";

    String ADOPTION_QS = "ÜBERNAHME DURCH DIE QS";

    String NOT_OUTLOOK = "DRUCKEN STATT MAILEN (KEIN PFAD ZU OUTLOOK GEFUNDEN)";
    String WARNING_NOT_OUTLOOK = "KEIN PFAD ZU OUTLOOK GEFUNDEN, PDF-DATEI IM  VERZEICHNIS " + Casting.LOCKING_WORKFLOW.PDF_PATH + " ERZEUGT";

    String MULTI_LOCK_MESSAGE = "Achtung, Mehrfachsperre!\nBitte zuerst freigeben!";

    String MULTI_LOCK_MESSAGE_FREE1 = "Es gibt noch weitere Sperren ";

    String MULTI_LOCK_MESSAGE_FREE2 = "Produktionsfreigabe an SAP wurde nicht versendet!";

    int ROLE_AV = 0;

    int ROLE_PROD = 1;

    int ROLE_TCS = 2;

    int ROLE_QS = 3;

    int NOT_LOCKED = 1;

    int EXTERNAL = 2;

    int OWN_MODIFIED_OR_PRINTED = 2;

    int BUSINESS_EXCEPTION = 3;

    int NOT_CHANGED = 0;

    int NOT_CHANGED_MULTILOCK = -1;

    /**
     * Fügt einen Text zum Ablaufprotokoll hinzu.
     *
     * @param lockingWorkflowDTO
     * @param user
     * @param message
     * @throws BusinessException Prüfung ob die Länge des Textes ausreicht
     */
    void addMessage( LockingWorkflowDTO lockingWorkflowDTO, String user, String message, boolean isRemarkAV, boolean save ) throws BusinessException;

    void addMessages( List<LWAddMessageDTO> lockingWorkflowMessages, String user, boolean isRemarkAV, boolean save ) throws BusinessException;

    Map<LockingWorkflowDTO, String> checkOwnModifiedMessages( List<LWAddMessageDTO> lockingWorkflowMessages, int role ) throws BusinessException;

    List<LockingWorkflowDTO> totalHandling( Function function, List<LockingWorkflowDTO> lockingWorkflowDTOs, boolean hasRoleProd, boolean hasRoleQS, boolean hasRoleAV, boolean hasRoleTCS,
            String message, String user, boolean tabg, boolean print, boolean mailPossible, boolean adoption ) throws BusinessException;

    List<LockingWorkflowDTO> totalHandlingAll( Function function, List<LWAddMessageDTO> addMessages, boolean hasRoleProd, boolean hasRoleQS, boolean hasRoleAV, boolean hasRoleTCS, String message,
            String user, boolean tabg, boolean print, boolean mailPossible, boolean adoption ) throws BusinessException;

    void print( List<LockingWorkflowDTO> lockingWorkflowDTOs );

    Map<String, String> createCauserCodeHashTable();

    Map<String, String> createMaterialLockLocationHashTable();

    Map<String, String> createMaterialLockTypeHashTable();

    void updateCauser( LockingWorkflowDTO lockingWorkflowDTO, String newCauser ) throws BusinessException;

    void updateDefectTypeCat( LockingWorkflowDTO lockingWorkflowDTO, String newDefectTypeCat ) throws BusinessException;
    void updateMaterialLockLocation( LockingWorkflowDTO lockingWorkflowDTO, String newMaterialLockLocation ) throws BusinessException;

    String getLockComment( LockingWorkflowDTO lockingWorkflowDTOs );

    String createMessage( Function function, LockingWorkflowDTO lockingWorkflowDTO, String message, boolean tabg, boolean print, boolean mailPossible, boolean adoption );

    //    boolean nextIsPacking( LockingWorkflowDTO lockingWorkflowDTO );

    String createMessageScrap( boolean print, boolean mailPossible, boolean tabg );

    String createMessageFree( boolean mailPossible );

    List<LWAddMessageDTO> addFeedback( Function function, List<LWAddMessageDTO> addMessages, boolean tabg, List<LockingWorkflowDTO> notSAPFree );

    //    boolean updateFinishedGoods( long id ) throws RuntimeException;

    //    boolean isLastNoPackedStrip( List<LockingWorkflowDTO> lockingWorkflowDTOs );
}
