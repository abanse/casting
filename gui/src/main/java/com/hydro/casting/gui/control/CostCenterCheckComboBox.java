package com.hydro.casting.gui.control;

import com.hydro.casting.common.constant.CostCenterEnum;
import com.hydro.core.gui.comp.MultiTextCheckComboBox;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class CostCenterCheckComboBox extends MultiTextCheckComboBox<CostCenterEnum>
{
    private List<String> allowedCostCenters = new ArrayList<>();

    public CostCenterCheckComboBox()
    {
        setVisibleRowCount( 10 );
        getItems().addAll( CostCenterEnum.values() );
        setConverter( new StringConverter<CostCenterEnum>()
        {
            @Override
            public String toString( CostCenterEnum lwCostCenterEnum )
            {
                return lwCostCenterEnum.getDescription();
            }

            @Override
            public CostCenterEnum fromString( String lwCostCenterEnumDescription )
            {
                return CostCenterEnum.findByDescription( lwCostCenterEnumDescription );
            }
        } );
        setButtonConverter( new StringConverter<CostCenterEnum>()
        {
            @Override
            public String toString( CostCenterEnum lwCostCenterEnum )
            {
                return lwCostCenterEnum.getCostCenter();
            }

            @Override
            public CostCenterEnum fromString( String lwCostCenterEnumCostCenter )
            {
                return CostCenterEnum.findByCostCenter( lwCostCenterEnumCostCenter );
            }
        } );
        getCheckModel().getCheckedIndices().addListener( new ListChangeListener<Integer>()
        {
            @Override
            public void onChanged( Change<? extends Integer> c )
            {
                while ( c.next() )
                {
                    for ( Integer added : c.getAddedSubList() )
                    {
                        CostCenterEnum selected = getItems().get( added );
                        if ( selected.getGroup() != null && selected.getGroup().startsWith( "*" ) )
                        {
                            for ( int i = 0; i < getItems().size(); i++ )
                            {
                                if ( i == added )
                                {
                                    continue;
                                }
                                if ( getCheckModel().isChecked( i ) )
                                {
                                    getCheckModel().clearCheck( i );
                                }
                            }
                        }
                        else
                        {
                            for ( int i = 0; i < getItems().size(); i++ )
                            {
                                CostCenterEnum lwCostCenterEnum = getItems().get( i );
                                if ( lwCostCenterEnum.getGroup() != null && lwCostCenterEnum.getGroup().startsWith( "*" ) )
                                {
                                    if ( getCheckModel().isChecked( i ) )
                                    {
                                        getCheckModel().clearCheck( i );
                                    }
                                }
                            }
                        }
                    }
                }
                if ( c.wasAdded() )
                {
                    rebuildAllowedCostCenters();
                    fireEvent( new ActionEvent() );
                }
                else if ( c.wasRemoved() )
                {
                    boolean onlySingleSelected = true;

                    int firstSingleIndex = 0;
                    for ( CostCenterEnum cce : getItems() )
                    {
                        if ( cce.getGroup() == null || cce.getGroup().startsWith( "*" ) == false )
                        {
                            break;
                        }
                        firstSingleIndex++;
                    }

                    for ( int i = 0; i < firstSingleIndex; i++ )
                    {
                        if ( getCheckModel().isChecked( i ) || c.getRemoved().contains( i ) )
                        {
                            onlySingleSelected = false;
                            break;
                        }
                    }
                    if ( onlySingleSelected )
                    {
                        rebuildAllowedCostCenters();
                        fireEvent( new ActionEvent() );
                    }
                }
            }
        } );
        rebuildAllowedCostCenters();
    }

    public void setCostCenterPredicate( Predicate<CostCenterEnum> costCenterPredicate )
    {
        final List<CostCenterEnum> filteredCostCenters = new ArrayList<>();
        for ( CostCenterEnum costCenterEnum : CostCenterEnum.values() )
        {
            if ( costCenterPredicate.test( costCenterEnum ) )
            {
                filteredCostCenters.add( costCenterEnum );
            }
        }
        getItems().clear();
        getItems().addAll( filteredCostCenters );
        rebuildAllowedCostCenters();
    }

    private void rebuildAllowedCostCenters()
    {
        allowedCostCenters.clear();
        final List<CostCenterEnum> selectedCostCenters = getCheckModel().getCheckedItems();
        selectedCostCenters.stream().forEach( lwCostCenter -> {
            if ( lwCostCenter.getGroup() != null && lwCostCenter.getGroup().equals( "*" ) )
            {
                for ( CostCenterEnum entry : getItems() )
                {
                    allowedCostCenters.add( entry.getCostCenter() );
                }
            }
            else if ( lwCostCenter.getGroup() != null && lwCostCenter.getGroup().startsWith( "*" ) )
            {
                if ( lwCostCenter.getGroup().length() > 1 )
                {
                    final String group = lwCostCenter.getGroup().substring( 1 );
                    for ( CostCenterEnum entry : getItems() )
                    {
                        if ( entry.getGroup() != null && entry.getGroup().equals( group ) )
                        {
                            allowedCostCenters.add( entry.getCostCenter() );
                        }
                    }

                }
                else
                {
                    for ( CostCenterEnum entry : getItems() )
                    {
                        if ( entry.getGroup() == null || entry.getGroup().startsWith( "*" ) == false )
                        {
                            allowedCostCenters.add( entry.getCostCenter() );
                        }
                    }
                }
            }
            else
            {
                allowedCostCenters.add( lwCostCenter.getCostCenter() );
            }
        } );
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
            return CostCenterCheckComboBox.this;
        }

        @Override
        public String getName()
        {
            return "onAction";
        }
    };

    public boolean isSelected( String costCenter )
    {
        if ( isFiltered() == false )
        {
            return true;
        }
        for ( String allowedCostCenter : allowedCostCenters )
        {
            if ( allowedCostCenter.equals( costCenter ) )
            {
                return true;
            }
        }
        return false;
    }

    public boolean isFiltered()
    {
        List<CostCenterEnum> selectedItems = getCheckModel().getCheckedItems();
        if ( selectedItems == null || selectedItems.isEmpty() || ( selectedItems.size() == 1 && selectedItems.contains( CostCenterEnum.ALL ) ) )
        {
            return false;
        }
        return true;
    }

    public String createFilter( String costCenterAttribute )
    {
        StringBuilder filterSB = new StringBuilder();
        for ( String allowedCostCenter : allowedCostCenters )
        {
            if ( filterSB.length() > 0 )
            {
                filterSB.append( "," );
            }
            filterSB.append( "'" + allowedCostCenter + "'" );
        }
        return costCenterAttribute + " IN (" + filterSB.toString() + ")";
    }

    public List<String> getAllowedCostCenters()
    {
        return allowedCostCenters;
    }
}
