package com.hydro.casting.gui.analysis.control.cell;

import com.hydro.casting.gui.model.*;
import com.hydro.casting.gui.model.common.AnalysisCalculator;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Format;

public class FormattedTableCellFactory<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>>
{
    private boolean showNullValues = true;
    private int[] coloredRows = null;
    private int minSpecRow = -1;
    private int maxSpecRow = -1;
    private TextAlignment alignment;
    private Format format;

    // private int[] lineIndex = null;

    private Specification specification;
    private Batch batch;
    private Analysis checkedAnalysis;

    private Callback<Integer, Boolean> editableCallback = null;

    public TextAlignment getAlignment()
    {
        return alignment;
    }

    public void setAlignment( TextAlignment alignment )
    {
        this.alignment = alignment;
    }

    public Format getFormat()
    {
        return format;
    }

    public void setFormat( Format format )
    {
        this.format = format;
    }

    public boolean isShowNullValues()
    {
        return showNullValues;
    }

    public void setShowNullValues( boolean showNullValues )
    {
        this.showNullValues = showNullValues;
    }

    public int[] getColoredRows()
    {
        return coloredRows;
    }

    public void setColoredRows( int[] coloredRows )
    {
        this.coloredRows = coloredRows;
    }

    public int getMinSpecRow()
    {
        return minSpecRow;
    }

    public void setMinSpecRow( int minSpecRow )
    {
        this.minSpecRow = minSpecRow;
    }

    public int getMaxSpecRow()
    {
        return maxSpecRow;
    }

    public void setMaxSpecRow( int maxSpecRow )
    {
        this.maxSpecRow = maxSpecRow;
    }

    public Specification getSpecification()
    {
        return specification;
    }

    public void setSpecification( Specification specification )
    {
        this.specification = specification;
    }

    public Batch getBatch()
    {
        return batch;
    }

    public void setBatch( Batch batch )
    {
        this.batch = batch;
    }

    public Analysis getCheckedAnalysis()
    {
        return checkedAnalysis;
    }

    public void setCheckedAnalysis( Analysis checkedAnalysis )
    {
        this.checkedAnalysis = checkedAnalysis;
    }

    @Override
    public TableCell<S, T> call( TableColumn<S, T> p )
    {
        MyTableCell tableCell = new MyTableCell();
        tableCell.setTextAlignment( alignment );

        switch ( alignment )
        {
        case CENTER:
            tableCell.setAlignment( Pos.CENTER );
            break;
        case RIGHT:
            tableCell.setAlignment( Pos.CENTER_RIGHT );
            break;
        default:
            tableCell.setAlignment( Pos.CENTER_LEFT );
            break;
        }
        return tableCell;
    }

    public void setEditableCallback( Callback<Integer, Boolean> editableCallback )
    {
        this.editableCallback = editableCallback;
    }

    public class MyTableCell extends TextFieldTableCell<S, T>
    {
        private FlowPane elementValueFP = new FlowPane();
        private Text elementValueT = new Text();
        private Text elementOriginalValueT = new Text();

        @SuppressWarnings( { "unchecked", "rawtypes" } )
        public MyTableCell()
        {
            elementValueFP.setHgap( 5.0 );
            elementValueFP.setAlignment( Pos.CENTER_RIGHT );
            elementValueFP.getChildren().addAll( elementValueT, elementOriginalValueT );
            setConverter( new StringConverter()
            {
                @Override
                public String toString( Object object )
                {
                    if ( object == null )
                    {
                        return null;
                    }
                    if ( object instanceof Number )
                    {
                        return CompositionElement.ELEMENT_FORMATTER.format( object );
                    }
                    return object.toString();
                }

                @Override
                public Object fromString( String string )
                {
                    if ( string == null || string.length() == 0 )
                    {
                        return 0;
                    }
                    if ( string.contains( "," ) )
                    {
                        string = string.replace( ',', '.' );
                    }
                    return Double.valueOf( string );
                }
            } );
        }

