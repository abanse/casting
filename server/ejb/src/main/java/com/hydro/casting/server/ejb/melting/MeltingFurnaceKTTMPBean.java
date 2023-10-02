package com.hydro.casting.server.ejb.melting;

import com.hydro.casting.server.contract.dto.MeltingFurnaceKTDTO;
import com.hydro.casting.server.contract.melting.MeltingFurnaceKTTMP;
import com.hydro.casting.server.ejb.melting.service.MeltingFurnaceKTService;
import com.hydro.casting.server.model.sched.MeltingFurnaceKT;
import com.hydro.casting.server.model.sched.dao.MeltingFurnaceKTHome;
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
public class MeltingFurnaceKTTMPBean implements MeltingFurnaceKTTMP
{
    private final static Logger log = LoggerFactory.getLogger( MeltingFurnaceKTTMPBean.class );

    @EJB
    private MeltingFurnaceKTService meltingFurnaceKTService;

    @EJB
    private MeltingFurnaceKTHome meltingFurnaceKTHome;

    @Override
    public List<MeltingFurnaceKTDTO> list( Map<String, Object> context )
    {
        return meltingFurnaceKTService.load();
    }

    @Override
    public MeltingFurnaceKTDTO update( Map<String, Object> context, MeltingFurnaceKTDTO viewDTO, String attribute, Object oldValue, Object newValue ) throws BusinessException
    {
        String user = (String) context.getOrDefault( "user", "unknown" );
        log.info( "Melting furnace knowledge table update. Attribute: " + attribute + ", old value: " + oldValue + ", new value: " + newValue + ", user: " + user + ". Entry: " + viewDTO );

        final MeltingFurnaceKT meltingFurnaceKT = meltingFurnaceKTHome.findById( viewDTO.getId() );

        try
        {
            MethodUtils.invokeMethod( meltingFurnaceKT, "set" + attribute.substring( 0, 1 ).toUpperCase() + attribute.substring( 1 ), newValue );
        }
        catch ( Exception e )
        {
            log.error( "error setting attribute " + attribute, e );
            throw new BusinessException( "Allgemeiner Fehler", e );
        }

        meltingFurnaceKTService.replicateCache( meltingFurnaceKT );
        return meltingFurnaceKTService.load( meltingFurnaceKT );
    }

    @Override
    public void delete( Map<String, Object> context, List<MeltingFurnaceKTDTO> viewDTOs ) throws BusinessException
    {
        String user = (String) context.getOrDefault( "user", "unknown" );
        log.info( "Melting furnace knowledge table deletion, " + viewDTOs.size() + " elements are deleted by user: " + user );

        final List<Long> removedIds = new ArrayList<>();
        for ( MeltingFurnaceKTDTO viewDTO : viewDTOs )
        {
            log.info( "Entry deleted: " + viewDTO );

            final MeltingFurnaceKT meltingFurnaceKT = meltingFurnaceKTHome.findById( viewDTO.getId() );

            if ( meltingFurnaceKT == null )
            {
                throw new BusinessObjectNotFoundException( "MeltingFurnaceKT", viewDTO.getId() );
            }

            meltingFurnaceKTHome.remove( meltingFurnaceKT );
            removedIds.add( viewDTO.getId() );
        }

        meltingFurnaceKTService.removeFromCache( removedIds );
    }

    @Override
    public MeltingFurnaceKTDTO create( Map<String, Object> context )
    {
        String user = (String) context.getOrDefault( "user", "unknown" );
        log.info( "Melting furnace knowledge table creation, new element created by user: " + user );

        final MeltingFurnaceKT meltingFurnaceKT = new MeltingFurnaceKT();
        meltingFurnaceKTHome.persist( meltingFurnaceKT );

        meltingFurnaceKTService.replicateCache( meltingFurnaceKT );
        return meltingFurnaceKTService.load( meltingFurnaceKT );
    }

    @Override
    public List<MeltingFurnaceKTDTO> copy( Map<String, Object> context, List<MeltingFurnaceKTDTO> sourceViewDTOs )
    {
        String user = (String) context.getOrDefault( "user", "unknown" );
        log.info( "Melting furnace knowledge table copy, " + sourceViewDTOs.size() + " elements are copied by user: " + user );

        List<MeltingFurnaceKTDTO> targetViewDTOs = new ArrayList<>();
        final List<MeltingFurnaceKT> targetMeltingFurnaceKTs = new ArrayList<>();
        for ( MeltingFurnaceKTDTO sourceViewDTO : sourceViewDTOs )
        {
            log.info( "Entry copied: " + sourceViewDTO );

            final MeltingFurnaceKT source = meltingFurnaceKTHome.findById( sourceViewDTO.getId() );

            final MeltingFurnaceKT copy = meltingFurnaceKTHome.detach( source );
            meltingFurnaceKTHome.persist( copy );

            targetViewDTOs.add( meltingFurnaceKTService.load( copy ) );
            targetMeltingFurnaceKTs.add( copy );
        }

        meltingFurnaceKTService.replicateCache( targetMeltingFurnaceKTs );

        return targetViewDTOs;
    }
}
