package com.hydro.casting.gui.locking.workflow.control;

import com.hydro.core.gui.PreferencesManager;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import org.controlsfx.control.CheckComboBox;

import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class OwnerCheckComboBox extends CheckComboBox<String>
{
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool( 1 );
    private ScheduledFuture<Void> scheduleFuture;

    private PreferencesManager preferencesManager;
    private String prefPath;
    private String prefKey;

    public OwnerCheckComboBox()
    {
        super();

        getItems().add( "Produktion" );
        getItems().add( "QS" );
        getItems().add( "AV" );
        getItems().add( "TCS" );
        getItems().add( "SAP" );

        getCheckModel().getCheckedItems().addListener( (ListChangeListener<String>) c -> fireEvent( new ActionEvent() ) );
    }

    public void activatePreferences( PreferencesManager preferencesManager, String prefPath, String prefKey )
    {
        this.preferencesManager = preferencesManager;
        this.prefPath = prefPath;
        this.prefKey = prefKey;

        int[] lastOwnerSelection = preferencesManager.getIntValues( PreferencesManager.SCOPE_USER, prefPath, prefKey );
        if ( lastOwnerSelection != null )
        {
            getCheckModel().checkIndices( lastOwnerSelection );
        }
        else
        {
            getCheckModel().checkAll();
        }
        getCheckModel().getCheckedIndices().addListener( (InvalidationListener) observable -> setPrefValue() );
    }

    private void setPrefValue()
    {
        if ( preferencesManager == null )
        {
            return;
        }
        if ( scheduleFuture != null && scheduleFuture.isDone() == false )
        {
            return;
        }
        scheduleFuture = scheduler.schedule( () -> {
            if ( getCheckModel().isEmpty() )
            {
                preferencesManager.setIntValues( PreferencesManager.SCOPE_USER, prefPath, prefKey, null );
            }
            else
            {
                int[] newValues = new int[getCheckModel().getCheckedIndices().size()];
                int index = 0;
                for ( Iterator<Integer> checkedIndexIter = getCheckModel().getCheckedIndices().iterator(); checkedIndexIter.hasNext(); )
                {
                    Integer checkedIndex = checkedIndexIter.next();
                    newValues[index] = checkedIndex;
                    index++;
                }
                preferencesManager.setIntValues( PreferencesManager.SCOPE_USER, prefPath, prefKey, newValues );
            }
            return null;
        }, 1, TimeUnit.SECONDS );
    }

    public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty()
    {
        return onAction;
    }

    public final void setOnAction( EventHandler<ActionEvent> value )
    {
        onActionProperty().set( value );
    }

    public final EventHandler<ActionEvent> getOnAction()
    {
        return onActionProperty().get();
    }

    private ObjectProperty<EventHandler<ActionEvent>> onAction = new ObjectPropertyBase<EventHandler<ActionEvent>>()
    {
        @Override
        protected void invalidated()
        {
            setEventHandler( ActionEvent.ACTION, get() );
        }

        @Override
        public Object getBean()
        {
            return OwnerCheckComboBox.this;
        }

        @Override
        public String getName()
        {
            return "onAction";
        }
    };

    public boolean isFiltered()
    {
        if ( getCheckModel().getCheckedItems().size() == getItems().size() )
        {
            return false;
        }
        return true;
    }

    public String createFilter()
    {
        StringBuilder ownerFilter = new StringBuilder();
        if ( getCheckModel().getCheckedItems().contains( "Produktion" ) )
        {
            ownerFilter.append( "(LR.PROD_START_TS IS NOT NULL AND LR.PROD_END_TS IS NULL)" );
        }
        if ( getCheckModel().getCheckedItems().contains( "QS" ) )
        {
            if ( ownerFilter.length() > 0 )
            {
                ownerFilter.append( " OR " );
            }
            ownerFilter.append( "(LR.QS_START_TS IS NOT NULL AND LR.QS_END_TS IS NULL)" );
        }
        if ( getCheckModel().getCheckedItems().contains( "AV" ) )
        {
            if ( ownerFilter.length() > 0 )
            {
                ownerFilter.append( " OR " );
            }
            ownerFilter.append( "(LR.AV_START_TS IS NOT NULL AND LR.AV_END_TS IS NULL)" );
        }
        if ( getCheckModel().getCheckedItems().contains( "TCS" ) )
        {
            if ( ownerFilter.length() > 0 )
            {
                ownerFilter.append( " OR " );
            }
            ownerFilter.append( "(LR.TCS_START_TS IS NOT NULL AND LR.TCS_END_TS IS NULL)" );
        }
        if ( getCheckModel().getCheckedItems().contains( "SAP" ) )
        {
            if ( ownerFilter.length() > 0 )
            {
                ownerFilter.append( " OR " );
            }
            ownerFilter.append( "(SC.DESCRIPTION = 'SAP-SPERRE' AND LR.PROD_START_TS IS NULL AND LR.QS_START_TS IS NULL AND LR.AV_START_TS IS NULL AND LR.TCS_START_TS IS NULL)" );
        }

        return "(" + ownerFilter.toString() + ")";
    }

}
