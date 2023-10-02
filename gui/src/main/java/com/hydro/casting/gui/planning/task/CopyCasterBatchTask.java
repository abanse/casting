package com.hydro.casting.gui.planning.task;

import com.google.inject.Inject;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.planning.CasterScheduleBusiness;
import com.hydro.core.gui.BusinessManager;
import com.hydro.core.gui.NotifyManager;
import com.hydro.core.gui.comp.IntegerTextField;
import com.hydro.core.gui.task.AbstractTask;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class CopyCasterBatchTask extends AbstractTask
{
    @Inject
    private BusinessManager businessManager;

    @Inject
    private NotifyManager notifyManager;

    private CasterScheduleDTO schedule;

    private int count;

    @Override
    public String getId()
    {
        return SecurityCasting.MABE.ACTION.CREATE_ENTRY;
    }

    public void setData( CasterScheduleDTO schedule )
    {
        this.schedule = schedule;
    }

    @Override
    public boolean beforeStart() throws Exception
    {
        final HBox countPane = new HBox();
        countPane.setAlignment( Pos.CENTER );
        countPane.setSpacing( 5.0 );
        final IntegerTextField countInput = new IntegerTextField();
        countInput.setPrefWidth( 80 );
        countInput.setIntValue( 2 );
        countPane.getChildren().addAll( new Label( "Anzahl" ), countInput );
        final ButtonType result = notifyManager.showCompInputMessage( "Eintrag kopieren", "Bitte geben Sie die Anzahl der zusätzlichen Einträge an", countPane, countInput );
        if ( result != ButtonType.OK )
        {
            return false;
        }

        this.count = countInput.getIntValue();

        return true;
    }

    @Override
    public void doWork() throws Exception
    {
        final CasterScheduleBusiness casterScheduleBusiness = businessManager.getSession( CasterScheduleBusiness.class );
        casterScheduleBusiness.copyCasterBatch( schedule, count );
    }
}
