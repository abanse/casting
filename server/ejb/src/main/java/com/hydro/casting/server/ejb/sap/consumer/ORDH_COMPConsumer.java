package com.hydro.casting.server.ejb.sap.consumer;

import com.hydro.core.common.exception.BusinessException;
import com.hydro.eai.sap.contract.consumer.SAPMessageListener;
import com.hydro.eai.sap.contract.message.SAPMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import java.util.Map;

@Stateless(name = "ORDH_COMP")
public class ORDH_COMPConsumer implements SAPMessageListener
{
    private final static Logger log = LoggerFactory.getLogger(ORDH_COMPConsumer.class);

    @Override
    public String getConsumerName()
    {
        return "ORDH_COMP";
    }

    @Override
    public void consume( Map<String, String> context, SAPMessage message ) throws BusinessException
    {
        log.debug( "ORDH_COMP consumer " + message.getArt() );
    }
}
