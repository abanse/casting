package com.hydro.casting.gui.prod.control;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.dto.InspectionDTO;
import com.hydro.casting.server.contract.dto.InspectionRuleDTO;
import com.hydro.casting.server.contract.dto.InspectionValueDTO;
import com.hydro.core.gui.binding.BeanPathAdapter;
import com.hydro.core.gui.control.DetailController;
import com.hydro.core.server.contract.workplace.MasterDetailProvider;
import com.hydro.core.server.contract.workplace.dto.ViewDTO;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract parent class to be used in DetailControllers that display details for process docu elements - one DetailController per area / tab of the process docu.
 * Provides capabilities common to all detail controllers:
 * - Setting a BeanPathAdapter (CasterScheduleDTO) for data access
 * - Loading / Creating frontend based on the inspection details (inspection rules and values)
 * - Returning inspection details as an InspectionDTO
 *
 * @param <M> Source DTO, which is selected from the parent list
 * @param <D> Target DTO, which should be returned filled with necessary information
 */
public abstract class InspectionDetailController<M extends ViewDTO, D extends InspectionDTO> extends DetailController<M, D>
{
    private final static CasterScheduleDTO EMPTY_BEAN = new CasterScheduleDTO();

    @Inject
    private Injector injector;

    private final List<InspectionController> inspectionControllers = new ArrayList<>();
    private final BooleanProperty editable = new SimpleBooleanProperty( false );
    protected final List<Node> ruleNodes = new ArrayList<>();
    protected final BeanPathAdapter<CasterScheduleDTO> beanPathAdapter = new BeanPathAdapter<>( EMPTY_BEAN );

    protected D dto;

    public InspectionDetailController( Class<? extends MasterDetailProvider<M>> businessClass, Class<D> dtoClass )
    {
        super( businessClass, dtoClass );
    }

    /**
     * Sets the BeanPathAdapter, which is required for later data access.
     * Implementing child class is required to set the DTO when retrieving it from a detail provider.
     */
    protected void loadBeanPathAdapter()
    {
        if ( dto == null )
        {
            beanPathAdapter.setBean( EMPTY_BEAN );
            return;
        }

        beanPathAdapter.setBean( dto.getCasterSchedule() );
    }

    /**
     * Creates the frontend based on the specified data, by retrieving the rules attached to the process docu area and loading the according FXML files and filling them with stored data, if existing.
     */
    protected void loadInspectionDetails()
    {
        inspectionControllers.clear();
        ruleNodes.clear();

        if ( dto != null && dto.getRules() != null )
        {
            for ( InspectionRuleDTO rule : dto.getRules() )
            {
                // find value
                InspectionValueDTO value = null;
                final List<InspectionValueDTO> values = dto.getValues();
                if ( values != null )
                {
                    for ( InspectionValueDTO inspectionValueDTO : values )
                    {
                        if ( inspectionValueDTO.getRuleId() == rule.getId() )
                        {
                            value = inspectionValueDTO;
                            break;
                        }
                    }
                }
                ruleNodes.add( createNode( dto.getCasterSchedule(), rule, value ) );
            }
        }
    }

    private Node createNode( CasterScheduleDTO casterScheduleDTO, InspectionRuleDTO rule, InspectionValueDTO value )
    {
        final String fxmlFileName;

        switch ( rule.getType() )
        {
        case Casting.INSPECTION.TYPE.YES_NO:
        case Casting.INSPECTION.TYPE.YES_NO_WITH_INTERVENTION:
        case Casting.INSPECTION.TYPE.OK_NOK:
        case Casting.INSPECTION.TYPE.OK_NOK_WITH_INTERVENTION:
        case Casting.INSPECTION.TYPE.YES_SIGNED:
            fxmlFileName = "InspectionTwoStateControl.fxml";
            break;
        case Casting.INSPECTION.TYPE.CASTER_POSITIONS:
            fxmlFileName = "CasterPositionsControl.fxml";
            break;
        case Casting.INSPECTION.TYPE.TEXT:
            fxmlFileName = "InspectionTextControl.fxml";
            break;
        case Casting.INSPECTION.TYPE.VISUAL_INSPECTION:
            fxmlFileName = "VisualInspectionControl.fxml";
            break;
        default:
            return new Label( "Typ nicht erkannt" );
        }

        final Node node;
        final FXMLLoader loader = new FXMLLoader( getClass().getResource( fxmlFileName ) );
        loader.setControllerFactory( injector::getInstance );
        try
        {
            node = loader.load();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            return new Label( "Fehler:" + e.getMessage() );
        }
        final InspectionController inspectionController = loader.getController();
        inspectionController.editableProperty().bind( editable );
        inspectionController.setInspection( casterScheduleDTO, rule, value );
        inspectionControllers.add( inspectionController );

        return node;
    }

    public InspectionDTO getInspectionDTO()
    {
        if ( dto == null )
        {
            return null;
        }
        final List<InspectionValueDTO> values = new ArrayList<>();
        for ( InspectionController inspectionController : inspectionControllers )
        {
            values.add( inspectionController.getValue() );
        }
        dto.setValues( values );
        return dto;
    }

    public boolean isEditable()
    {
        return editable.get();
    }

    public BooleanProperty editableProperty()
    {
        return editable;
    }

    public void setEditable( boolean editable )
    {
        this.editable.set( editable );
    }
}
