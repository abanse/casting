package com.hydro.casting.server.ejb.main.service;

import com.hydro.casting.server.model.res.Caster;
import com.hydro.casting.server.model.res.MeltingFurnace;
import com.hydro.casting.server.model.res.dao.CasterHome;
import com.hydro.casting.server.model.res.dao.MeltingFurnaceHome;
import com.hydro.core.common.exception.BusinessException;
import com.hydro.core.common.exception.BusinessObjectNotFoundException;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class ResourceService
{
    @EJB
    private CasterHome casterHome;

    @EJB
    private MeltingFurnaceHome meltingFurnaceHome;

    public Caster getCaster( final String machineApk ) throws BusinessException
    {
        final Caster caster = casterHome.findByApk( machineApk );
        if ( caster == null )
        {
            throw new BusinessObjectNotFoundException( "Caster", machineApk );
        }
        return caster;
    }

    public MeltingFurnace getMeltingFurnace( final String machineApk ) throws BusinessException
    {
        final MeltingFurnace meltingFurnace = meltingFurnaceHome.findByApk( machineApk );
        if ( meltingFurnace == null )
        {
            throw new BusinessObjectNotFoundException( "MeltingFurnace", machineApk );
        }
        return meltingFurnace;
    }
}
