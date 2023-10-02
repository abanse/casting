package com.hydro.casting.gui.planning.table.grouping;

import com.hydro.casting.server.contract.dto.CasterDemandDTO;
import com.hydro.core.gui.comp.GroupTreeItemProvider;
import com.hydro.core.gui.util.SummaryUtil;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CasterDemandMaterialTypeTableGroup implements GroupTreeItemProvider<CasterDemandDTO>
{
    @Override
    public void buildTree( TreeItem<CasterDemandDTO> root, List<CasterDemandDTO> data, HBox summary )
    {
        if ( data.isEmpty() )
        {
            return;
        }
        // sortieren
        final List<CasterDemandDTO> sortedList = data.stream().filter( casterDemandDTO -> {
            return casterDemandDTO.getMaterialType() != null;
        } ).sorted( ( o1, o2 ) -> {
            return Comparator.comparing( CasterDemandDTO::getAlloy ).thenComparing( CasterDemandDTO::getQuality ).thenComparingInt( CasterDemandDTO::getHeight )
                    .thenComparingInt( CasterDemandDTO::getWidth ).thenComparingInt( CasterDemandDTO::getLength ).compare( o1, o2 );
        } ).collect( Collectors.toList() );

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
