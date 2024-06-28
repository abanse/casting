package com.hydro.casting.gui.fernladung.table;

import com.hydro.casting.common.constant.CostCenterEnum;
import com.hydro.casting.server.contract.downtime.dto.DowntimeDTO;
import com.hydro.core.common.util.DateTimeUtil;
import com.hydro.core.common.util.StringTools;
import com.hydro.core.gui.comp.GroupTreeItemProvider;
import com.hydro.core.gui.util.SummaryUtil;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;
import org.controlsfx.control.IndexedCheckModel;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Predicate;

public class DowntimeGroupTreeItemProvider implements GroupTreeItemProvider<DowntimeDTO>
{
    public IndexedCheckModel<CostCenterEnum> costCenterModel;

    private String singleCostCenter;
    private boolean shiftViewEnabled = false;

    @Override
    public void buildTree( TreeItem<DowntimeDTO> root, List<DowntimeDTO> data, HBox summary )
    {
        Predicate<DowntimeDTO> predicate = null;
        List<String> allowedCostCenters = new ArrayList<>();
        if ( costCenterModel != null )
        {
            List<CostCenterEnum> selectedCostCenters = costCenterModel.getCheckedItems();
            selectedCostCenters.forEach( dtCostCenter -> {
                if ( dtCostCenter.getGroup() != null && dtCostCenter.getGroup().startsWith( "*" ) )
                {
                    if ( dtCostCenter.getGroup().length() > 1 )
                    {
                        final String group = dtCostCenter.getGroup().substring( 1 );
                        for ( CostCenterEnum entry : CostCenterEnum.values() )
                        {
                            if ( entry.getGroup() != null && entry.getGroup().equals( group ) )
                            {
                                allowedCostCenters.add( entry.getCostCenter() );
                            }
                        }

                    }
                    else
                    {
                        for ( CostCenterEnum entry : CostCenterEnum.values() )
                        {
                            if ( entry.getGroup() == null || !entry.getGroup().startsWith( "*" ) )
                            {
                                allowedCostCenters.add( entry.getCostCenter() );
                            }
                        }
                    }
                }
                else
                {
                    allowedCostCenters.add( dtCostCenter.getCostCenter() );
                }
            } );

        }
        else if ( StringTools.isFilled( singleCostCenter ) )
        {
            allowedCostCenters.add( singleCostCenter );
        }

        if ( !allowedCostCenters.isEmpty() )
        {
            predicate = downtimeDTO -> {
                if ( downtimeDTO == null )
                {
                    return false;
                }
                // check costCenter
                return downtimeDTO.getCostCenter() == null || allowedCostCenters.contains( downtimeDTO.getCostCenter() );
            };
        }

        Map<String, TreeItem<DowntimeDTO>> costCenterGroupTreeItems = new HashMap<>();
        for ( String costCenter : allowedCostCenters )
        {
            addCostCenterGroupTreeItem( costCenter, "", root, costCenterGroupTreeItems );
        }

        // Build tree
        long count = 0;
        for ( final DowntimeDTO downtimeDTO : data )
        {
            if ( predicate != null && !predicate.test( downtimeDTO ) )
            {
                continue;
            }
            TreeItem<DowntimeDTO> costCenterGroupTreeItem = costCenterGroupTreeItems.get( downtimeDTO.getCostCenter() + StringTools.getNullSafe( downtimeDTO.getMachine() ) );

            TreeItem<DowntimeDTO> dataGroupTreeItem;
            if ( !shiftViewEnabled )
            {
                dataGroupTreeItem = costCenterGroupTreeItem;
            }
            else
            {
                switch ( downtimeDTO.getShift() )
                {
                case "1":
                    dataGroupTreeItem = costCenterGroupTreeItem.getChildren().get( 0 );
                    break;
                case "2":
                    dataGroupTreeItem = costCenterGroupTreeItem.getChildren().get( 1 );
                    break;
                case "3":
                    dataGroupTreeItem = costCenterGroupTreeItem.getChildren().get( 2 );
                    break;

                default:
                    dataGroupTreeItem = null;
                    break;
                }
            }
            if ( dataGroupTreeItem != null )
            {
                downtimeDTO.setParent( dataGroupTreeItem.getValue() );
                dataGroupTreeItem.getValue().getChilds().add( downtimeDTO );
                dataGroupTreeItem.getChildren().add( new TreeItem<>( downtimeDTO ) );
                count++;
            }
        }
        for ( TreeItem<DowntimeDTO> costCenterGroupTreeItem : root.getChildren() )
        {
            DowntimeDTO costCenterDowntimeDTO = costCenterGroupTreeItem.getValue();
            long duration = 0;

            if ( !shiftViewEnabled )
            {
                duration = addDurationOfAllChildren( costCenterDowntimeDTO );
            }
            else
            {
                for ( TreeItem<DowntimeDTO> shiftGroupTreeItem : costCenterGroupTreeItem.getChildren() )
                {
                    DowntimeDTO downtimeDTO = shiftGroupTreeItem.getValue();
                    long shiftDuration = addDurationOfAllChildren( downtimeDTO );
                    duration += shiftDuration;
                    setDurationForRootDTO( downtimeDTO, shiftDuration );
                }
            }

            setDurationForRootDTO( costCenterDowntimeDTO, duration );
        }

        if ( summary != null )
        {
            SummaryUtil.addCount( summary, count );
        }
    }

