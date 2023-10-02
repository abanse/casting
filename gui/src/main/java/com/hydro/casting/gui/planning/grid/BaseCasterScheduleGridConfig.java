package com.hydro.casting.gui.planning.grid;

import com.hydro.casting.common.Casting;
import com.hydro.casting.gui.ImagesCasting;
import com.hydro.casting.gui.model.Batch;
import com.hydro.casting.gui.model.Caster;
import com.hydro.casting.gui.model.CastingClientModel;
import com.hydro.casting.gui.model.Transfer;
import com.hydro.casting.gui.util.CastingUtil;
import com.hydro.casting.server.contract.dto.CasterScheduleDTO;
import com.hydro.casting.server.contract.dto.CasterSchedulePosDTO;
import com.hydro.casting.server.contract.dto.MachineDTO;
import com.hydro.core.common.util.StringTools;
import com.hydro.core.gui.comp.grid.model.GridModel;
import com.hydro.core.gui.comp.grid.model.GridPopupHandler;
import com.hydro.core.gui.model.ClientModel;
import com.hydro.core.gui.util.converter.LocalDateTimeConverter;
import com.hydro.core.gui.util.converter.NumberConverter;
import com.hydro.core.gui.util.converter.StringNumberConverter;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class BaseCasterScheduleGridConfig
{
    protected NumberConverter n0Converter = new NumberConverter( new DecimalFormat( "0" ) );
    protected StringConverter<Number> tConverter = new StringConverter<Number>()
    {
        @Override
        public String toString( Number value )
        {
            if ( value == null )
            {
                return "";
            }
            final double kg = value.doubleValue();
            return StringTools.N02F.format( kg / 1000.0 );
        }

        @Override
        public Number fromString( String string )
        {
            return null;
        }
    };
    protected NumberConverter n0ignore0Converter = new NumberConverter( new DecimalFormat( "0" ), true );
    protected NumberConverter thicknessConverter = new NumberConverter( new DecimalFormat( "   #.#####" ) );

    protected StringNumberConverter stringNumberConverter = new StringNumberConverter( new DecimalFormat( "0" ), true );

    protected StringConverter<String> chargePropertyConverter = new StringConverter<String>()
    {
        @Override
        public String toString( String charge )
        {
            if ( charge != null && charge.length() > 2 )
            {
                return charge.substring( 2 );
            }
            return null;
        }

        @Override
        public String fromString( String string )
        {
            return null;
        }
    };

    protected LocalDateTimeConverter dateLongConverter = new LocalDateTimeConverter( "dd.MM.yy HH:mm" );

    protected Callback<CasterScheduleDTO, String> executionStateCellStyleCallback = ( ( CasterScheduleDTO param ) -> {
        if ( param == null )
        {
            return null;
        }
        switch ( param.getExecutionState() )
        {
        case Casting.SCHEDULABLE_STATE.CANCELED:
        case Casting.SCHEDULABLE_STATE.FAILED:
            return "red";
        case Casting.SCHEDULABLE_STATE.IN_PROGRESS:
        case Casting.SCHEDULABLE_STATE.CASTING_IN_PROGRESS:
        case Casting.SCHEDULABLE_STATE.UNLOADING_IN_PROGRESS:
        case Casting.SCHEDULABLE_STATE.PAUSED:
            return "yellow";
        case Casting.SCHEDULABLE_STATE.SUCCESS:
            return "green";

        default:
            return null;
        }
    } );

    private List<GridColumn> gridColumns = new ArrayList<>();

    private ClientModel model;

    public BaseCasterScheduleGridConfig( ClientModel model )
    {
        this.model = model;
    }

    public void configure( GridModel<CasterScheduleDTO> gridModel )
    {
        addColumns();

        gridModel.setRowHeightCallback( param -> 32.0 );

        final List<String> properties = new ArrayList<>();
        final List<String> columnHeaders = new ArrayList<>();
        final List<Double> defaultColumnWidths = new ArrayList<>();

        for ( GridColumn gridColumn : gridColumns )
        {
            properties.add( gridColumn.getProperty() );
            columnHeaders.add( gridColumn.getColumnHeader() );
            defaultColumnWidths.add( gridColumn.getDefaultColumnWidth() );
        }

        gridModel.setProperties( properties );
        gridModel.setColumnHeaders( columnHeaders );
        gridModel.setDefColumnWidths( defaultColumnWidths );

        for ( GridColumn gridColumn : gridColumns )
        {
            if ( gridColumn.getCellStyleCallback() != null )
            {
                gridModel.addCellStyleCallback( gridColumn.getProperty(), gridColumn.getCellStyleCallback() );
            }
            if ( gridColumn.getPropertyConverter() != null )
            {
                gridModel.addPropertyConverter( gridColumn.getProperty(), gridColumn.getPropertyConverter() );
            }
            if ( gridColumn.getCellConverter() != null )
            {
                gridModel.addCellConverter( gridColumn.getProperty(), gridColumn.getCellConverter() );
            }
            if ( gridColumn.isEditable() )
            {
                gridModel.setEditable( gridColumn.getProperty(), true );
            }
            if ( gridColumn.getPopupHandler() != null )
            {
                gridModel.setPopupHandler( gridColumn.getProperty(), gridColumn.getPopupHandler() );
            }
        }

        int index = 0;
        for ( GridColumn gridColumn : gridColumns )
        {
            if ( gridColumn.getColumnStyle() != null )
            {
                gridModel.addColumnStyle( index, gridColumn.getColumnStyle() );
            }
            if ( !gridColumn.isIgnoreSingleColumn() )
            {
                gridModel.addSingleRowColumn( index );
            }
            if ( gridColumn.getColumnCellObjectProvider() != null )
            {
                gridModel.setColumnCellObjectProvider( index, gridColumn.getColumnCellObjectProvider() );
            }
            if ( gridColumn.getColumnGraphicProvider() != null )
            {
                gridModel.setColumnGraphicProvider( index, gridColumn.getColumnGraphicProvider() );
            }
            if ( gridColumn.getColumnCellStyleClassProvider() != null )
            {
                gridModel.setColumnCellStyleClassProvider( index, gridColumn.getColumnCellStyleClassProvider() );
            }
            index++;
        }
    }

    protected abstract void addColumns();

    protected void addExecutionStateColumn()
    {
        final GridColumn<Number> column = addGridColumn( "executionState", "Status", 60.0, null, executionStateCellStyleCallback, CastingUtil.executionStatePropertyConverter, null );
        column.setColumnGraphicProvider( casterScheduleDTO -> {
            if ( casterScheduleDTO.getId() == -1 || "setup".equals( casterScheduleDTO.getType() ) )
            {
                return null;
            }
            // TDOD temporär muss wieder raus
            if ( Casting.MACHINE.MELTING_FURNACE_S1.equals( casterScheduleDTO.getMachine() ) || Casting.MACHINE.MELTING_FURNACE_S2.equals( casterScheduleDTO.getMachine() ) )
            {
                return null;
            }
            StringBuilder errorMessageSB = new StringBuilder();
            final MachineDTO machineDTO = model.getRelatedEntity( casterScheduleDTO, CastingClientModel.CURRENT_FURNACE );
            if ( machineDTO != null )
            {
                if ( casterScheduleDTO.getPlannedWeight() > machineDTO.getMaxWeight() )
                {
                    StringTools.appendln( errorMessageSB, "Maximale Ofenkapazität überschritten" );
                }
            }
            int countPlannedPositions = 0;
            for ( int i = 1; i < 6; i++ )
            {
                CasterSchedulePosDTO casterSchedulePosDTO = casterScheduleDTO.getPos( i );
                if ( casterSchedulePosDTO.getMaterialType() != null )
                {
                    countPlannedPositions++;
                }
            }
            if ( countPlannedPositions < 2 )
            {
                StringTools.appendln( errorMessageSB, "Minimal 2 Positionen müssen besetzt sein" );
            }
            if ( errorMessageSB.length() > 0 )
            {
                final ImageView imageView = new ImageView( ImagesCasting.ERROR.load( 18, 18, true, true ) );
                imageView.setUserData( errorMessageSB.toString() );
                return imageView;
            }
            else
            {
                return null;
            }
        } );
    }

    protected void addAlloyColumn()
    {
        final GridColumn<String> column = addGridColumn( "alloy", "Leg.", 70.0, null, null, null, null );
        column.setColumnCellStyleClassProvider( ( param -> {
            if ( param == null )
            {
                return null;
            }
            if ( "setup".equals( param.getType() ) )
            {
                return null;
            }
            final CasterScheduleDTO prev = param.findPrevCastingBatch();
            if ( prev == null )
            {
                return null;
            }
            if ( Objects.equals( prev.getAlloy(), param.getAlloy() ) )
            {
                return null;
            }
            return "prev-change";
        } ) );
    }

    protected void addPercentageColumn( String source )
    {
        final GridColumn<CasterScheduleDTO> column = addGridColumn( "percentage" + source, "%" + source, 60.0, null, new Callback<CasterScheduleDTO, String>()
        {
            @Override
            public String call( CasterScheduleDTO casterScheduleDTO )
            {
                boolean concrete = false;
                if ( Objects.equals( Casting.ALLOY_SOURCES.MELTING_FURNACE_S1, source ) )
                {
                    if ( casterScheduleDTO.getPercentageS1() != null )
                    {
                        concrete = true;
                    }
                }
                else if ( Objects.equals( Casting.ALLOY_SOURCES.MELTING_FURNACE_S2, source ) )
                {
                    if ( casterScheduleDTO.getPercentageS2() != null )
                    {
                        concrete = true;
                    }
                }
                else if ( Objects.equals( Casting.ALLOY_SOURCES.UBC_S3, source ) )
                {
                    if ( casterScheduleDTO.getPercentageS3() != null )
                    {
                        concrete = true;
                    }
                }
                else if ( Objects.equals( Casting.ALLOY_SOURCES.ELEKTROLYSE, source ) )
                {
                    if ( casterScheduleDTO.getPercentageEL() != null )
                    {
                        concrete = true;
                    }
                }
                else if ( Objects.equals( Casting.ALLOY_SOURCES.REAL_ALLOY, source ) )
                {
                    if ( casterScheduleDTO.getPercentageRA() != null )
                    {
                        concrete = true;
                    }
                }
                if ( concrete )
                {
                    return "-fx-alignment: center-right; -fx-text-fill: black";
                }
                else
                {
                    return "-fx-alignment: center-right; -fx-text-fill: gray";
                }
            }
        }, null, new StringConverter<CasterScheduleDTO>()
        {
            @Override
            public String toString( CasterScheduleDTO casterScheduleDTO )
            {
                final double percentage = getPercentage( casterScheduleDTO, source );
                if ( percentage == 0 )
                {
                    return "";
                }
                return StringTools.N1F.format( percentage );
            }

            @Override
            public CasterScheduleDTO fromString( String string )
            {
                return null;
            }
        } );
        column.setEditable( true );
    }

    protected void addPercentageMetalColumn()
    {
        addGridColumn( "percentageMetal", "%Metall", 60.0, "-fx-alignment: center-right", null, null, new StringConverter<CasterScheduleDTO>()
        {
            @Override
            public String toString( CasterScheduleDTO casterScheduleDTO )
            {
                if ( !( model instanceof CastingClientModel ) || casterScheduleDTO.getId() < 0 )
                {
                    return "";
                }

                final MachineDTO machineDTO = model.getRelatedEntity( casterScheduleDTO, CastingClientModel.CURRENT_FURNACE );
                if ( machineDTO == null || machineDTO.getMaxWeight() == null )
                {
                    return "";
                }
                final Caster caster = model.getEntity( Caster.class, casterScheduleDTO.getMachine() );
                if ( caster == null )
                {
                    return "";
                }
                final Batch batch = caster.findBatch( casterScheduleDTO.getId() );
                if ( batch == null || batch.getTransfers().isEmpty() )
                {
                    return "";
                }
                final Transfer transfer = batch.getTransfers().get( 0 );
                final double bottomWeight = transfer.getBottomWeight();
                final double percentageBottomWeight = bottomWeight / machineDTO.getMaxWeight() * 100.0;

                final CastingClientModel castingClientModel = (CastingClientModel) model;
                double alloySourcePercentage = percentageBottomWeight;
                for ( String source : Casting.ALLOY_SOURCES.ALL )
                {
                    final double percentage = getPercentage( casterScheduleDTO, source );
                    alloySourcePercentage = alloySourcePercentage + percentage;
                }

                return StringTools.N1F.format( 100 - alloySourcePercentage );
            }

            @Override
            public CasterScheduleDTO fromString( String string )
            {
                return null;
            }
        } );
    }

    protected void addProcessOrderColumn()
    {
        final GridColumn<String> column = addGridColumn( "processOrder", "PA", 70.0, null, null, null, null );
        column.setEditable( true );
    }

    protected void addAnnotationColumn()
    {
        final GridColumn<String> column = addGridColumn( "annotation", "Bemerkung", 220.0, null, null, null, null );
        column.setEditable( true );
    }

    protected void addPos1Column()
    {
        final GridColumn<CasterScheduleDTO> posColumn = addGridColumn( "pos1", "Position 1", 140.0, null, null, null, null );
        posColumn.setColumnCellStyleClassProvider( new PosStyleClassCallback( 1 ) );
        posColumn.setColumnCellObjectProvider( param -> param.getPos1() );
    }

    protected void addPos2Column()
    {
        final GridColumn<CasterScheduleDTO> posColumn = addGridColumn( "pos2", "Position 2", 140.0, null, null, null, null );
        posColumn.setColumnCellStyleClassProvider( new PosStyleClassCallback( 2 ) );
        posColumn.setColumnCellObjectProvider( param -> param.getPos2() );
    }

    protected void addPos3Column()
    {
        final GridColumn<CasterScheduleDTO> posColumn = addGridColumn( "pos3", "Position 3", 140.0, null, null, null, null );
        posColumn.setColumnCellStyleClassProvider( new PosStyleClassCallback( 3 ) );
        posColumn.setColumnCellObjectProvider( param -> param.getPos3() );
    }

    protected void addPos4Column()
    {
        final GridColumn<CasterScheduleDTO> posColumn = addGridColumn( "pos4", "Position 4", 140.0, null, null, null, null );
        posColumn.setColumnCellStyleClassProvider( new PosStyleClassCallback( 4 ) );
        posColumn.setColumnCellObjectProvider( param -> param.getPos4() );
    }

    protected void addPos5Column()
    {
        final GridColumn<CasterScheduleDTO> posColumn = addGridColumn( "pos5", "Position 5", 140.0, null, null, null, null );
        posColumn.setColumnCellStyleClassProvider( new PosStyleClassCallback( 5 ) );
        posColumn.setColumnCellObjectProvider( param -> param.getPos5() );
    }

    protected void addChargeColumn()
    {
        addGridColumn( "charge", "Charge", 90.0, "-fx-alignment: center", null, chargePropertyConverter, null );
    }

    protected void addCastingSequence()
    {
        addGridColumn( "castingSequence", "Guss-Nr.", 50.0, null, null, null, null );
    }

    protected void addPlannedCalenderWeekColumn()
    {
        addGridColumn( "plannedCalenderWeek", "KW", 30.0, "-fx-alignment: center", null, null, null );
    }

    protected void addMeltingFurnaceColumn()
    {
        addGridColumn( "meltingFurnace", "Ofen", 30.0, "-fx-alignment: center", null, null, null );
    }

    protected void addPlannedLengthColumn()
    {
        addGridColumn( "plannedLength", "Länge", 60.0, "-fx-alignment: center-right", null, null, null );
    }

    protected void addPlannedWeightColumn()
    {
        addGridColumn( "plannedWeight", "Abguß(to)", 60.0, "-fx-alignment: center-right", null, tConverter, null );
    }

    protected void addNetWeightColumn()
    {
        addGridColumn( "netWeight", "Netto(to)", 60.0, "-fx-alignment: center-right", null, tConverter, null );
    }

    protected void addMaxWeightColumn()
    {
        addGridColumn( "maxWeight", "Max.Ofenfüllung(to)", 60.0, "-fx-alignment: center-right", null, null, new StringConverter<CasterScheduleDTO>()
        {
            @Override
            public String toString( CasterScheduleDTO casterScheduleDTO )
            {
                final MachineDTO machineDTO = model.getRelatedEntity( casterScheduleDTO, CastingClientModel.CURRENT_FURNACE );
                if ( machineDTO != null && machineDTO.getMaxWeight() != null )
                {
                    return StringTools.N02F.format( machineDTO.getMaxWeight() / 1000.0 );
                }
                return "";
            }

            @Override
            public CasterScheduleDTO fromString( String string )
            {
                return null;
            }
        } );
    }

    protected void addTransferNorfColumn()
    {
        final GridColumn<CasterScheduleDTO> column = addGridColumn( "transferNorg", "Norf Überführung", 60.0, "-fx-alignment: center-right", null, null, new StringConverter<CasterScheduleDTO>()
        {
            @Override
            public String toString( CasterScheduleDTO casterScheduleDTO )
            {
                return "";
            }

            @Override
            public CasterScheduleDTO fromString( String string )
            {
                return null;
            }
        } );
        column.setEditable( true );
    }

    protected void addMaxCastingLengthColumn()
    {
        addGridColumn( "maxCastingLength", "Max.Gießlänge", 60.0, "-fx-alignment: center-right", null, null, new StringConverter<CasterScheduleDTO>()
        {
            @Override
            public String toString( CasterScheduleDTO casterScheduleDTO )
            {
                final MachineDTO machineDTO = model.getRelatedEntity( casterScheduleDTO, CastingClientModel.CURRENT_CASTER );
                if ( machineDTO != null && machineDTO.getMaxCastingLength() != null )
                {
                    return "" + machineDTO.getMaxCastingLength();
                }
                return "";
            }

            @Override
            public CasterScheduleDTO fromString( String string )
            {
                return null;
            }
        } );
    }

    protected void addPercentageBottomWeightColumn()
    {
        addGridColumn( "percentageBottomWeight", "%Sumpf", 60.0, "-fx-alignment: center-right", null, null, new StringConverter<CasterScheduleDTO>()
        {
            @Override
            public String toString( CasterScheduleDTO casterScheduleDTO )
            {
                final MachineDTO machineDTO = model.getRelatedEntity( casterScheduleDTO, CastingClientModel.CURRENT_FURNACE );
                if ( machineDTO == null || machineDTO.getMaxWeight() == null )
                {
                    return "";
                }
                final Caster caster = model.getEntity( Caster.class, casterScheduleDTO.getMachine() );
                if ( caster == null )
                {
                    return "";
                }
                final Batch batch = caster.findBatch( casterScheduleDTO.getId() );
                if ( batch == null || batch.getTransfers().isEmpty() )
                {
                    return "";
                }
                final Transfer transfer = batch.getTransfers().get( 0 );
                final double bottomWeight = transfer.getBottomWeight();
                return StringTools.N1F.format( bottomWeight / machineDTO.getMaxWeight() * 100.0 );
            }

            @Override
            public CasterScheduleDTO fromString( String string )
            {
                return null;
            }
        } );
    }

    protected void addUtilizationColumn()
    {
        addGridColumn( "utilization", "Auslastung(%)", 60.0, null, new Callback<CasterScheduleDTO, String>()
        {
            @Override
            public String call( CasterScheduleDTO casterScheduleDTO )
            {
                final MachineDTO machineDTO = model.getRelatedEntity( casterScheduleDTO, CastingClientModel.CURRENT_FURNACE );
                if ( machineDTO != null && machineDTO.getMaxWeight() != null )
                {
                    final double maxWeight = machineDTO.getMaxWeight();
                    final double plannedWeight = casterScheduleDTO.getPlannedWeight();
                    final double utilization = plannedWeight / maxWeight;
                    if ( utilization < 0.8 )
                    {
                        final double percent = utilization * 160.0;
                        return "-fx-alignment: center-right;-fx-background-color: derive(#4b80ff, " + StringTools.N1F.format( percent ) + "%)";
                    }
                    else if ( utilization > 1.0 )
                    {
                        return "-fx-alignment: center-right;-fx-background-color: derive(#D42200, 60%);-fx-text-fill: white";
                    }
                }
                return "-fx-alignment: center-right";
            }
        }, null, new StringConverter<CasterScheduleDTO>()
        {
            @Override
            public String toString( CasterScheduleDTO casterScheduleDTO )
            {
                final MachineDTO machineDTO = model.getRelatedEntity( casterScheduleDTO, CastingClientModel.CURRENT_FURNACE );
                if ( machineDTO != null && machineDTO.getMaxWeight() != null )
                {
                    final double maxWeight = machineDTO.getMaxWeight();
                    final double plannedWeight = casterScheduleDTO.getPlannedWeight();
                    return StringTools.N01F.format( ( plannedWeight / maxWeight ) * 100.0 );
                }
                return "";
            }

            @Override
            public CasterScheduleDTO fromString( String string )
            {
                return null;
            }
        } );
    }

    protected void addInProgressTSColumn()
    {
        addGridColumn( "inProgressTS", "Start-Zeit", 120.0, "-fx-alignment: center", null, dateLongConverter, null );
    }

    protected void addShiftColumn()
    {
        addGridColumn( "type", "Schicht", 90.0, "-fx-alignment: center", null, null, null );
    }

    protected <P> GridColumn<P> addGridColumn( String property, String columnHeader, double defaultColumnWidth, String columnStyle, Callback<CasterScheduleDTO, String> cellStyleCallback,
            StringConverter<P> propertyConverter, StringConverter<P> cellConverter )
    {
        final GridColumn gridColumn = new GridColumn( property, columnHeader, defaultColumnWidth, columnStyle, cellStyleCallback, propertyConverter, cellConverter );
        gridColumns.add( gridColumn );
        return gridColumn;
    }

    private double getPercentage( CasterScheduleDTO casterScheduleDTO, String source )
    {
        if ( casterScheduleDTO.getId() < 0 )
        {
            return 0;
        }
        Double concreteValue = null;
        if ( Casting.ALLOY_SOURCES.MELTING_FURNACE_S1.equals( source ) )
        {
            concreteValue = casterScheduleDTO.getPercentageS1();
        }
        else if ( Casting.ALLOY_SOURCES.MELTING_FURNACE_S2.equals( source ) )
        {
            concreteValue = casterScheduleDTO.getPercentageS2();
        }
        else if ( Casting.ALLOY_SOURCES.UBC_S3.equals( source ) )
        {
            concreteValue = casterScheduleDTO.getPercentageS3();
        }
        else if ( Casting.ALLOY_SOURCES.ELEKTROLYSE.equals( source ) )
        {
            concreteValue = casterScheduleDTO.getPercentageEL();
        }
        else if ( Casting.ALLOY_SOURCES.REAL_ALLOY.equals( source ) )
        {
            concreteValue = casterScheduleDTO.getPercentageRA();
        }
        if ( concreteValue != null )
        {
            return concreteValue;
        }
        if ( !( model instanceof CastingClientModel ) )
        {
            return 0;
        }
        final CastingClientModel castingClientModel = (CastingClientModel) model;
        final double percentage = castingClientModel.getContentPercentage( casterScheduleDTO.getMachine(), casterScheduleDTO.getMeltingFurnace(), casterScheduleDTO.getAlloy(), source );
        return percentage;
    }

    public class GridColumn<P>
    {
        private String property;
        private String columnHeader;
        private double defaultColumnWidth;
        private String columnStyle;
        private Callback<CasterScheduleDTO, String> cellStyleCallback;
        private StringConverter<P> propertyConverter;
        private StringConverter<P> cellConverter;
        private boolean ignoreSingleColumn;
        private boolean editable = false;
        private GridPopupHandler<CasterScheduleDTO> popupHandler;
        private Callback<CasterScheduleDTO, Object> columnCellObjectProvider;
        private Callback<CasterScheduleDTO, Node> columnGraphicProvider;
        private Callback<CasterScheduleDTO, String> columnCellStyleClassProvider;

        public GridColumn( String property, String columnHeader, double defaultColumnWidth, String columnStyle, Callback<CasterScheduleDTO, String> cellStyleCallback,
                StringConverter<P> propertyConverter, StringConverter<P> cellConverter )
        {
            this.property = property;
            this.columnHeader = columnHeader;
            this.defaultColumnWidth = defaultColumnWidth;
            this.columnStyle = columnStyle;
            this.cellStyleCallback = cellStyleCallback;
            this.propertyConverter = propertyConverter;
            this.cellConverter = cellConverter;
        }

        public String getProperty()
        {
            return property;
        }

        public String getColumnHeader()
        {
            return columnHeader;
        }

        public double getDefaultColumnWidth()
        {
            return defaultColumnWidth;
        }

        public String getColumnStyle()
        {
            return columnStyle;
        }

        public Callback<CasterScheduleDTO, String> getCellStyleCallback()
        {
            return cellStyleCallback;
        }

        public StringConverter<P> getPropertyConverter()
        {
            return propertyConverter;
        }

        public StringConverter<P> getCellConverter()
        {
            return cellConverter;
        }

        public boolean isIgnoreSingleColumn()
        {
            return ignoreSingleColumn;
        }

        public void setIgnoreSingleColumn( boolean ignoreSingleColumn )
        {
            this.ignoreSingleColumn = ignoreSingleColumn;
        }

        public boolean isEditable()
        {
            return editable;
        }

        public void setEditable( boolean editable )
        {
            this.editable = editable;
        }

        public GridPopupHandler getPopupHandler()
        {
            return popupHandler;
        }

        public void setPopupHandler( GridPopupHandler<CasterScheduleDTO> popupHandler )
        {
            this.popupHandler = popupHandler;
        }

        public Callback<CasterScheduleDTO, Object> getColumnCellObjectProvider()
        {
            return columnCellObjectProvider;
        }

        public void setColumnCellObjectProvider( Callback<CasterScheduleDTO, Object> columnCellObjectProvider )
        {
            this.columnCellObjectProvider = columnCellObjectProvider;
        }

        public Callback<CasterScheduleDTO, Node> getColumnGraphicProvider()
        {
            return columnGraphicProvider;
        }

        public void setColumnGraphicProvider( Callback<CasterScheduleDTO, Node> columnGraphicProvider )
        {
            this.columnGraphicProvider = columnGraphicProvider;
        }

        public Callback<CasterScheduleDTO, String> getColumnCellStyleClassProvider()
        {
            return columnCellStyleClassProvider;
        }

        public void setColumnCellStyleClassProvider( Callback<CasterScheduleDTO, String> columnCellStyleClassProvider )
        {
            this.columnCellStyleClassProvider = columnCellStyleClassProvider;
        }
    }

    private class PosStyleClassCallback implements Callback<CasterScheduleDTO, String>
    {
        private int pos;

        public PosStyleClassCallback( int pos )
        {
            this.pos = pos;
        }

        @Override
        public String call( CasterScheduleDTO param )
        {
            if ( param == null )
            {
                return null;
            }
            if ( "setup".equals( param.getType() ) )
            {
                return null;
            }
            final CasterScheduleDTO prev = param.findPrevCastingBatch();
            if ( prev == null )
            {
                return null;
            }
            CasterSchedulePosDTO posDTO = param.getPos( pos );
            if ( posDTO.getMaterialType() == null )
            {
                return null;
            }
            CasterSchedulePosDTO prevDTO = prev.getPos( pos );
            if ( posDTO == null || prevDTO == null )
            {
                return null;
            }
            if ( posDTO.getWidth() == prevDTO.getWidth() )
            {
                return null;
            }
            return "prev-change";
        }
    }
}
