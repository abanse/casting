package com.hydro.casting.server.ejb.melting;

import com.hydro.casting.server.contract.dto.AlloyDTO;
import com.hydro.casting.server.contract.melting.MeltingAlloyTMP;
import com.hydro.casting.server.ejb.main.service.AlloyService;
import com.hydro.casting.server.model.mat.Alloy;
import com.hydro.core.common.exception.BusinessException;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Stateless
public class MeltingAlloyTMPBean implements MeltingAlloyTMP
{
    private static final Logger log = LoggerFactory.getLogger( MeltingAlloyTMPBean.class );

    @EJB
    private AlloyService alloyService;

    @Override
    public List<AlloyDTO> list( Map<String, Object> context )
    {
        // Only using active alloys
        return alloyService.load().stream().filter( AlloyDTO::isActive ).collect( Collectors.toList() );
    }

    @Override
    public AlloyDTO create( Map<String, Object> context ) throws BusinessException
    {
        log.info( "MeltingAlloyTMPBean.create was called: Invalid call because creation of alloys from melting maintenance is forbidden!" );
        throw new BusinessException( "Invalid call because creation of alloys from melting maintenance is forbidden!" );
    }

    @Override
    public List<AlloyDTO> copy( Map<String, Object> context, List<AlloyDTO> sourceViewDTOs ) throws BusinessException
    {
        log.info( "MeltingAlloyTMPBean.copy was called: Invalid call because copying alloys from melting maintenance is forbidden!" );
        throw new BusinessException( "Invalid call because copying alloys from melting maintenance is forbidden!" );
    }

    @Override
    public AlloyDTO update( Map<String, Object> context, AlloyDTO viewDTO, String attribute, Object oldValue, Object newValue ) throws BusinessException
    {
        log.info( "set value " + attribute + " " + newValue );

        final Alloy alloy = alloyService.findAlloy( viewDTO.getName() );

        try
        {
            MethodUtils.invokeMethod( alloy, "set" + attribute.substring( 0, 1 ).toUpperCase() + attribute.substring( 1 ), newValue );
        }
        catch ( Exception e )
        {
            log.error( "error setting attribute " + attribute, e );
            throw new BusinessException( "Allgemeiner Fehler", e );
        }

        alloyService.replicateCache( alloy );
        return alloyService.load( alloy );
    }

    @Override
    public void delete( Map<String, Object> context, List<AlloyDTO> viewDTOs ) throws BusinessException
    {
        log.info( "MeltingAlloyTMPBean.delete was called: Invalid call because deletion of alloys from melting maintenance is forbidden!" );
        throw new BusinessException( "Invalid call because deletion of alloys from melting maintenance is forbidden!" );
    }
}