        @SuppressWarnings( "unchecked" )
        @Override
        public void updateItem( Object item, boolean empty )
        {
            super.updateItem( (T) item, empty );

            if ( getTableRow() == null )
            {
                setStyle( null );
                return;
            }

            final S rowObject = getTableRow().getItem();
            int row = this.getTableView().getItems().indexOf( rowObject );

            if ( editableCallback != null )
            {
                setEditable( editableCallback.call( row ) );
            }

            String style = null;
            DecimalFormat specFormat = null;
            boolean colorized = false;
            if ( specification != null )
            {
                specFormat = new DecimalFormat( "0" );

                boolean showColors = false;
                if ( coloredRows == null )
                {
                    showColors = row != 0 && row != 2;
                }
                else
                {
                    for ( int coloredRow : coloredRows )
                    {
                        if ( row == coloredRow )
                        {
                            showColors = true;
                            break;
                        }
                    }
                }

                // Only color the current cell if the row is not 0 or 2 (reserved for displaying the specification), and an item is present
                if ( showColors && getItem() != null )
                {
                    int decimalPlaces = AnalysisCalculator.getDecimalPlaces( specification, getTableColumn().getId() );
                    if ( decimalPlaces > 0 )
                    {
                        specFormat.setMinimumFractionDigits( decimalPlaces );
                        specFormat.setMaximumFractionDigits( decimalPlaces );
                    }

                    final double analysisValue = AnalysisCalculator.round( (Double) getItem(), decimalPlaces );
                    double maxValue = analysisValue;
                    double minValue = analysisValue;
                    if ( specification.getMax() != null )
                    {
                        maxValue = AnalysisCalculator.round( specification.getMax().getCompositionElementValue( getTableColumn().getId() ), decimalPlaces );
                    }

                    if ( specification.getMin() != null )
                    {
                        minValue = AnalysisCalculator.round( specification.getMin().getCompositionElementValue( getTableColumn().getId() ), decimalPlaces );
                    }

                    // Checking for 0 values to prevent NaN / 0 values being displayed as below minimum
                    if ( analysisValue != 0d )
                    {
                        if ( maxValue != 0d && analysisValue > maxValue )
                        {
                            style = "-fx-background-color: #BF2A23; -fx-text-fill: white;";
                            colorized = true;
                        }

                        if ( minValue != 0d && analysisValue < minValue )
                        {
                            style = "-fx-background-color: #2367bf; -fx-text-fill:white;";
                            colorized = true;
                        }
                    }
                }
                else if ( item instanceof Number )
                {
                    int decimalPlaces = AnalysisCalculator.getDecimalPlaces( specification, getTableColumn().getId() );
                    if ( decimalPlaces > 0 )
                    {
                        specFormat.setMinimumFractionDigits( decimalPlaces );
                        specFormat.setMaximumFractionDigits( decimalPlaces );
                    }
                }
            }
            if ( !isSelected() && row == 1 )
            {
                boolean overload = false;
                if ( batch != null )
                {
                    if ( item instanceof Number )
                    {
                        final double weight = ( (Number) item ).doubleValue();
                        if ( Math.round( weight ) > Math.round( batch.getFurnaceTargetWeight() ) )
                        {
                            overload = true;
                        }
                    }
                }

                if ( overload )
                {
                    style = "-fx-background-color: #BF2A23;-fx-text-fill:white;";
                }
            }
            // boolean paintLine = false;
            // if (lineIndex != null)
            // {
            // for (int line : lineIndex)
            // {
            // if (row == line)
            // {
            // paintLine = true;
            // break;
            // }
            // }
            // }
            // if (paintLine)
            // {
            // if (style != null)
            // {
            // style = style + "-fx-border-color: -fx-table-cell-border-color -fx-table-cell-border-color grey
            // -fx-table-cell-border-color; -fx-border-width: 0 1 4 0;";
            // }
            // else
            // {
            // style = "-fx-border-color: -fx-table-cell-border-color -fx-table-cell-border-color grey
            // -fx-table-cell-border-color; -fx-border-width: 0 1 4 0;";
            // }
            // }

            setStyle( style );

            if ( item == null )
            {
                super.setText( null );
                super.setGraphic( null );
            }
            else if ( !showNullValues && item instanceof Number && ( (Number) item ).doubleValue() == 0.0 )
            {
                super.setText( null );
                super.setGraphic( null );
            }
            else if ( specFormat != null )
            {
                double compositionElementFactor = 1.0;
                if ( rowObject instanceof Composition )
                {
                    Composition composition = (Composition) rowObject;
                    CompositionElement compositionElement = composition.getCompositionElement( getTableColumn().getId() );
                    if ( compositionElement != null )
                    {
                        compositionElementFactor = compositionElement.getElementFactor();
                    }
                }
                if ( compositionElementFactor < 1.0 && item instanceof Number )
                {
                    super.setText( null );
                    elementValueT.setStyle( null );
                    if ( colorized )
                    {
                        elementValueT.setFill( Color.WHITE );
                        elementOriginalValueT.setFill( Color.LIGHTGREY );
                    }
                    else
                    {
                        elementValueT.setFill( Color.BLACK );
                        elementOriginalValueT.setFill( Color.GREY );
                    }
                    elementValueT.setText( formatItem( specFormat, item ) );
                    elementOriginalValueT.setText( formatItem( specFormat, ( (Number) item ).doubleValue() / compositionElementFactor ) );
                    super.setGraphic( elementValueFP );
                }
                else
                {
                    if ( minSpecRow >= 0 && minSpecRow == row && specification != null && specification.getMin() != null && item instanceof Number )
                    {
                        final Double minWarningValue = specification.getMin().getCompositionWarningElementValue( getTableColumn().getId() );

                        if ( minWarningValue != null && minWarningValue >= 0 )
                        {
                            final Double minOriginalValue = specification.getMin().getCompositionOriginalElementValue( getTableColumn().getId() );
                            if ( minOriginalValue != null )
                            {
                                elementValueT.setText( formatItem( specFormat, minOriginalValue ) );
                                elementValueT.setStyle( "-fx-fill:#ababab" );
                                elementOriginalValueT.setText( formatItem( specFormat, minWarningValue ) );
                                super.setGraphic( elementValueFP );
                                super.setText( null );
                                return;
                            }
                        }
                    }
                    if ( maxSpecRow >= 0 && maxSpecRow == row && specification != null && specification.getMax() != null && item instanceof Number )
                    {
                        final Double maxWarningValue = specification.getMax().getCompositionWarningElementValue( getTableColumn().getId() );

                        if ( maxWarningValue != null && maxWarningValue >= 0 )
                        {
                            final Double maxOriginalValue = specification.getMax().getCompositionOriginalElementValue( getTableColumn().getId() );
                            if ( maxOriginalValue != null )
                            {
                                elementValueT.setText( formatItem( specFormat, maxOriginalValue ) );
                                elementValueT.setStyle( "-fx-fill:#ababab" );
                                elementOriginalValueT.setText( formatItem( specFormat, maxWarningValue ) );
                                super.setGraphic( elementValueFP );
                                super.setText( null );
                                return;
                            }
                        }
                    }

                    if ( format != null )
                    {
                        super.setText( format.format( item ) );
                    }
                    else
                    {
                        super.setText( formatItem( specFormat, item ) );
                    }
                    super.setGraphic( null );
                }
            }
            else if ( format != null )
            {
                super.setText( format.format( item ) );
                super.setGraphic( null );
            }
            else if ( item instanceof Node )
            {
                super.setText( null );
                super.setGraphic( (Node) item );
            }
            else
            {
                super.setText( item.toString() );
                super.setGraphic( null );
            }
        }

        private String formatItem( DecimalFormat formatter, Object item )
        {
            // double formatting requires conversion to BigDecimal first for correct rounding
            if ( item instanceof Double && !Double.isNaN( (Double) item ) )
            {
                return formatter.format( BigDecimal.valueOf( (Double) item ) );
            }

            return formatter.format( item );
        }
    }
}