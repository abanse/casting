package com.hydro.casting.server.ejb.downtime;

import com.hydro.casting.server.contract.downtime.DowntimeBusiness;
import com.hydro.casting.server.contract.downtime.dto.DowntimeCreationDTO;
import com.hydro.casting.server.contract.downtime.dto.DowntimeDTO;
import com.hydro.casting.server.contract.downtime.dto.DowntimeRequestDTO;
import com.hydro.casting.server.ejb.downtime.detail.DowntimeCreationProvider;
import com.hydro.casting.server.ejb.downtime.service.DowntimeRequestService;
import com.hydro.casting.server.ejb.downtime.service.DowntimeService;
import com.hydro.casting.server.ejb.main.service.MachineService;
import com.hydro.casting.server.ejb.shift.service.ShiftService;
import com.hydro.casting.server.model.downtime.Downtime;
import com.hydro.casting.server.model.downtime.DowntimeKind;
import com.hydro.casting.server.model.downtime.DowntimeRequest;
import com.hydro.casting.server.model.downtime.dao.DowntimeHome;
import com.hydro.casting.server.model.downtime.dao.DowntimeKindHome;
import com.hydro.casting.server.model.downtime.dao.DowntimeModuleHome;
import com.hydro.casting.server.model.downtime.dao.DowntimeRequestHome;
import com.hydro.casting.server.model.res.Machine;
import com.hydro.casting.server.model.res.dao.MachineHome;
import com.hydro.core.common.exception.BusinessException;
import com.hydro.core.common.exception.BusinessObjectNotFoundException;
import com.hydro.core.common.util.DateTimeUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.LocalDateTime;
import java.util.List;

/**
 * This class includes the business logic for the DowntimeBusiness interface.
 */
@Stateless
public class DowntimeBusinessBean implements DowntimeBusiness
{
    @EJB
    DowntimeCreationProvider downtimeCreationProvider;

    @EJB
    DowntimeKindHome stillstandsArtenHome;

    @EJB
    DowntimeHome stillstaendeHome;

    @EJB
    DowntimeModuleHome baugruppeHome;

    @EJB
    ShiftService shiftService;

    @EJB
    DowntimeService downtimeService;

    @EJB
    private MachineHome machineHome;
    @EJB
    private MachineService machineService;

    @EJB
    private DowntimeRequestHome downtimeRequestHome;

    @EJB
    private DowntimeRequestService downtimeRequestService;

    @Override
    public DowntimeCreationDTO loadEmptyDataDowntime( String[] costCenters, String machine )
    {
        return downtimeCreationProvider.loadEmptyData( costCenters, machine );
    }

    @Override
    public DowntimeCreationDTO loadDataDowntime( String costCenter, String machine )
    {
        return downtimeCreationProvider.loadData( costCenter, machine );
    }

    @Override
    public DowntimeCreationDTO loadDataDowntime( DowntimeDTO downtimeDTO )
    {
        return downtimeCreationProvider.loadData( downtimeDTO );
    }

