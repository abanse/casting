package com.hydro.casting.gui.locking.workflow.control;

import com.hydro.casting.gui.locking.workflow.task.LWAddMessageTask;
import com.hydro.casting.server.contract.locking.workflow.dto.LockingWorkflowDTO;
import com.hydro.core.common.util.StringTools;
import com.hydro.core.gui.comp.TaskButton;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

public class LockingWorkflowStepsControlController extends LockingWorkflowDetailController<LockingWorkflowDTO>
{
    // @FXML
    // private WebView lockComment;
    @FXML
    private SwingNode lockCommentContainer;
    private JEditorPane lockComment;

    @FXML
    private TaskButton addMessage;

    @FXML
    private LWAddMessageTask addMessageTask;

    public LockingWorkflowStepsControlController()
    {
        super( LockingWorkflowDTO.class );
    }

    /**
     * Initializes the controller class.
     */
    @FXML
    private void initialize()
    {
        SwingUtilities.invokeLater( () -> {

            // create jeditorpane
            lockComment = new JEditorPane();

            // make it read-only
            lockComment.setEditable( false );

            // add an html editor kit
            HTMLEditorKit kit = new HTMLEditorKit();
            lockComment.setEditorKit( kit );

            // add some styles to the html
            StyleSheet styleSheet = kit.getStyleSheet();
            styleSheet.addRule( "body {color:#000; font-family:sans-serif; margin: 4px; }" );

            // create a document, set it on the jeditorpane, then add the html
            Document doc = kit.createDefaultDocument();
            lockComment.setDocument( doc );

            JScrollPane lockCommentSP = new JScrollPane( lockComment );

            lockCommentContainer.setContent( lockCommentSP );
        } );

        // lockComment.getEngine().loadContent( "<html></html>" );
    }

    @Override
    public void loadData( LockingWorkflowDTO data )
    {
        LockingWorkflowDTO lockingWorkflowDTO = data;
        if ( data != null && data.getChilds() != null )
        {
            lockingWorkflowDTO = lockingWorkflowDTO.getChilds().get( 0 );
        }

        final String html;
        if ( lockingWorkflowDTO != null && StringTools.isFilled( lockingWorkflowDTO.getLockComment() ) )
        {
            // html = getHTML( lockingWorkflowDTO.getLockComment(), true );
            html = getHTML( lockingWorkflowDTO.getLockComment(), false );
        }
        else
        {
            html = "<html></html>";
        }

        addMessageTask.setData( null );
        if ( lockingWorkflowDTO != null )
        {
            addMessageTask.setData( lockingWorkflowDTO );
        }

        // lockComment.getEngine().loadContent( html.toString() );
        SwingUtilities.invokeLater( () -> {
            lockComment.setText( html );
        } );
    }

    public static String getHTML( String lockCommentString, boolean withScripts )
    {
        StringBuilder html = new StringBuilder().append( "<html>" );
        if ( withScripts )
        {
            html.append( "<head>" );
            html.append( "   <script language=\"javascript\" type=\"text/javascript\">" );
            html.append( "       function toBottom(){" );
            html.append( "           window.scrollTo(0, document.body.scrollHeight);" );
            html.append( "       }" );
            html.append( "   </script>" );
            html.append( "   <style>" );
            html.append( "       body {font: 12px Arial,sans-serif;}" );
            html.append( "   </style>" );
            html.append( "</head>" );
            html.append( "<body onload='toBottom()'>" );
        }
        else
        {
            html.append( "<body>" );
        }

        if ( !StringTools.isNullOrEmpty( lockCommentString ) ) // Neufeld 8.2.2018
        {
            String[] lockComments = lockCommentString.split( "\\|" );
            for ( String lockCommentRow : lockComments )
            {

                LockingWorkflowLockComment lockCommentText = new LockingWorkflowLockComment( lockCommentRow );
                String user = lockCommentText.getUser();
                String time = lockCommentText.getTime();
                String commentText = lockCommentText.getComment();
                commentText = StringEscapeUtils.unescapeJava( commentText );// Für Sonderzeichen
                html.append( user + " " + time + "<p>" );
                if ( lockCommentText.isAvRemark() )
                {
                    html.append( "<font color='#000000'><b>" + commentText + "</b></font><p>" );
                }
                else
                {
                    html.append( "<font color='#1d4eab'>" + commentText + "</font><p>" );
                }
                if ( lockCommentText.isWithConfirmation() )
                {
                    if ( lockCommentText.isOk() )
                    {
                        html.append( "<font color='#006400'>" + lockCommentText.getConfirmation() + "</font><p>" );
                    }
                    else
                    {
                        html.append( "<font color='#ff0000'>" + lockCommentText.getConfirmation() + "</font><p>" );
                    }
                }

            }
        }
        html.append( "</body></html>" );

        return html.toString();
    }
}
