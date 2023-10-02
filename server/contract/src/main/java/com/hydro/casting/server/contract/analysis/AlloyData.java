package com.hydro.casting.server.contract.analysis;

import javax.ejb.Remote;

@Remote
public interface AlloyData
{
    String updateAllAlloysFromLIMS();
}
