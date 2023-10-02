package com.hydro.casting.gui.planning.table.cell;

import com.hydro.casting.gui.model.CastingClientModel;
import com.hydro.casting.server.contract.dto.CasterDemandDTO;
import com.hydro.casting.server.contract.dto.CasterSchedulePosDTO;
import com.hydro.casting.server.contract.dto.SlabDTO;
import com.hydro.core.gui.model.ClientModel;
import com.hydro.core.gui.model.ClientModelProvider;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

import java.util.Collection;
import java.util.Objects;

public class DemandCounterValueFactory implements Callback<TreeTableColumn.CellDataFeatures<CasterDemandDTO, Integer>, IntegerProperty>
{
    public enum CounterVariant
    {Scheduled, Slabs, ToProduce, ToSchedule}

    private ClientModelProvider clientModelProvider;
    private CounterVariant counterVariant;
    private String costCenter;

    final IntegerProperty observableValue = new SimpleIntegerProperty();

    public ClientModelProvider getClientModelProvider()
    {
        return clientModelProvider;
    }

    public void setClientModelProvider( ClientModelProvider clientModelProvider )
    {
        this.clientModelProvider = clientModelProvider;
    }

    public CounterVariant getCounterVariant()
    {
        return counterVariant;
    }

    public void setCounterVariant( CounterVariant counterVariant )
    {
        this.counterVariant = counterVariant;
    }

    public String getCostCenter()
    {
        return costCenter;
    }

    public void setCostCenter( String costCenter )
    {
        this.costCenter = costCenter;
    }

    @Override
    public IntegerProperty call( TreeTableColumn.CellDataFeatures<CasterDemandDTO, Integer> param )
    {
        if ( clientModelProvider == null )
        {
            observableValue.set( -1 );
            return observableValue;
        }
        final ClientModel clientModel = clientModelProvider.getClientModel();
        if ( counterVariant == CounterVariant.ToSchedule )
        {
            final CasterDemandDTO casterDemandDTO = param.getValue().getValue();
            final Collection<SlabDTO> assignedSlabs = clientModel.getRelatedEntity( casterDemandDTO, CastingClientModel.SLAB_ASSIGNMENT );
            final Collection<CasterSchedulePosDTO> assignedSchedulePositions = clientModel.getRelatedEntity( casterDemandDTO, CastingClientModel.SCHEDULE_ASSIGNMENT );
            final int toProduce = casterDemandDTO.getToProduce();
            if ( assignedSlabs == null && assignedSchedulePositions == null )
            {
                observableValue.set( toProduce );
            }
            else if ( assignedSchedulePositions == null )
            {
                observableValue.set( toProduce - assignedSlabs.size() );
            }
            else if ( assignedSlabs == null )
            {
                observableValue.set( toProduce - assignedSchedulePositions.size() );
            }
            else
            {
                observableValue.set( toProduce - assignedSlabs.size() - assignedSchedulePositions.size() );
            }
        }
        else if ( counterVariant == CounterVariant.ToProduce )
        {
            final CasterDemandDTO casterDemandDTO = param.getValue().getValue();
            final Collection<SlabDTO> assignedSlabs = clientModel.getRelatedEntity( casterDemandDTO, CastingClientModel.SLAB_ASSIGNMENT );
            final int toProduce = casterDemandDTO.getToProduce();
            if ( assignedSlabs == null )
            {
                observableValue.set( toProduce );
            }
            else
            {
                observableValue.set( toProduce - assignedSlabs.size() );
            }
        }
        else if ( counterVariant == CounterVariant.Slabs )
        {
            final Collection<SlabDTO> assignedSlabs = clientModel.getRelatedEntity( param.getValue().getValue(), CastingClientModel.SLAB_ASSIGNMENT );
            if ( assignedSlabs == null )
            {
                observableValue.set( 0 );
            }
            else
            {
                observableValue.set( assignedSlabs.size() );
            }
        }
        else
        {
            final Collection<CasterSchedulePosDTO> assignedSchedulePositions = clientModel.getRelatedEntity( param.getValue().getValue(), CastingClientModel.SCHEDULE_ASSIGNMENT );
            if ( assignedSchedulePositions == null )
            {
                observableValue.set( 0 );
            }
            else
            {
                if ( costCenter != null )
                {
                    observableValue.set( (int) assignedSchedulePositions.stream().filter( casterSchedulePosDTO -> {
                        return Objects.equals( costCenter, casterSchedulePosDTO.getCasterSchedule().getMachine() );
                    } ).count() );
                }
                else
                {
                    observableValue.set( assignedSchedulePositions.size() );
                }
            }
        }
        return observableValue;
    }
}
