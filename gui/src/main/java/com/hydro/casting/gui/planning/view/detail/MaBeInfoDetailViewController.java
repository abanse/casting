package com.hydro.casting.gui.planning.view.detail;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.common.Casting;
import com.hydro.casting.common.SecurityCasting;
import com.hydro.casting.gui.model.CastingClientModel;
import com.hydro.casting.gui.planning.grid.Caster50_60_70GridConfig;
import com.hydro.casting.gui.planning.grid.Caster80GridConfig;
import com.hydro.casting.gui.planning.grid.node.CasterSchedulePosNode;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.dto.CasterSchedulePosDTO;
import com.hydro.core.gui.ClientModelManager;
import com.hydro.core.gui.View;
import com.hydro.core.gui.ViewManager;
import com.hydro.core.gui.ViewType;
import com.hydro.core.gui.comp.grid.GridView;
import com.hydro.core.gui.comp.grid.model.GridModel;
import com.hydro.core.gui.model.ClientModel;
import com.hydro.core.gui.view.ViewControllerBase;
import com.hydro.core.gui.view.ViewDeclaration;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.util.StringConverter;
import org.controlsfx.control.spreadsheet.CellGraphicFactory;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;

import java.util.List;

@ViewDeclaration( id = MaBeInfoDetailViewController.ID, fxmlFile = "/com/hydro/casting/gui/planning/view/detail/MaBeInfoDetailView.fxml", type = ViewType.DETAIL )
public class MaBeInfoDetailViewController extends ViewControllerBase
{
    public final static String ID = SecurityCasting.MABE.DETAILS.MABE_INFO.VIEW;

    @Inject
    private Injector injector;

    @Inject
    private ViewManager viewManager;

    @Inject
    private ClientModelManager clientModelManager;

    private ClientModel castingModel;

    @FXML
    private GridView<CasterScheduleDTO> gridView;

    private GridModel<CasterScheduleDTO> gridModel = new GridModel<>();

    private String costCenter;

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {
        injector.injectMembers( gridView );

        castingModel = clientModelManager.getClientModel( CastingClientModel.ID );

        gridView.setCellGraphicFactory( new CellGraphicFactory()
        {
            @Override
            public Node getNode( SpreadsheetCell spreadsheetCell )
            {
                return new CasterSchedulePosNode( null );
            }

            @Override
            public void load( Node node, SpreadsheetCell spreadsheetCell )
            {
                CasterSchedulePosNode casterSchedulePosNode = (CasterSchedulePosNode) node;
                casterSchedulePosNode.setCasterSchedulePos( (CasterSchedulePosDTO) spreadsheetCell.getItem() );
            }

            @Override
            public void loadStyle( Node node, SpreadsheetCell spreadsheetCell, Font font, Paint paint, Pos pos, Background background )
            {
            }

            @Override
            public void setUnusedNode( Node node )
            {
            }

            @Override
            public Class getType()
            {
                return CasterSchedulePosNode.class;
            }
        } );

        gridView.setEditable( false );
        gridView.setFixingColumnsAllowed( false );
        gridView.setFixingRowsAllowed( false );
    }

    void loadData()
    {
        final List<CasterScheduleDTO> casterSchedules;
        if ( Casting.MACHINE.CASTER_50.equals( costCenter ) )
        {
            casterSchedules = castingModel.getView( CastingClientModel.CASTER50_VIEW_ID );
        }
        else if ( Casting.MACHINE.CASTER_60.equals( costCenter ) )
        {
            casterSchedules = castingModel.getView( CastingClientModel.CASTER60_VIEW_ID );
        }
        else if ( Casting.MACHINE.CASTER_70.equals( costCenter ) )
        {
            casterSchedules = castingModel.getView( CastingClientModel.CASTER70_VIEW_ID );
        }
        else
        {
            casterSchedules = castingModel.getView( CastingClientModel.CASTER80_VIEW_ID );
        }

        gridModel.clearRowConverters();
        gridModel.clearColoredRowStyles();
        // modify setup types
        final ObservableList<CasterScheduleDTO> entries = FXCollections.observableArrayList();
        int row = 0;
        long lastIndex = 0;
        for ( CasterScheduleDTO casterSchedule : casterSchedules )
        {
            if ( "setup".equals( casterSchedule.getType() ) )
            {
                entries.add( createSetupEntry( gridModel, row, casterSchedule ) );
            }
            else
            {
                entries.add( casterSchedule );
            }
            lastIndex = casterSchedule.getExecutingSequenceIndex();
            row++;
        }

        final CasterScheduleDTO emptySchedule = new CasterScheduleDTO();
        emptySchedule.setId( -1 );
        emptySchedule.setMachine( costCenter );
        emptySchedule.setExecutingSequenceIndex( lastIndex );

        entries.add( emptySchedule );

        gridModel.setRows( entries );
        gridView.setGridModel( gridModel );
    }

    private CasterScheduleDTO createSetupEntry( GridModel<CasterScheduleDTO> gridModel, int row, CasterScheduleDTO originalEntry )
    {
        long duration = originalEntry.getDuration();
        if ( duration <= 0 )
        {
            gridModel.addColoredRowStyle( row, "yellow" );
        }
        else
        {
            gridModel.addColoredRowStyle( row, "red" );
        }
        gridModel.addRowConverter( row, new StringConverter<CasterScheduleDTO>()
        {
            @Override
            public String toString( CasterScheduleDTO object )
            {
                if ( object == null )
                {
                    return "Störzeit";
                }
                if ( object.getDuration() > 0 )
                {
                    return "" + object.getDuration() + "min :" + object.getAnnotation();
                }
                return object.getAnnotation();
            }

            @Override
            public CasterScheduleDTO fromString( String string )
            {
                return null;
            }
        } );
        return originalEntry;
    }

    @Override
    public void setStartObject( Object startObject )
    {
        if ( startObject != null && startObject instanceof String )
        {
            costCenter = (String) startObject;
            gridView.setGridIdSuffix( costCenter );
            gridView.resetColumnWidths();
            gridModel = new GridModel<>();
            if ( Casting.MACHINE.CASTER_80.equals( costCenter ) )
            {
                new Caster80GridConfig( castingModel ).configure( gridModel );
            }
            else if ( Casting.MACHINE.CASTER_50.equals( costCenter ) )
            {
                new Caster50_60_70GridConfig( castingModel ).configure( gridModel );
            }
            else if ( Casting.MACHINE.CASTER_60.equals( costCenter ) )
            {
                new Caster50_60_70GridConfig( castingModel ).configure( gridModel );
            }
            else if ( Casting.MACHINE.CASTER_70.equals( costCenter ) )
            {
                new Caster50_60_70GridConfig( castingModel ).configure( gridModel );
            }
        }
    }

    @Override
    public void beforeShown( View view )
    {
        super.beforeShown( view );
        view.setTitle( "Maschinebelegung " + costCenter );
    }

    @Override
    public void activateView( View view )
    {
        super.activateView( view );
        loadData();
    }

    @Override
    public void deactivateView( View view )
    {
        super.deactivateView( view );
    }

    public void cancel( ActionEvent actionEvent )
    {
        viewManager.backward();
    }
}
