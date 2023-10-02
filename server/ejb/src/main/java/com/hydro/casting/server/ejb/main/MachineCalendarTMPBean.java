package com.hydro.casting.server.ejb.main;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.dto.MachineCalendarDTO;
import com.hydro.casting.server.contract.main.MachineCalendarTMP;
import com.hydro.casting.server.ejb.main.service.MachineCalendarService;
import com.hydro.casting.server.model.res.Machine;
import com.hydro.casting.server.model.res.MachineCalendar;
import com.hydro.casting.server.model.res.dao.MachineCalendarHome;
import com.hydro.casting.server.model.res.dao.MachineHome;
import com.hydro.core.common.exception.BusinessException;
import com.hydro.core.common.exception.BusinessObjectNotFoundException;
import com.hydro.core.common.util.StringTools;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Stateless
public class MachineCalendarTMPBean implements MachineCalendarTMP
{
    private final static Logger log = LoggerFactory.getLogger( MachineCalendarTMPBean.class );

    @EJB
    private MachineCalendarService machineCalendarService;

    @EJB
    private MachineCalendarHome machineCalendarHome;

    @EJB
    private MachineHome machineHome;

    @Override
    public List<MachineCalendarDTO> list( Map<String, Object> context )
    {
        return machineCalendarService.load();
    }

    @Override
    public MachineCalendarDTO update( Map<String, Object> context, MachineCalendarDTO viewDTO, String attribute, Object oldValue, Object newValue ) throws BusinessException
    {
        log.info( "set value " + attribute + " " + newValue );

        final MachineCalendar machineCalendar = machineCalendarHome.findById( viewDTO.getId() );

        if ( "machine".equals( attribute ) )
        {
            if ( newValue == null || StringTools.isNullOrEmpty( newValue.toString() ) )
            {
                throw new BusinessException( "Anlage muss besetzt werden" );
            }
            final Machine machine = machineHome.findByApk( newValue.toString() );
            if ( machine == null )
            {
                throw new BusinessException( "Anlage '" + newValue.toString() + "' konnte nicht gefunden werden" );
            }
            machineCalendar.setMachine( machine );
        }
        else
        {
            try
            {
                MethodUtils.invokeMethod( machineCalendar, "set" + attribute.substring( 0, 1 ).toUpperCase() + attribute.substring( 1 ), newValue );
            }
            catch ( Exception e )
            {
                log.error( "error setting attribute " + attribute, e );
                throw new BusinessException( "Allgemeiner Fehler", e );
            }
        }

        machineCalendar.setEndTS( machineCalendar.getStartTS().plusMinutes( machineCalendar.getDuration() ) );

        machineCalendarService.replicateCache( machineCalendar );
        return machineCalendarService.load( machineCalendar );
    }

    @Override
    public void delete( Map<String, Object> context, List<MachineCalendarDTO> viewDTOs ) throws BusinessException
    {
        final List<Long> removedIds = new ArrayList<>();
        for ( MachineCalendarDTO viewDTO : viewDTOs )
        {
            final MachineCalendar machineCalendar = machineCalendarHome.findById( viewDTO.getId() );

            if ( machineCalendar == null )
            {
                throw new BusinessObjectNotFoundException( "MachineCalendar", viewDTO.getId() );
            }

            machineCalendarHome.remove( machineCalendar );
            removedIds.add( viewDTO.getId() );
        }

        machineCalendarService.removeFromCache( removedIds );
    }

    @Override
    public MachineCalendarDTO create( Map<String, Object> context )
    {
        final Machine createMachine = machineHome.findByApk( Casting.MACHINE.CASTER_50 );
        final MachineCalendar machineCalendar = new MachineCalendar();
        machineCalendar.setMachine( createMachine );
        final LocalDateTime createStartTS = LocalDateTime.now().withNano( 0 ).withSecond( 0 ).withMinute( 0 ).withHour( 6 ).plusDays( 1 );
        machineCalendar.setStartTS( createStartTS );
        machineCalendar.setDuration( 15 );
        machineCalendar.setEndTS( createStartTS.plusMinutes( machineCalendar.getDuration() ) );
        machineCalendar.setDescription( "Unterbrechung" );
        machineCalendarHome.persist( machineCalendar );

        machineCalendarService.replicateCache( machineCalendar );
        return machineCalendarService.load( machineCalendar );
    }

    @Override
    public List<MachineCalendarDTO> copy( Map<String, Object> context, List<MachineCalendarDTO> sourceViewDTOs )
    {
        final List<MachineCalendarDTO> targetViewDTOs = new ArrayList<>();
        final List<MachineCalendar> targetEntities = new ArrayList<>();
        for ( MachineCalendarDTO sourceViewDTO : sourceViewDTOs )
        {
            final MachineCalendar source = machineCalendarHome.findById( sourceViewDTO.getId() );

            final MachineCalendar copy = machineCalendarHome.detach( source );
            machineCalendarHome.persist( copy );

            targetViewDTOs.add( machineCalendarService.load( copy ) );
            targetEntities.add( copy );
        }

        machineCalendarService.replicateCache( targetEntities );
        return targetViewDTOs;
    }
}
