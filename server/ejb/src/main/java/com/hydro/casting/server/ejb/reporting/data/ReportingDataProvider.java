package com.hydro.casting.server.ejb.reporting.data;

import com.hydro.casting.common.Casting;
import com.hydro.casting.server.contract.reporting.dto.ReportingDTO;
import com.hydro.core.common.util.StringTools;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * This abstract class is used to distribute ReportingDTOs to their corresponding cost centers.
 */
public abstract class ReportingDataProvider<S extends ReportingDTO>
{

    protected List<S> allDetailDTOs = new ArrayList<>();
    protected List<S> caster50DTOs = new ArrayList<>();
    protected List<S> caster60DTOs = new ArrayList<>();
    protected List<S> caster70DTOs = new ArrayList<>();
    protected List<S> caster80DTOs = new ArrayList<>();
    protected List<S> meltingFurnace51DTOs = new ArrayList<>();
    protected List<S> meltingFurnace52DTOs = new ArrayList<>();
    protected List<S> meltingFurnace61DTOs = new ArrayList<>();
    protected List<S> meltingFurnace62DTOs = new ArrayList<>();
    protected List<S> meltingFurnace71DTOs = new ArrayList<>();
    protected List<S> meltingFurnace72DTOs = new ArrayList<>();
    protected List<S> meltingFurnace81DTOs = new ArrayList<>();
    protected List<S> meltingFurnace82DTOs = new ArrayList<>();

    public abstract void loadAllDetailDTOsFromCache();

    protected void assignDataToCostCenters()
    {
        clearDataForAllCostCenters();

        allDetailDTOs.forEach( detail -> {
            List<S> list = getDataForCostCenter( detail.getCostCenter(), StringTools.getNullSafe( detail.getMachine() ) );
            if ( list != null )
            {
                list.add( detail );
            }
        } );
    }

    protected void clearDataForAllCostCenters()
    {
        caster50DTOs.clear();
        caster60DTOs.clear();
        caster70DTOs.clear();
        caster80DTOs.clear();
        meltingFurnace51DTOs.clear();
        meltingFurnace52DTOs.clear();
        meltingFurnace61DTOs.clear();
        meltingFurnace62DTOs.clear();
        meltingFurnace71DTOs.clear();
        meltingFurnace72DTOs.clear();
        meltingFurnace81DTOs.clear();
        meltingFurnace82DTOs.clear();
    }

    public List<S> getDataForCostCenter( String costCenter, String machine )
    {
        switch ( costCenter + StringTools.getNullSafe( machine ) )
        {
        case Casting.MACHINE.CASTER_50:
            return caster50DTOs;

        case Casting.MACHINE.CASTER_60:
            return caster60DTOs;

        case Casting.MACHINE.CASTER_70:
            return caster70DTOs;

        case Casting.MACHINE.CASTER_80:
            return caster80DTOs;

        case Casting.MACHINE.MELTING_FURNACE_51:
            return meltingFurnace51DTOs;

        case Casting.MACHINE.MELTING_FURNACE_52:
            return meltingFurnace52DTOs;

        case Casting.MACHINE.MELTING_FURNACE_61:
            return meltingFurnace61DTOs;

        case Casting.MACHINE.MELTING_FURNACE_62:
            return meltingFurnace62DTOs;

        case Casting.MACHINE.MELTING_FURNACE_71:
            return meltingFurnace71DTOs;

        case Casting.MACHINE.MELTING_FURNACE_72:
            return meltingFurnace72DTOs;

        case Casting.MACHINE.MELTING_FURNACE_81:
            return meltingFurnace81DTOs;

        case Casting.MACHINE.MELTING_FURNACE_82:
            return meltingFurnace82DTOs;

        default:
            return null;
        }
    }

    //    public List<S> getDataForLastMonth( String costCenter, String machine )
    //    {
    //        LocalDateTime start = ReportingUtils.getFirstDayOfLastMonth();
    //        LocalDateTime end = ReportingUtils.getLastDayOfLastMonth();
    //
    //        return getDataForInterval( start, end, getDataForCostCenter( costCenter, machine ) );
    //    }

    public abstract List<S> getDataForInterval( LocalDateTime start, LocalDateTime end, List<S> detailDTOs );
}