package com.hydro.casting.server.ejb.downtime.detail;

import com.hydro.casting.server.contract.downtime.dto.DowntimeCreationDTO;
import com.hydro.casting.server.contract.downtime.dto.DowntimeDTO;
import com.hydro.casting.server.contract.downtime.dto.DowntimeKindDTO;
import com.hydro.casting.server.contract.downtime.dto.DowntimeModuleDTO;
import com.hydro.casting.server.ejb.downtime.service.DowntimeService;
import com.hydro.casting.server.model.downtime.Downtime;
import com.hydro.casting.server.model.downtime.dao.DowntimeHome;
import com.hydro.casting.server.model.downtime.dao.DowntimeKindHome;
import com.hydro.casting.server.model.downtime.dao.DowntimeModuleHome;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

/**
 * This class creates the DowntimeCreationDTOs which are needed at the GUI to provide the necessary data.
 */
@Local
@Stateless
public class DowntimeCreationProvider
{
    @EJB
    private DowntimeKindHome stillstandsArtenHome;

    @EJB
    private DowntimeModuleHome baugruppeHome;

    @EJB
    private DowntimeHome stillstaendeHome;

    @EJB
    private DowntimeService downtimeService;

    public DowntimeCreationDTO loadEmptyData( String[] costCenters, String machine )
    {
        return createCreationDTO( null, costCenters );
    }

    public DowntimeCreationDTO loadData( DowntimeDTO downtimeDTO )
    {
        Downtime downtime = stillstaendeHome.findById( downtimeDTO.getId() );
        return createCreationDTO( downtime, new String[] {downtimeDTO.getCostCenter()} );
    }

    public DowntimeCreationDTO loadData( String costCenter, String machine )
    {
        Downtime downtime = stillstaendeHome.findOpenDowntimeByCostCenter( costCenter, machine );
        return createCreationDTO( downtime, new String[]{costCenter} );
    }

    private DowntimeCreationDTO createCreationDTO( Downtime downtime, String[] costCenters )
    {
        DowntimeCreationDTO downtimeCreationDTO = new DowntimeCreationDTO();
        downtimeCreationDTO.setDowntimeKinds( downtimeService.findDowntimeKindDTOsByCostCenter( costCenters ) );
        downtimeCreationDTO.setDowntimeModules( downtimeService.findModuleDTOsByCostCenter( costCenters ) );

        if ( downtime != null )
        {
            downtimeCreationDTO.setId( downtime.getObjid() );
            downtimeCreationDTO.setMachine( downtime.getMachine() );
            downtimeCreationDTO.setCostCenter( downtime.getDowntimeKind().getCostCenter() );
            downtimeCreationDTO.setDescription( downtime.getDescription() );
            downtimeCreationDTO.setStartTS( downtime.getFromTS() );
            DowntimeKindDTO lastKind = new DowntimeKindDTO();
            lastKind.setCostCenter( downtime.getDowntimeKind().getCostCenter() );
            lastKind.setDowntimeKind1( downtime.getDowntimeKind().getKind1() );
            lastKind.setDowntimeKind2( downtime.getDowntimeKind().getKind2() );
            lastKind.setDowntimeKind3( downtime.getDowntimeKind().getKind3() );
            lastKind.setDescription( downtime.getDowntimeKind().getDescription() );
            lastKind.setPhase( downtime.getDowntimeKind().getPhase() );
            downtimeCreationDTO.setDowntimeKind( lastKind );
            if ( downtime.getDowntimeModule() != null )
            {
                DowntimeModuleDTO lastModule = new DowntimeModuleDTO();
                lastModule.setCostCenter( downtime.getDowntimeModule().getCostCenter() );
                lastModule.setModule( downtime.getDowntimeModule().getModule() );
                lastModule.setComponent( downtime.getDowntimeModule().getComponent() );
                lastModule.setDescription( downtime.getDowntimeModule().getDescription() );
                lastModule.setOrderNumber( downtime.getDowntimeModule().getErpIdent() );
                downtimeCreationDTO.setDowntimeModule( lastModule );
            }
            if ( downtime.getEndTS() != null )
            {
                downtimeCreationDTO.setEndTS( downtime.getEndTS() );
            }
        }
        return downtimeCreationDTO;
    }
}
