package com.hydro.casting.server.ejb.planning;

import com.hydro.casting.server.contract.dto.CastingKTDTO;
import com.hydro.casting.server.contract.planning.CastingKTTMP;
import com.hydro.casting.server.ejb.planning.service.CastingKTService;
import com.hydro.casting.server.model.sched.CastingKT;
import com.hydro.casting.server.model.sched.dao.CastingKTHome;
import com.hydro.core.common.exception.BusinessException;
import com.hydro.core.common.exception.BusinessObjectNotFoundException;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Stateless
public class CastingKTTMPBean implements CastingKTTMP
{
    private final static Logger log = LoggerFactory.getLogger( CastingKTTMPBean.class );

    @EJB
    private CastingKTService castingKTService;

    @EJB
    private CastingKTHome castingKTHome;

    @Override
    public List<CastingKTDTO> list( Map<String, Object> context )
    {
        return castingKTService.load();
    }

    @Override
    public CastingKTDTO update( Map<String, Object> context, CastingKTDTO viewDTO, String attribute, Object oldValue, Object newValue ) throws BusinessException
    {
        log.info( "set value " + attribute + " " + newValue );

        final CastingKT castingKT = castingKTHome.findById( viewDTO.getId() );

        try
        {
            MethodUtils.invokeMethod( castingKT, "set" + attribute.substring( 0, 1 ).toUpperCase() + attribute.substring( 1 ), newValue );
        }
        catch ( Exception e )
        {
            log.error( "error setting attribute " + attribute, e );
            throw new BusinessException( "Allgemeiner Fehler", e );
        }

        castingKTService.replicateCache( castingKT );
        return castingKTService.load( castingKT );
    }

    @Override
    public void delete( Map<String, Object> context, List<CastingKTDTO> viewDTOs ) throws BusinessException
    {
        final List<Long> removedIds = new ArrayList<>();
        for ( CastingKTDTO viewDTO : viewDTOs )
        {
            final CastingKT castingKT = castingKTHome.findById( viewDTO.getId() );

            if ( castingKT == null )
            {
                throw new BusinessObjectNotFoundException( "CastingKT", viewDTO.getId() );
            }

            castingKTHome.remove( castingKT );
            removedIds.add( viewDTO.getId() );
        }

        castingKTService.removeFromCache( removedIds );
    }

    @Override
    public CastingKTDTO create( Map<String, Object> context )
    {
        final CastingKT castingKT = new CastingKT();
        castingKTHome.persist( castingKT );

        castingKTService.replicateCache( castingKT );

        return castingKTService.load( castingKT );
    }

    @Override
    public List<CastingKTDTO> copy( Map<String, Object> context, List<CastingKTDTO> sourceViewDTOs )
    {
        final List<CastingKTDTO> targetViewDTOs = new ArrayList<>();
        final List<CastingKT> targetCastingKTs = new ArrayList<>();
        for ( CastingKTDTO sourceViewDTO : sourceViewDTOs )
        {
            final CastingKT source = castingKTHome.findById( sourceViewDTO.getId() );

            final CastingKT copy = castingKTHome.detach( source );
            castingKTHome.persist( copy );

            targetViewDTOs.add( castingKTService.load( copy ) );
            targetCastingKTs.add( copy );
        }

        castingKTService.replicateCache( targetCastingKTs );

        return targetViewDTOs;
    }
}
