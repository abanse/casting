package com.hydro.casting.gui.melting.dialog;

import com.google.inject.Inject;
import com.hydro.casting.gui.melting.dialog.result.NewMeltingChargeResult;
import com.hydro.core.common.exception.BusinessException;
import com.hydro.core.common.product.COREProductDefinition;
import com.hydro.core.gui.ClientModelManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.FutureTask;

public class NewMeltingChargeDialog extends Dialog<NewMeltingChargeResult>
{
    @Inject
    private ClientModelManager clientModelManager;

    public NewMeltingChargeDialog( List<String> alloyEntries )
    {
        final DialogPane dialogPane = getDialogPane();
        dialogPane.getStylesheets().add( Objects.requireNonNull( getClass().getResource( COREProductDefinition.getInstance().getStylesheet() ) ).toExternalForm() );

        // Basic dialog setup
        setTitle( "Parameter für die neue Schmelzcharge" );
        dialogPane.setHeaderText( "Bitte geben sie die Parameter für die neue Schmelzcharge an" );
        dialogPane.getButtonTypes().addAll( ButtonType.OK, ButtonType.CANCEL );

        // Setup of the elements in the dialog which require more than one line for setup
        // Alloy selection
        ComboBox<String> alloySelection = new ComboBox<>();
        alloySelection.getItems().addAll( alloyEntries );

        // Adding dialog elements to a VBox for proper ordering
        VBox parameters = new VBox();
        parameters.setAlignment( Pos.CENTER );
        parameters.setPadding( new Insets( 5., 5., 5., 5. ) );
        parameters.getChildren().add( new Label( "Legierung:" ) );
        parameters.getChildren().add( alloySelection );

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
            NewMeltingChargeResult result = new NewMeltingChargeResult();

            result.setAlloy( alloySelection.getValue() );

            return result;
        } );
    }

    public static NewMeltingChargeResult showDialog( List<String> alloyEntries ) throws BusinessException
    {
        if ( Platform.isFxApplicationThread() )
        {
            final NewMeltingChargeDialog dialog = new NewMeltingChargeDialog( alloyEntries );
            final Optional<NewMeltingChargeResult> result = dialog.showAndWait();

            return result.orElse( null );
        }
        else
        {
            FutureTask<Optional<NewMeltingChargeResult>> future = new FutureTask<>( () -> {
                final NewMeltingChargeDialog dialog = new NewMeltingChargeDialog( alloyEntries );
                return dialog.showAndWait();
            } );
            Platform.runLater( future );
            Optional<NewMeltingChargeResult> result = Optional.empty();
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
