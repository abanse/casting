package com.hydro.casting.gui.reporting.util;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.hydro.casting.gui.reporting.view.OutputDetailSummaryController;
import com.hydro.casting.server.contract.reporting.dto.ReportingOutputSummaryDTO;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;

@Singleton
public class ReportingViewUtils
{
    @Inject
    private Injector injector;

    public Node createOutputSummary( ReportingOutputSummaryDTO summaryDTO )
    {
        Node outputSummaryNode = null;
        if ( summaryDTO != null )
        {
            FXMLLoader loader = new FXMLLoader( getClass().getResource( "/com/hydro/casting/gui/reporting/view/OutputDetailSummary.fxml" ) );
            loader.setControllerFactory( injector::getInstance );
            try
            {
                outputSummaryNode = loader.load();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
            OutputDetailSummaryController outputSummary = loader.getController();
            outputSummary.setText( summaryDTO.getTitle() );
            outputSummary.setPrefWidth( 250. );
            summaryDTO.getEntries().forEach( ( key, value ) -> outputSummary.addNewOutputSummary( key, value, false ) );
        }

        return outputSummaryNode;
    }
}
