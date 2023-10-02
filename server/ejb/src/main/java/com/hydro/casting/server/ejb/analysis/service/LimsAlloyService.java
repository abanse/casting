package com.hydro.casting.server.ejb.analysis.service;

import com.hydro.eai.lims.model.LimsAlloy;
import com.hydro.eai.lims.model.dao.LimsAlloyHome;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class LimsAlloyService
{
    @EJB
    private LimsAlloyHome limsAlloyHome;

    public LimsAlloy findBySampleTypeId( Long sampleTypeId )
    {
        return limsAlloyHome.findBySampleTypeId( sampleTypeId );
    }

    public LimsAlloy findByNameAndVersion( String name, int version )
    {
        return limsAlloyHome.findByNameAndVersion( name, version );
    }
}
