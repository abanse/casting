package com.hydro.casting.gui.prod.table.cell;

import com.hydro.casting.server.contract.dto.SpecificationElementDTO;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.converter.DoubleStringConverter;

import java.text.DecimalFormat;

public class SpecificationElementValueCellFactory implements Callback<TableColumn<SpecificationElementDTO, Double>, TableCell<SpecificationElementDTO, Double>>
{
    final DecimalFormat format = new DecimalFormat( "0" );

    @Override
    public TableCell<SpecificationElementDTO, Double> call( TableColumn<SpecificationElementDTO, Double> param )
    {
        return new TextFieldTableCell<>()
        {
            {
                setConverter( new DoubleStringConverter()
                {
                    @Override
                    public Double fromString( String value )
                    {
                        String doubleString = value;
                        if ( doubleString != null && doubleString.contains( "," ))
                        {
                            doubleString = doubleString.replace( ',', '.' );
                        }
                        return super.fromString( doubleString );
                    }
                });
            }

            @Override
            public void updateItem( Double item, boolean empty )
            {
                super.updateItem( item, empty );

                if ( item == null || getTableRow() == null )
                {
                    setText( null );
                    return;
                }

                final SpecificationElementDTO specificationElementDTO = getTableRow().getItem();
                if ( specificationElementDTO == null )
                {
                    setText( null );
                    return;
                }
                final int precision = specificationElementDTO.getPrecision();
                format.setMinimumFractionDigits( precision );
                format.setMaximumFractionDigits( precision );
                setText( format.format( item ) );
            }
        };
    }
}
