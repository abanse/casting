package com.hydro.casting.gui.prod.control;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.gui.prod.view.ProcessDocuViewController;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.dto.InspectionRuleDTO;
import com.hydro.casting.server.contract.dto.InspectionValueDTO;
import com.hydro.core.common.util.BitSetTools;
import com.hydro.core.gui.SecurityManager;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import static com.hydro.casting.server.contract.dto.InspectionValueDTO.*;
public class VisualInspectionControlController implements InspectionController, InvalidationListener
{
    private final static Logger log = LoggerFactory.getLogger( VisualInspectionControlController.class );
    private final static int HEADER_ROWS = 2;
    private final static int NUMBER_OF_INSPECTIONS = 2;
    @Inject
    private Injector injector;
    @Inject
    private SecurityManager securityManager;
    @FXML
    private GridPane mainGrid;
    @FXML
    private Label description;
    @FXML
    private Label result;
    @FXML
    private Label inspector_nets;
    @FXML
    private Label inspection_ts_nets;
    @FXML
    private Label inspector_cast;
    @FXML
    private Label inspection_ts_cast;

    private final List<VisualInspectionSlabControlController> slabControlControllerList = new ArrayList<>();

    private InspectionValueDTO inspectionValue;
    private InspectionRuleDTO inspectionRule;

    private final StringProperty updateUserNets = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> updateTSNets = new SimpleObjectProperty<>();
    private final StringProperty updateUserCast = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> updateTSCast = new SimpleObjectProperty<>();

    private final BooleanProperty editable = new SimpleBooleanProperty( false );

    private final static String ISSUE_SEPARATOR = "|";
    private final static String DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm:ss";

    @FXML
    private void initialize()
    {
        inspector_nets.textProperty().bind( updateUserNets );
        inspector_cast.textProperty().bind( updateUserCast );
        inspection_ts_nets.textProperty().bind( createLocalDateTimeToTextBinding( updateTSNets ) );
        inspection_ts_cast.textProperty().bind( createLocalDateTimeToTextBinding( updateTSCast ) );
    }

    public boolean isEditable()
    {
        return editable.get();
    }

    @Override
    public BooleanProperty editableProperty()
    {
        return editable;
    }

    public void setEditable( boolean editable )
    {
        this.editable.set( editable );
    }

    @Override
    public void setInspection( CasterScheduleDTO casterScheduleDTO, InspectionRuleDTO inspectionRule, InspectionValueDTO inspectionValue )
    {
        this.inspectionRule = inspectionRule;
        this.inspectionValue = inspectionValue;
        this.description.setText( inspectionRule.getDescription() );

        addSlabForCasterPositionIfUsed( "1", casterScheduleDTO.getPos1Amount() );
        addSlabForCasterPositionIfUsed( "2", casterScheduleDTO.getPos2Amount() );
        addSlabForCasterPositionIfUsed( "3", casterScheduleDTO.getPos3Amount() );
        addSlabForCasterPositionIfUsed( "4", casterScheduleDTO.getPos4Amount() );
        addSlabForCasterPositionIfUsed( "5", casterScheduleDTO.getPos5Amount() );

        if ( inspectionValue != null )
        {
            this.updateUserNets.set( inspectionValue.getVisualInspectionUpdateUserNets() );
            this.updateTSNets.set( inspectionValue.getVisualInspectionUpdateTSNets() );
            this.updateUserCast.set( inspectionValue.getVisualInspectionUpdateUserCast() );
            this.updateTSCast.set( inspectionValue.getVisualInspectionUpdateTSCast() );

            String[] issueList = inspectionValue.getVisualInspectionIssue().split( "\\|" );
            BitSet bitset = BitSetTools.toBitSet( inspectionValue.getVisualInspectionValue() );
            // Each controller corresponds to 2 bits, to cover for 2 checkboxes / 3 possibilities. bitIndex is the index of the lower bit for the current controller
            int bitIndex = 0;
            for ( VisualInspectionSlabControlController controller : slabControlControllerList )
            {
                if ( bitset.get( bitIndex ) )
                {
                    controller.setValue( RESULT_OK );
                }
                else if ( bitset.get( bitIndex + 1 ) )
                {
                    controller.setValue( RESULT_FAILED );
                }
                else
                {
                    controller.setValue( RESULT_NOT_FILLED );
                }

                // issue list has one entry per controller, each controller is represented by 2 bits - so controllerIndex = bitIndex / 2
                controller.setIssue( issueList[bitIndex / 2] );

                bitIndex += 2;
            }
        }

        slabControlControllerList.forEach( VisualInspectionSlabControlController::installExternalListeners );
        slabControlControllerList.forEach( controller -> controller.editableProperty().bind( editable ) );
        invalidated( null );
    }

    /**
     * Adds a row in the grid pane of this FXML for a new slab, if the position was used during casting. Position can always produce 0, 1 or 2 slabs.
     * 0 if the position was not used during casting.
     * 1 if the position was used for normal casting.
     * 2 if the position was used to cast 2 slabs at once - this will only add 1 new slab row, because at this time, the slab is not yet cut into two parts, so
     * only one slab exists and only one ID should be given.
     *
     * Position 5 is always null except for caster 80.
     *
     * @param position The position that is currently being checked, to be used in the slabId label
     * @param amount   The amount of slabs for the given position (0, 1 or 2)
     */
    private void addSlabForCasterPositionIfUsed( String position, Integer amount )
    {
        if ( amount != null )
        {
            switch ( amount )
            {
            case 0:
                break;
            case 1:
            case 2:
                // Always adds 1 slab - at the time of the visual inspection the slab is not yet cut into two parts
                addNewSlabRowToGrid( "1" + position );
                break;
            default:
                log.error( "Unexpected value for caster position amount: " + amount );
            }
        }
    }

