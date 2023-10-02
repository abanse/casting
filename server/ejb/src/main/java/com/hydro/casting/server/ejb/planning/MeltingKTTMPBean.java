package com.hydro.casting.server.ejb.planning;

import com.hydro.casting.server.contract.dto.MeltingKTDTO;
import com.hydro.casting.server.contract.planning.CastingKTTMP;
import com.hydro.casting.server.contract.planning.MeltingKTTMP;
import com.hydro.casting.server.ejb.planning.service.MeltingKTService;
import com.hydro.casting.server.model.sched.CastingKT;
import com.hydro.casting.server.model.sched.MeltingKT;
import com.hydro.casting.server.model.sched.dao.MeltingKTHome;
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
public class MeltingKTTMPBean implements MeltingKTTMP
{
    private final static Logger log = LoggerFactory.getLogger( MeltingKTTMPBean.class );

    @EJB
    private MeltingKTService meltingKTService;

    @EJB
    private MeltingKTHome meltingKTHome;

    @Override
    public List<MeltingKTDTO> list( Map<String, Object> context )
    {
        return meltingKTService.load();
    }

    @Override
    public MeltingKTDTO update( Map<String, Object> context, MeltingKTDTO viewDTO, String attribute, Object oldValue, Object newValue ) throws BusinessException
    {
        log.info( "set value " + attribute + " " + newValue );

        final MeltingKT meltingKT = meltingKTHome.findById( viewDTO.getId() );

        try
        {
            MethodUtils.invokeMethod( meltingKT, "set" + attribute.substring( 0, 1 ).toUpperCase() + attribute.substring( 1 ), newValue );
        }
        catch ( Exception e )
        {
            log.error( "error setting attribute " + attribute, e );
            throw new BusinessException( "Allgemeiner Fehler", e );
        }

        meltingKTService.replicateCache( meltingKT );
        return meltingKTService.load( meltingKT );
    }

    @Override
    public void delete( Map<String, Object> context, List<MeltingKTDTO> viewDTOs ) throws BusinessException
    {
        final List<Long> removedIds = new ArrayList<>();
        for ( MeltingKTDTO viewDTO : viewDTOs )
        {
            final MeltingKT meltingKT = meltingKTHome.findById( viewDTO.getId() );

            if ( meltingKT == null )
            {
                throw new BusinessObjectNotFoundException( "MeltingKT", viewDTO.getId() );
            }

            meltingKTHome.remove( meltingKT );
            removedIds.add( viewDTO.getId() );
        }

        meltingKTService.removeFromCache( removedIds );
    }

    @Override
    public MeltingKTDTO create( Map<String, Object> context )
    {
        final MeltingKT meltingKT = new MeltingKT();
        meltingKTHome.persist( meltingKT );

        meltingKTService.replicateCache( meltingKT );
        return meltingKTService.load( meltingKT );
    }

    @Override
    public List<MeltingKTDTO> copy( Map<String, Object> context, List<MeltingKTDTO> sourceViewDTOs )
    {
        List<MeltingKTDTO> targetViewDTOs = new ArrayList<>();
        final List<MeltingKT> targetMeltingKTs = new ArrayList<>();
        for ( MeltingKTDTO sourceViewDTO : sourceViewDTOs )
        {
            final MeltingKT source = meltingKTHome.findById( sourceViewDTO.getId() );

            final MeltingKT copy = meltingKTHome.detach( source );
            meltingKTHome.persist( copy );

            targetViewDTOs.add( meltingKTService.load( copy ) );
            targetMeltingKTs.add( copy );
        }

        meltingKTService.replicateCache( targetMeltingKTs );

        return targetViewDTOs;
    }
}
