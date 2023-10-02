package com.hydro.casting.server.contract.downtime;

import com.hydro.casting.server.contract.downtime.dto.DowntimeCreationDTO;
import com.hydro.casting.server.contract.downtime.dto.DowntimeDTO;
import com.hydro.casting.server.contract.downtime.dto.DowntimeRequestDTO;
import com.hydro.core.common.exception.BusinessException;

import javax.ejb.Remote;
import java.time.LocalDateTime;

@Remote
public interface DowntimeBusiness
{
    DowntimeCreationDTO loadEmptyDataDowntime( String[] costCenters, String machine );

    DowntimeCreationDTO loadDataDowntime( String costCenter, String machine );

    DowntimeCreationDTO loadDataDowntime( DowntimeDTO downtimeDTO );

    void createDowntime( DowntimeDTO downTime, DowntimeRequestDTO downtimeRequestDTO, LocalDateTime splitTime ) throws BusinessException;

    void editDowntime( DowntimeDTO downtimeDTO );

    void deleteDowntime( DowntimeDTO downtimeDTO );

    void addAdditionalDescription( DowntimeDTO downtimeDTO );

    long createDowntime( String user, String costCenter, String machine, String downtimeKind1, String downtimeKind2, String downtimeKind3, LocalDateTime start, String description, String type ) throws BusinessException;

    void closeDowntime( long serial, LocalDateTime end ) throws BusinessException;

    void closeOpenDowntime( String costCenter, LocalDateTime end ) throws BusinessException;
}
