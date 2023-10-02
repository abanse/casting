package com.hydro.casting.gui.planning.table.grouping;

import com.hydro.casting.server.contract.dto.FurnaceDemandDTO;
import com.hydro.core.gui.comp.GroupTreeItemProvider;
import com.hydro.core.gui.util.SummaryUtil;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FurnaceDemandTableGroup implements GroupTreeItemProvider<FurnaceDemandDTO>
{
    private SingleSelectionModel<String> groupingSelection;

    public FurnaceDemandTableGroup( SingleSelectionModel<String> groupingSelection )
    {
        this.groupingSelection = groupingSelection;
    }

    @Override
    public void buildTree( TreeItem<FurnaceDemandDTO> root, List<FurnaceDemandDTO> data, HBox summary )
    {
        if ( data.isEmpty() )
        {
            return;
        }
        if ( groupingSelection.getSelectedIndex() == 1 )
        {
            final List<FurnaceDemandDTO> alloySortedList = data.stream().sorted( Comparator.comparing( FurnaceDemandDTO::getAlloy ) ).collect( Collectors.toList() );
            TreeItem<FurnaceDemandDTO> alloyItem = null;
            for ( FurnaceDemandDTO furnaceDemandDTO : alloySortedList )
            {
                if ( alloyItem == null || !Objects.equals( alloyItem.getValue().getAlloy(), furnaceDemandDTO.getAlloy() ) )
                {
                    final FurnaceDemandDTO alloyFurnaceDemand = new FurnaceDemandDTO();
                    alloyFurnaceDemand.setId( furnaceDemandDTO.getAlloy().hashCode() );
                    alloyFurnaceDemand.setAlloy( furnaceDemandDTO.getAlloy() );
                    alloyFurnaceDemand.setCharge( "" );

                    alloyItem = new TreeItem<>( alloyFurnaceDemand );
                    //alloyItem.setExpanded( true );
                    root.getChildren().add( alloyItem );
                }
                alloyItem.getChildren().add( new TreeItem<>( furnaceDemandDTO ) );
            }
            // Werte summieren
            for ( TreeItem<FurnaceDemandDTO> child : root.getChildren() )
            {
                final FurnaceDemandDTO furnaceDemandDTO = child.getValue();

                for ( TreeItem<FurnaceDemandDTO> childChild : child.getChildren() )
                {
                    final FurnaceDemandDTO childFurnaceDemandDTO = childChild.getValue();
                    furnaceDemandDTO.setPlannedWeight( furnaceDemandDTO.getPlannedWeight() + childFurnaceDemandDTO.getPlannedWeight() );
                    furnaceDemandDTO.setFurnaceMaxWeight( furnaceDemandDTO.getFurnaceMaxWeight() + childFurnaceDemandDTO.getFurnaceMaxWeight() );
                    furnaceDemandDTO.setBottomWeight( furnaceDemandDTO.getBottomWeight() + childFurnaceDemandDTO.getBottomWeight() );
                    furnaceDemandDTO.setPlannedELWeight( furnaceDemandDTO.getPlannedELWeight() + childFurnaceDemandDTO.getPlannedELWeight() );
                    furnaceDemandDTO.setPlannedS1Weight( furnaceDemandDTO.getPlannedS1Weight() + childFurnaceDemandDTO.getPlannedS1Weight() );
                    furnaceDemandDTO.setPlannedS2Weight( furnaceDemandDTO.getPlannedS2Weight() + childFurnaceDemandDTO.getPlannedS2Weight() );
                    furnaceDemandDTO.setPlannedS3Weight( furnaceDemandDTO.getPlannedS3Weight() + childFurnaceDemandDTO.getPlannedS3Weight() );
                    furnaceDemandDTO.setPlannedRAWeight( furnaceDemandDTO.getPlannedRAWeight() + childFurnaceDemandDTO.getPlannedRAWeight() );
                    if ( furnaceDemandDTO.getStartTime() == null || furnaceDemandDTO.getStartTime().isAfter( childFurnaceDemandDTO.getStartTime() ) )
                    {
                        furnaceDemandDTO.setStartTime( childFurnaceDemandDTO.getStartTime() );
                    }
                    if ( furnaceDemandDTO.getEndTime() == null || furnaceDemandDTO.getEndTime().isBefore( childFurnaceDemandDTO.getEndTime() ) )
                    {
                        furnaceDemandDTO.setEndTime( childFurnaceDemandDTO.getEndTime() );
                    }
                }
            }
        }
        else
        {
            for ( FurnaceDemandDTO furnaceDemandDTO : data )
            {
                final TreeItem<FurnaceDemandDTO> cdTI = new TreeItem<>( furnaceDemandDTO );
                root.getChildren().add( cdTI );
            }
        }

        if ( summary != null )
        {
            SummaryUtil.addCount( summary, data.size() );
        }
    }
}
