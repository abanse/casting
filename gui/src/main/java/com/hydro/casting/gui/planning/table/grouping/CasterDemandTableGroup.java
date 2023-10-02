package com.hydro.casting.gui.planning.table.grouping;

import com.hydro.casting.server.contract.dto.CasterDemandDTO;
import com.hydro.core.gui.comp.GroupTreeItemProvider;
import com.hydro.core.gui.util.SummaryUtil;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CasterDemandTableGroup implements GroupTreeItemProvider<CasterDemandDTO>
{
    @Override
    public void buildTree( TreeItem<CasterDemandDTO> root, List<CasterDemandDTO> data, HBox summary )
    {
        if ( data.isEmpty() )
        {
            return;
        }
        // sortieren
        final List<CasterDemandDTO> sortedList = data.stream().sorted( ( o1, o2 ) -> {
            return Comparator.comparing( CasterDemandDTO::getAlloy ).thenComparing( CasterDemandDTO::getQuality ).thenComparingInt( CasterDemandDTO::getHeight )
                    .thenComparingInt( CasterDemandDTO::getWidth ).thenComparingInt( CasterDemandDTO::getLength ).compare( o1, o2 );
        } ).collect( Collectors.toList() );

//        TreeItem<CasterDemandDTO> last = null;
//        for ( CasterDemandDTO casterDemandDTO : sortedList )
//        {
//            if ( last != null && Objects.equals( last.getValue().getAlloy(), casterDemandDTO.getAlloy() ) && Objects.equals( last.getValue().getQuality(), casterDemandDTO.getQuality() ) && Objects
//                    .equals( last.getValue().getHeight(), casterDemandDTO.getHeight() ) && Objects.equals( last.getValue().getWidth(), casterDemandDTO.getWidth() ) && Objects
//                    .equals( last.getValue().getLength(), casterDemandDTO.getLength() ) )
//            {
//                if ( last.getChildren().isEmpty() )
//                {
//                    root.getChildren().remove( last );
//                    // Create Bundle Item
//                    final CasterDemandDTO bundleItem = new CasterDemandDTO();
//                    bundleItem.setCombinedKey( "bundle" );
//                    bundleItem.setAlloy( casterDemandDTO.getAlloy() );
//                    bundleItem.setQuality( casterDemandDTO.getQuality() );
//                    bundleItem.setHeight( casterDemandDTO.getHeight() );
//                    bundleItem.setWidth( casterDemandDTO.getWidth() );
//                    bundleItem.setLength( casterDemandDTO.getLength() );
//                    bundleItem.setDoubleLength( casterDemandDTO.getDoubleLength() );
//                    final TreeItem<CasterDemandDTO> bundleTI = new TreeItem<>( bundleItem );
//                    bundleTI.getChildren().add( last );
//                    bundleTI.getChildren().add( new TreeItem<>( casterDemandDTO ) );
//                    root.getChildren().add( bundleTI );
//                    last = bundleTI;
//                    continue;
//                }
//                else
//                {
//                    last.getChildren().add( new TreeItem<>( casterDemandDTO ) );
//                    continue;
//                }
//            }
//            final TreeItem<CasterDemandDTO> cdTI = new TreeItem<>( casterDemandDTO );
//            root.getChildren().add( cdTI );
//            last = cdTI;
//        }
//
//        // Add calc fields to bundles
//        for ( TreeItem<CasterDemandDTO> child : root.getChildren() )
//        {
//            if ( !child.getChildren().isEmpty() )
//            {
//                final CasterDemandDTO bundleDTO = child.getValue();
//                for ( TreeItem<CasterDemandDTO> childChild : child.getChildren() )
//                {
//                    bundleDTO.setAmount( bundleDTO.getAmount() + childChild.getValue().getAmount() );
//                    bundleDTO.setPlanned( bundleDTO.getPlanned() + childChild.getValue().getPlanned() );
//                    bundleDTO.setRetrieved( bundleDTO.getRetrieved() + childChild.getValue().getRetrieved() );
//                    bundleDTO.setDelivered( bundleDTO.getDelivered() + childChild.getValue().getDelivered() );
//                    if ( bundleDTO.getDeliveryDateFrom() == null || bundleDTO.getDeliveryDateFrom().isAfter( childChild.getValue().getDeliveryDateFrom() ) )
//                    {
//                        bundleDTO.setDeliveryDateFrom( childChild.getValue().getDeliveryDateFrom() );
//                    }
//                    if ( bundleDTO.getDeliveryDateTo() == null || bundleDTO.getDeliveryDateTo().isBefore( childChild.getValue().getDeliveryDateTo() ) )
//                    {
//                        bundleDTO.setDeliveryDateTo( childChild.getValue().getDeliveryDateTo() );
//                    }
//                }
//            }
//        }

        for ( CasterDemandDTO casterDemandDTO : sortedList )
        {
            final TreeItem<CasterDemandDTO> cdTI = new TreeItem<>( casterDemandDTO );
            root.getChildren().add( cdTI );
        }

        if ( summary != null )
        {
            SummaryUtil.addCount( summary, sortedList.size() );
        }
    }
}
