package com.hydro.casting.server.ejb.mail;

import com.hydro.core.common.util.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

@Stateless
public class MailService
{
    private final static Logger log = LoggerFactory.getLogger( MailService.class );

    @Resource( name = "java:jboss/mail/hydro" )
    private Session session;

    @Asynchronous
    public void sendTestMail( String sender, String recipients, String subject, String mailContent )
    {
        sendMail( sender, recipients, null, subject, mailContent );
    }

    private void sendMail( String sender, String recipients, String blindCopyRecipients, String subject, String mailText )
    {
        Message mail = new MimeMessage( session );
        try
        {
            mail.setFrom( new InternetAddress( sender ) );
            mail.setRecipients( Message.RecipientType.TO, InternetAddress.parse( recipients ) );
            if ( !StringTools.isNullOrEmpty( blindCopyRecipients ) )
            {
                mail.setRecipients( Message.RecipientType.BCC, InternetAddress.parse( blindCopyRecipients ) );
            }
            mail.setSubject( subject );

            MimeMultipart multipart = new MimeMultipart();

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText( mailText );
            multipart.addBodyPart( messageBodyPart );

            mail.setContent( multipart );

            Transport.send( mail );
        }
        catch ( MessagingException me )
        {
            // momentan ignorieren bis alles eingerichtet ist
            // throw new BusinessException( "Fehler beim senden der Mail", me );
            log.warn( "Fehler beim Mail versenden. Wird momentan ignoriert", me );
        }
    }
}
