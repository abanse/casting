package com.hydro.casting.gui.melting.dialog;

import com.hydro.casting.common.Casting;
import com.hydro.casting.gui.melting.dialog.result.ConfigureMeltingChargeResult;
import com.hydro.casting.server.contract.dto.MachineDTO;
import com.hydro.core.common.exception.BusinessException;
import com.hydro.core.common.product.COREProductDefinition;
import com.hydro.core.common.util.StringTools;
import com.hydro.core.gui.comp.IntegerTextField;
import com.hydro.core.gui.validation.MESValidationSupport;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.FutureTask;

public class ConfigureMeltingChargeDialog extends Dialog<ConfigureMeltingChargeResult>
{
    public ConfigureMeltingChargeDialog( String melterApk, MachineDTO melterMachineDTO )
    {
        final DialogPane dialogPane = getDialogPane();
        dialogPane.getStylesheets().add( Objects.requireNonNull( getClass().getResource( COREProductDefinition.getInstance().getStylesheet() ) ).toExternalForm() );

        // Basic dialog setup
        setTitle( "Konfiguration für neue Schmelzchargen" );
        dialogPane.setHeaderText( "Bitte geben sie die Parameter ein, welche für neue Schmelzchargen angewendet werden sollen" );
        dialogPane.getButtonTypes().addAll( ButtonType.OK, ButtonType.CANCEL );

        // Setup for fields requiring more than one line of setup code
        // Charge counter
        IntegerTextField chargeCounter = new IntegerTextField();
        String currentChargeCounter;
        if ( melterMachineDTO.getLastCharge() != null )
        {
            currentChargeCounter = StringTools.N4F.format( melterMachineDTO.getLastCharge() );
        }
        else
        {
            // Create a "new" charge counter
            currentChargeCounter = Casting.getNextCharge( melterApk, null ).substring( 3 );
        }
        chargeCounter.setText( currentChargeCounter );

        // Input validation for the charge number to ensure correct formatting
        final MESValidationSupport validationSupport = new MESValidationSupport();
        chargeCounter.addValidationGreaterThan( validationSupport, 0, 9999, "Chargen-Zähler" );
        validationSupport.initInitialDecoration();
        validationSupport.validationResultProperty().addListener( ( p, o, n ) -> {
            final Node okButton = dialogPane.lookupButton( ButtonType.OK );
            if ( okButton != null )
            {
                okButton.setDisable( n != null && n.getErrors() != null && !n.getErrors().isEmpty() );
            }
        } );

        // Adding dialog elements to a VBox for proper ordering
        VBox parameters = new VBox();
        parameters.setAlignment( Pos.CENTER );
        parameters.setPadding( new Insets( 5., 5., 5., 5. ) );
        parameters.getChildren().add( new Label( "Charge:" ) );
        parameters.getChildren().add( chargeCounter );

        // Creating BorderPane as container for the VBox
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter( parameters );

        // Adding the container to the dialog pane
        dialogPane.setContent( borderPane );

        // Mandatory result converter for the dialog
        setResultConverter( buttonType -> {

            if ( buttonType != ButtonType.OK )
            {
                return null;
            }
            ConfigureMeltingChargeResult result = new ConfigureMeltingChargeResult();

            result.setChargeCounter( chargeCounter.getIntValue() );

            return result;
        } );
    }

    public static ConfigureMeltingChargeResult showDialog( String machineApk, MachineDTO melterMachineDTO ) throws BusinessException
    {
        if ( Platform.isFxApplicationThread() )
        {
            final ConfigureMeltingChargeDialog dialog = new ConfigureMeltingChargeDialog( machineApk, melterMachineDTO );
            final Optional<ConfigureMeltingChargeResult> result = dialog.showAndWait();

            return result.orElse( null );
        }
        else
        {
            FutureTask<Optional<ConfigureMeltingChargeResult>> future = new FutureTask<>( () -> {
                final ConfigureMeltingChargeDialog dialog = new ConfigureMeltingChargeDialog( machineApk, melterMachineDTO );
                return dialog.showAndWait();
            } );
            Platform.runLater( future );
            Optional<ConfigureMeltingChargeResult> result = Optional.empty();
            try
            {
                result = future.get();
            }
            catch ( Exception ex )
            {
                // do nothing
            }
            return result.orElse( null );
        }
    }
}