    @Override
    public void createDowntime( DowntimeDTO downtimeDTO, DowntimeRequestDTO downtimeRequestDTO, LocalDateTime splitTime )
    {
        Downtime newDowntime;
        if ( downtimeDTO.getId() != 0 )
        {
            newDowntime = stillstaendeHome.findById( downtimeDTO.getId() );
        }
        else
        {
            newDowntime = new Downtime();
            newDowntime.setCostCenter( downtimeDTO.getCostCenter() );
            newDowntime.setMachine( downtimeDTO.getMachine() );
        }
        newDowntime.setFromTS( downtimeDTO.getFromTS() );
        final int fromTime = DateTimeUtil.getHoursMinutes( downtimeDTO.getFromTS() );
        newDowntime.setShift( shiftService.calculateShift( downtimeDTO.getCostCenter(), fromTime ) );

        if ( downtimeDTO.getEndTS() != null )
        {
            newDowntime.setEndTS( downtimeDTO.getEndTS() );
        }

        newDowntime.setDescription( correctDescription( downtimeDTO.getDescription() ) );
        newDowntime.setType( downtimeDTO.getType() );
        newDowntime.setUserId( downtimeDTO.getUserId() );
        newDowntime.setCreateTS( LocalDateTime.now() );

        newDowntime.setDowntimeKind( stillstandsArtenHome.findByApk( downtimeDTO.getCostCenter(), downtimeDTO.getDowntimeKind1(), downtimeDTO.getDowntimeKind2(), downtimeDTO.getDowntimeKind3() ) );

        if ( downtimeDTO.getModule() != null )
        {
            newDowntime.setDowntimeModule( baugruppeHome.findByApk( downtimeDTO.getCostCenter(), downtimeDTO.getModule(), downtimeDTO.getComponent() ) );
        }

        if ( downtimeDTO.getId() == 0 )
        {
            newDowntime.setObjid( 0L );
            stillstaendeHome.persist( newDowntime );
            downtimeDTO.setId( newDowntime.getObjid() );
        }
        else
        {
            newDowntime.setObjid( downtimeDTO.getId() );
            stillstaendeHome.merge( newDowntime );
        }

        if ( downtimeRequestDTO != null )
        {
            final DowntimeRequest downtimeRequest = downtimeRequestHome.findById( downtimeRequestDTO.getId() );
            if ( downtimeRequest != null )
            {
                if ( splitTime != null)
                {
                    final List<DowntimeRequest> downtimeRequests = downtimeRequestService.split(downtimeRequest, splitTime);
                    downtimeRequestService.release( downtimeRequests.get( 0 ), newDowntime.getUserId() );
                    downtimeRequestService.removeFromCache( downtimeRequests.get( 0 ).getObjid() );
                    downtimeRequestService.replicateCache( downtimeRequests.get( 1 ) );
                }
                else
                {
                    downtimeRequestService.release( downtimeRequest, newDowntime.getUserId() );
                    downtimeRequestService.removeFromCache( downtimeRequestDTO.getId() );
                }
            }
        }

        downtimeService.replicateCache( downtimeDTO );
        downtimeService.replicateReportingDataCache( downtimeDTO );

        replicateMachine( downtimeDTO.getCostCenter() );
    }

    @Override
    public void editDowntime( DowntimeDTO downtimeDTO )
    {
        Downtime downtime = stillstaendeHome.findById( downtimeDTO.getId() );
        downtime.setCostCenter( downtimeDTO.getCostCenter() );
        downtime.setDowntimeModule( baugruppeHome.findByApk( downtimeDTO.getCostCenter(), downtimeDTO.getModule(), downtimeDTO.getComponent() ) );
        downtime.setDowntimeKind( stillstandsArtenHome.findByApk( downtimeDTO.getCostCenter(), downtimeDTO.getDowntimeKind1(), downtimeDTO.getDowntimeKind2(), downtimeDTO.getDowntimeKind3() ) );
        downtime.setMachine( downtimeDTO.getMachine() );
        downtime.setFromTS( downtimeDTO.getFromTS() );
        downtime.setEndTS( downtimeDTO.getEndTS() );
        final int timeFrom = DateTimeUtil.getHoursMinutes( downtimeDTO.getFromTS() );
        downtime.setShift( shiftService.calculateShift( downtimeDTO.getCostCenter(), timeFrom ) );
        downtime.setAmount( downtimeDTO.getAmount() );
        downtime.setDescription( correctDescription( downtimeDTO.getDescription() ) );
        downtime.setUserId( downtimeDTO.getUserId() );
        downtime.setType( downtimeDTO.getType() );
        downtime.setCreateTS( LocalDateTime.now() );
        downtimeService.replicateCache( downtimeDTO );
        downtimeService.replicateReportingDataCache( downtimeDTO );

        replicateMachine( downtimeDTO.getCostCenter() );
    }

    @Override
    public void deleteDowntime( DowntimeDTO downtimeDTO )
    {
        stillstaendeHome.remove( stillstaendeHome.findById( downtimeDTO.getId() ) );
        downtimeService.removeFromCache( downtimeDTO );
        downtimeService.replicateReportingDataCache( downtimeDTO );

        replicateMachine( downtimeDTO.getCostCenter() );
    }

