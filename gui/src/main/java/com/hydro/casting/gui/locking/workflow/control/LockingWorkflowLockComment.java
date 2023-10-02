package com.hydro.casting.gui.locking.workflow.control;

import com.hydro.casting.common.Casting;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LockingWorkflowLockComment
{
    public String getComment()
    {
        return comment;
    }

    public void setComment( String comment )
    {
        this.comment = comment;
    }

    private final static DateFormat TSDF = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
    private final static DateFormat DF = new SimpleDateFormat( "dd.MM.yy HH:mm:ss" );

    String text, time, user, comment, confirmation;
    private boolean withConfirmation, ok;
    private boolean avRemark;

    public LockingWorkflowLockComment( String text )
    {
        super();
        this.text = text;
        int timeStart = text.indexOf( ' ' ) + 1;
        this.user = text.substring( 0, timeStart - 1 );
        this.time = text.substring( timeStart, timeStart + 19 );
        Date timestamp = null;
        try
        {
            timestamp = TSDF.parse( time );
            this.time = DF.format( timestamp );
        }
        catch ( ParseException e )
        {
            // ignore
        }
        int nConfirmation = text.indexOf( Casting.LOCKING_WORKFLOW.COMMENT_MARK );

        int nConfirmationOld = text.indexOf( Casting.LOCKING_WORKFLOW.COMMENT_MARK_OLD );

        String commentMark = Casting.LOCKING_WORKFLOW.COMMENT_MARK;

        if ( nConfirmationOld != -1 )
        {
            nConfirmation = nConfirmationOld;
            commentMark = Casting.LOCKING_WORKFLOW.COMMENT_MARK_OLD;

        }

        if ( nConfirmation != -1 )
        {
            this.comment = text.substring( timeStart + 20, nConfirmation );
            String allConfirmation = text.substring( nConfirmation );
            if ( allConfirmation.length() > commentMark.length() && //es gibt Text nach der Marke
                    allConfirmation.equals( commentMark + Casting.LOCKING_WORKFLOW.FREE_MARK + Casting.LOCKING_WORKFLOW.OK ) || allConfirmation.equals(
                    commentMark + Casting.LOCKING_WORKFLOW.SCRAP_MARK + Casting.LOCKING_WORKFLOW.OK ) || allConfirmation.equals(
                    commentMark + Casting.LOCKING_WORKFLOW.CONTAINER_MARK + Casting.LOCKING_WORKFLOW.OK ) || allConfirmation.equals(
                    commentMark + Casting.LOCKING_WORKFLOW.CONTAINER_MARK + Casting.LOCKING_WORKFLOW.TABG_MARK + Casting.LOCKING_WORKFLOW.OK ) || allConfirmation.equals(
                    commentMark + Casting.LOCKING_WORKFLOW.SCRAP_MARK + Casting.LOCKING_WORKFLOW.TABG_MARK + Casting.LOCKING_WORKFLOW.OK ) )

            {
                this.withConfirmation = true;
                this.avRemark = false;
                this.ok = true;
                this.comment = text.substring( timeStart + 20, nConfirmation );

                final String allActions = allConfirmation.substring( commentMark.length() );
                final String action = allActions.substring( 0, 1 );
                boolean tabg = false;

                if ( allActions.length() > 1 && allActions.substring( 1, 2 ).equals( Casting.LOCKING_WORKFLOW.TABG_MARK ) )
                {
                    tabg = true;
                }
                switch ( action )
                {
                case Casting.LOCKING_WORKFLOW.FREE_MARK:
                    this.confirmation = Casting.LOCKING_WORKFLOW.FREE_OK;
                    break;
                case Casting.LOCKING_WORKFLOW.SCRAP_MARK:
                    this.confirmation = tabg ? Casting.LOCKING_WORKFLOW.SCRAP_TABG_OK : Casting.LOCKING_WORKFLOW.SCRAP_OK;
                    break;
                case Casting.LOCKING_WORKFLOW.CONTAINER_MARK:
                    this.confirmation = tabg ? Casting.LOCKING_WORKFLOW.CONTAINER_TABG_OK : Casting.LOCKING_WORKFLOW.CONTAINER_OK;
                    break;
                default:
                    break;
                }
            }
            else
            {
                String allActions = allConfirmation.substring( commentMark.length() );
                if ( allActions.startsWith( Casting.LOCKING_WORKFLOW.ERROR_MARK ) )
                {
                    this.withConfirmation = true;
                    this.avRemark = false;
                    this.ok = false;
                    this.comment = text.substring( timeStart + 20, nConfirmation );
                    this.confirmation = "Fehler " + allActions.substring( 1 );
                }
                if ( allActions.equals( Casting.LOCKING_WORKFLOW.AV_MARK ) )
                {
                    this.withConfirmation = false;
                    this.avRemark = true;
                }
            }
        }
        else
        {
            this.comment = text.substring( timeStart + 20 ).trim();
            this.withConfirmation = false;
            this.ok = false;
            this.confirmation = "";
        }

    }

    public boolean isAvRemark()
    {
        return avRemark;
    }

    public void setAvRemark( boolean avRemark )
    {
        this.avRemark = avRemark;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime( String time )
    {
        this.time = time;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser( String user )
    {
        this.user = user;
    }

    public String getConfirmation()
    {
        return confirmation;
    }

    public void setConfirmation( String confirmation )
    {
        this.confirmation = confirmation;
    }

    public boolean isWithConfirmation()
    {
        return withConfirmation;
    }

    public void setWithConfirmation( boolean withConfirmation )
    {
        this.withConfirmation = withConfirmation;
    }

    public boolean isOk()
    {
        return ok;
    }

    public void setOk( boolean ok )
    {
        this.ok = ok;
    }

}
