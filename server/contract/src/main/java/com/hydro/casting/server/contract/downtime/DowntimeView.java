package com.hydro.casting.server.contract.downtime;

import com.hydro.casting.server.contract.downtime.dto.DowntimeDTO;
import com.hydro.casting.server.contract.dto.TimeManagementDTO;

import javax.ejb.Remote;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Remote
public interface DowntimeView
{
    List<DowntimeDTO> loadDowntimes( LocalDateTime start, LocalDateTime end );

    Map<String, List<DowntimeDTO>> loadDowntimes( Collection<String> machines, LocalDateTime fromTS, LocalDateTime toTS );

    List<TimeManagementDTO> loadTimeManagements( Collection<String> machines, LocalDateTime fromTS, LocalDateTime toTS, boolean withFuture );

    List<TimeManagementDTO> loadTimeManagementsForMeltingArea( Collection<String> machines, LocalDateTime fromTS, LocalDateTime toTS, boolean correctOverlapping );
}