    @Override
    public void addAdditionalDescription( DowntimeDTO downtimeDTO )
    {
        Downtime downtime = stillstaendeHome.findById( downtimeDTO.getId() );
        if ( downtimeDTO.getRemark() == null || downtimeDTO.getRemark().isEmpty() )
        {
            downtime.setRemark( null );
        }
        else
        {
            downtime.setRemark( downtimeDTO.getRemark() );
        }
        downtime.setUserId( downtimeDTO.getUserId() );
        downtime.setCreateTS( LocalDateTime.now() );
        downtimeService.replicateCache( downtimeDTO );

        replicateMachine( downtimeDTO.getCostCenter() );
    }

    @Override
    public long createDowntime( String user, String costCenter, String machine, String downtimeKind1, String downtimeKind2, String downtimeKind3, LocalDateTime start, String description, String type )
            throws BusinessException
    {
        final Downtime existingDowntime = stillstaendeHome.findOpenDowntimeByCostCenter( costCenter, machine );
        if ( existingDowntime != null )
        {
            throw new BusinessException( "open downtime exist" );
        }

        Downtime newDowntime = new Downtime();
        newDowntime.setCostCenter( costCenter );
        newDowntime.setMachine( machine );

        newDowntime.setFromTS( start );
        final int timeFrom = DateTimeUtil.getHoursMinutes( newDowntime.getFromTS() );
        final String shift = shiftService.calculateShift( costCenter, timeFrom );
        if ( shift == null )
        {
            newDowntime.setShift( "0" );
        }
        else
        {
            newDowntime.setShift( shift );
        }

        newDowntime.setDescription( correctDescription( description ) );
        newDowntime.setUserId( user );
        newDowntime.setCreateTS( LocalDateTime.now() );

        final DowntimeKind stillstandsArten = stillstandsArtenHome.findByApk( costCenter, downtimeKind1, downtimeKind2, downtimeKind3 );
        if ( stillstandsArten == null )
        {
            throw new BusinessObjectNotFoundException( "Störzeit-Arten", costCenter + " " + downtimeKind1 + " " + downtimeKind2 );
        }
        newDowntime.setDowntimeKind( stillstandsArten );

        newDowntime.setType( type );

        newDowntime.setObjid( 0L );
        stillstaendeHome.persist( newDowntime );

        replicateMachine( newDowntime.getCostCenter() );

        return newDowntime.getObjid();
    }

    @Override
    public void closeDowntime( long serial, LocalDateTime end ) throws BusinessException
    {
        final Downtime downtime = stillstaendeHome.findById( serial );
        if ( downtime == null )
        {
            throw new BusinessObjectNotFoundException( "Downtime", serial );
        }

        downtime.setEndTS( end );

        downtimeService.replicateCache( downtime );

        replicateMachine( downtime.getCostCenter() );
    }

    @Override
    public void closeOpenDowntime( String costCenter, LocalDateTime end ) throws BusinessException
    {
        final Downtime downtime = stillstaendeHome.findOpenDowntimeByCostCenter( costCenter, null );
        if ( downtime == null )
        {
            return;
        }

        if ( downtime.getFromTS() != null && downtime.getFromTS().isAfter( end ) )
        {
            downtime.setEndTS( LocalDateTime.now() );
        }
        else
        {
            downtime.setEndTS( end );
        }

        downtimeService.replicateCache( downtime );

        replicateMachine( downtime.getCostCenter() );
    }

    private String correctDescription( String description )
    {
        StringBuffer text = new StringBuffer( description );

        int i = 0;
        boolean found = false;
        while ( i < text.length() )
        {
            if ( text.charAt( i ) == '\n' || text.charAt( i ) == '\r' || text.charAt( i ) == '\t' )
            {
                found = true;
            }
            if ( !found )
            {
                i++;
            }
            else
            {
                text.replace( i, i + 1, " " );
            }
            found = false;
        }

        return text.toString();
    }

    private void replicateMachine( String costCenter )
    {
        final Machine machine = machineHome.findByApk( costCenter );
        if ( machine != null )
        {
            machineService.replicateCache( machine );
        }
    }
}