    private void addNewSlabRowToGrid( String slabId )
    {
        int row = ( slabControlControllerList.size() / NUMBER_OF_INSPECTIONS ) + HEADER_ROWS;

        Label labelSlabId = new Label( slabId );
        labelSlabId.setStyle( "-fx-font-weight: bold" );
        mainGrid.add( labelSlabId, 0, row );

        try
        {
            for ( int i = 0; i < NUMBER_OF_INSPECTIONS; i++ )
            {
                addNewVisualInspectionSlabControlToGrid( i + 1, row );
            }
        }
        catch ( IOException ioException )
        {
            log.error( ioException.getMessage() );
        }
    }

    private void addNewVisualInspectionSlabControlToGrid( int column, int row ) throws IOException
    {
        final String visualInspectionSlabControlFXML = "/com/hydro/casting/gui/prod/control/VisualInspectionSlabControl.fxml";
        FXMLLoader loader = new FXMLLoader( getClass().getResource( visualInspectionSlabControlFXML ) );
        loader.setControllerFactory( injector::getInstance );
        Node node = loader.load();
        VisualInspectionSlabControlController controller = loader.getController();

        InvalidationListener listener = null;
        // Number of cases should be equal to number of columns (NUMBER_OF_INSPECTIONS)
        switch ( column )
        {
        case 1:
            listener = observable -> {
                updateUserNets.set( securityManager.getUserInfo().getDisplayName() );
                updateTSNets.set( LocalDateTime.now() );
            };
            break;
        case 2:
            listener = observable -> {
                updateUserCast.set( securityManager.getUserInfo().getDisplayName() );
                updateTSCast.set( LocalDateTime.now() );
            };
            break;
        }

        if ( listener != null )
        {
            controller.addInvalidationListenerToExternalListeners( listener );
        }

        controller.addInvalidationListenerToExternalListeners( this );
        slabControlControllerList.add( controller );
        mainGrid.add( node, column, row );
    }

    @Override
    public InspectionValueDTO getValue()
    {
        InspectionValueDTO value = new InspectionValueDTO();
        if ( inspectionValue != null )
        {
            value.setId( inspectionValue.getId() );
        }
        else
        {
            value.setId( -1 );
        }
        value.setRuleId( inspectionRule.getId() );

        value.setResult( calcResult() );
        value.setVisualInspectionValue( getSlabValues() );
        value.setVisualInspectionIssue( getSlabIssues() );
        value.setVisualInspectionUpdateUserNets( updateUserNets.get() );
        value.setVisualInspectionUpdateTSNets( updateTSNets.get() );
        value.setVisualInspectionUpdateUserCast( updateUserCast.get() );
        value.setVisualInspectionUpdateTSCast( updateTSCast.get() );
        return value;
    }

    private int getSlabValues()
    {
        // Each controller corresponds to 2 bits, to cover for 2 checkboxes / 3 possibilities. bitIndex is the index of the lower bit for the current controller
        int bitIndex = 0;
        BitSet bitset = new BitSet();

        for ( VisualInspectionSlabControlController controller : slabControlControllerList )
        {
            if ( controller.getValue() == RESULT_OK )
            {
                bitset.set( bitIndex );
            }
            else if ( controller.getValue() == RESULT_FAILED )
            {
                bitset.set( bitIndex + 1 );
            }
            bitIndex += 2;
        }

        return BitSetTools.toInt( bitset );
    }

    private String getSlabIssues()
    {
        StringBuilder slabIssues = new StringBuilder();
        slabControlControllerList.forEach( controller -> slabIssues.append( controller.getIssue() ).append( ISSUE_SEPARATOR ) );
        return slabIssues.toString();
    }

    @Override
    public void invalidated( Observable observable )
    {
        setSummary( calcResult() );
    }

    private int calcResult()
    {
        return slabControlControllerList.stream().mapToInt( VisualInspectionSlabControlController::getValue ).max().orElse( RESULT_NOT_FILLED );
    }

    private void setSummary( int summary )
    {
        final ImageView icon;
        if ( summary == RESULT_OK )
        {
            icon = new ImageView( ProcessDocuViewController.RESULT_OK_I );
        }
        else if ( summary == RESULT_OK_WITH_LIMITATIONS )
        {
            icon = new ImageView( ProcessDocuViewController.RESULT_OK_WITH_LIMITATIONS_I );
        }
        else if ( summary == RESULT_FAILED )
        {
            icon = new ImageView( ProcessDocuViewController.RESULT_FAILED_I );
        }
        else
        {
            icon = new ImageView( ProcessDocuViewController.RESULT_MISSED_I );
        }
        icon.setFitHeight( 20 );
        icon.setFitWidth( 20 );
        result.setGraphic( icon );
    }

    private ObjectBinding<String> createLocalDateTimeToTextBinding( ObjectProperty<LocalDateTime> localDateTimeProperty )
    {
        return Bindings.createObjectBinding( () -> localDateTimeProperty.get() != null ? localDateTimeProperty.get().format( DateTimeFormatter.ofPattern( DATE_TIME_PATTERN ) ) : null,
                localDateTimeProperty );
    }
}