    private long addDurationOfAllChildren( DowntimeDTO downtimeDTO )
    {
        long duration = 0;
        for ( DowntimeDTO child : downtimeDTO.getChilds() )
        {

            if ( child.getEndTS() != null )
            {
                duration += child.getEndTS().toEpochSecond( ZoneOffset.UTC ) - child.getFromTS().toEpochSecond( ZoneOffset.UTC );
            }
        }

        return duration;
    }

    private void setDurationForRootDTO( DowntimeDTO downtimeDTO, long duration )
    {
        final LocalDateTime fromLDT = DateTimeUtil.getDateTime( new Date( 0 ), 0 );
        LocalDateTime untilLDT = fromLDT.plusSeconds( duration );
        downtimeDTO.setFromTS( fromLDT );
        downtimeDTO.setEndTS( untilLDT );
    }

    private void addCostCenterGroupTreeItem( String costCenter, String machine, TreeItem<DowntimeDTO> root, Map<String, TreeItem<DowntimeDTO>> costCenterGroupTreeItems )
    {
        DowntimeDTO rootDowntimeDTO = new DowntimeDTO();
        rootDowntimeDTO.setCostCenter( costCenter );
        rootDowntimeDTO.setMachine( machine );
        rootDowntimeDTO.setChilds( new ArrayList<>() );
        TreeItem<DowntimeDTO> costCenterGroupTreeItem = new TreeItem<>( rootDowntimeDTO );
        costCenterGroupTreeItem.setExpanded( true );
        root.getChildren().add( costCenterGroupTreeItem );
        costCenterGroupTreeItems.put( rootDowntimeDTO.getCostCenter() + StringTools.getNullSafe( rootDowntimeDTO.getMachine() ), costCenterGroupTreeItem );
        if ( shiftViewEnabled )
        {
            addShiftTreeItem( costCenterGroupTreeItem, "Frühschicht" );
            addShiftTreeItem( costCenterGroupTreeItem, "Spätschicht" );
            addShiftTreeItem( costCenterGroupTreeItem, "Nachtschicht" );
        }
    }

    private void addShiftTreeItem( TreeItem<DowntimeDTO> costCenterGroupTreeItem, String shiftName )
    {
        DowntimeDTO rootDowntimeDTO = new DowntimeDTO();
        rootDowntimeDTO.setMachine( shiftName );
        rootDowntimeDTO.setChilds( new ArrayList<>() );
        TreeItem<DowntimeDTO> shiftGroupTreeItem = new TreeItem<>( rootDowntimeDTO );
        shiftGroupTreeItem.setExpanded( true );
        costCenterGroupTreeItem.getValue().getChilds().add( rootDowntimeDTO );
        costCenterGroupTreeItem.getChildren().add( shiftGroupTreeItem );
    }

    public IndexedCheckModel<CostCenterEnum> getCostCenterModel()
    {
        return costCenterModel;
    }

    public void setCostCenterModel( IndexedCheckModel<CostCenterEnum> costCenterModel )
    {
        this.costCenterModel = costCenterModel;
    }

    public String getSingleCostCenter()
    {
        return singleCostCenter;
    }

    public void setSingleCostCenter( String singleCostCenter )
    {
        this.singleCostCenter = singleCostCenter;
    }

    public boolean isShiftViewEnabled()
    {
        return shiftViewEnabled;
    }

    public void setShiftViewEnabled( boolean shiftViewEnabled )
    {
        this.shiftViewEnabled = shiftViewEnabled;
    }
}
