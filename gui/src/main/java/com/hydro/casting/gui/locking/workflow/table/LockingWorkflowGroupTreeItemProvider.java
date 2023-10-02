package com.hydro.casting.gui.locking.workflow.table;

import com.hydro.casting.common.constant.CostCenterEnum;
import com.hydro.casting.server.contract.locking.workflow.dto.LockingWorkflowDTO;
import com.hydro.core.common.util.StringTools;
import com.hydro.core.gui.comp.GroupTreeItemProvider;
import com.hydro.core.gui.util.SummaryUtil;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;
import org.controlsfx.control.IndexedCheckModel;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Predicate;

public class LockingWorkflowGroupTreeItemProvider implements GroupTreeItemProvider<LockingWorkflowDTO>
{
    private final static DecimalFormat N4F = new DecimalFormat( "0000" );

    public IndexedCheckModel<String> ownerModel;
    public IndexedCheckModel<CostCenterEnum> costCenterModel;

    @Override
    public void buildTree( TreeItem<LockingWorkflowDTO> root, List<LockingWorkflowDTO> data, HBox summary )
    {
        Predicate<LockingWorkflowDTO> predicate = null;
        if ( ownerModel != null && costCenterModel != null )
        {
            List<String> allowedOwners = new ArrayList<>();
            List<String> selectedOwners = ownerModel.getCheckedItems();
            selectedOwners.stream().forEach( allowedOwner -> {
                if ( "Produktion".equals( allowedOwner ) )
                {
                    allowedOwners.add( LockingWorkflowDTO.OWNER_PROD );
                }
                allowedOwners.add( allowedOwner );
            } );

            List<String> allowedCBUCodes = new ArrayList<>();
            List<String> allowedCostCenters = new ArrayList<>();
            List<CostCenterEnum> selectedCostCenters = costCenterModel.getCheckedItems();
            selectedCostCenters.stream().forEach( lwCostCenter -> {
                if ( lwCostCenter.getGroup() != null && lwCostCenter.getGroup().startsWith( "*" ) )
                {
                    if ( lwCostCenter.getGroup().length() > 1 )
                    {
                        final String group = lwCostCenter.getGroup().substring( 1 );
                        for ( CostCenterEnum entry : CostCenterEnum.values() )
                        {
                            if ( entry.getGroup() != null && entry.getGroup().equals( group ) )
                            {
                                allowedCostCenters.add( entry.getCostCenter() );
                            }
                        }
                    }
                }
                else
                {
                    allowedCostCenters.add( lwCostCenter.getCostCenter() );
                }
            } );

            predicate = new Predicate<LockingWorkflowDTO>()
            {
                @Override
                public boolean test( LockingWorkflowDTO lockingWorkflowDTO )
                {
                    if ( lockingWorkflowDTO == null )
                    {
                        return false;
                    }
                    // check owner
                    if ( lockingWorkflowDTO.getOwner() != null && allowedOwners.contains( lockingWorkflowDTO.getOwner() ) == false )
                    {
                        return false;
                    }
                    // check cbuCode
                    if ( allowedCBUCodes.isEmpty() == false )
                    {
                        if ( lockingWorkflowDTO.getCbuCode() != null && allowedCBUCodes.contains( lockingWorkflowDTO.getCbuCode() ) == false )
                        {
                            return false;
                        }
                    }
                    // check costCenter
                    if ( allowedCostCenters.isEmpty() == false )
                    {
                        if ( lockingWorkflowDTO.getKst() != null && allowedCostCenters.contains( lockingWorkflowDTO.getKst() ) == false )
                        {
                            return false;
                        }
                    }
                    return true;
                }
            };
        }

        // prepare data for tree
        List<LockingWorkflowDTO> rootElements = new ArrayList<>();
        for ( final LockingWorkflowDTO lockingWorkflowDTO : data )
        {
            if ( predicate != null && predicate.test( lockingWorkflowDTO ) == false )
            {
                continue;
            }

//            LockingWorkflowDTO existingRoot = rootElements.stream().filter( ( rootLW ) -> {
//                //@formatter:off
//
//                if ( Objects.equals( rootLW.getLot(), lockingWorkflowDTO.getLot() ) &&
//                        Objects.equals( rootLW.getSublot(), lockingWorkflowDTO.getSublot() ) &&
//                        Objects.equals( rootLW.getInvSuffix(), lockingWorkflowDTO.getInvSuffix() ) &&
//                        Objects.equals( rootLW.getOpSeq(), lockingWorkflowDTO.getOpSeq() ) )
//                {
//                    // Wenn es das gleiche Material ist nur unterschiedliche sperren hat,
//                    // dann keine Gruppierung des Knotens
//                    String dropId = lockingWorkflowDTO.getDropId();
//                    String rootDropId = rootLW.getDropId();
//                    Integer cutId = lockingWorkflowDTO.getCutId();
//                    Integer rootCutId = rootLW.getCutId();
//                    Integer paletteId = lockingWorkflowDTO.getPaletteId();
//                    Integer rootPaletteId = rootLW.getPaletteId();
//                    boolean cutIdEmpty = cutId == null || cutId == 0;
//                    boolean rootCutIdEmpty = rootCutId == null || rootCutId == 0;
//                    boolean rootDropIdEmpty = StringTools.isNullOrEmpty(rootDropId);
//                    boolean dropIdEmpty = StringTools.isNullOrEmpty(dropId);
//
//                    boolean paletteIdEmpty = paletteId == null || paletteId == 0;
//                    boolean rootPaletteIdEmpty = rootPaletteId == null || rootPaletteId == 0;
//                    if ( ( ( Objects.equals( rootLW.getDropId(), lockingWorkflowDTO.getDropId() ) || ( dropIdEmpty && rootDropIdEmpty ) ) &&
//                           ( Objects.equals( rootLW.getCutId(), lockingWorkflowDTO.getCutId() ) || ( cutIdEmpty && rootCutIdEmpty ) ) &&
//                           ( Objects.equals( rootLW.getPaletteId(), lockingWorkflowDTO.getPaletteId() ) || ( paletteIdEmpty  && rootPaletteIdEmpty ) ) ) )
//                    {
//                        return false;
//                    }
//                    return true;
//                }
//                //@formatter:on
//
//                return false;
//            } ).findFirst().orElse( null );
//            if ( existingRoot != null )
//            {
//                if ( existingRoot.getChilds() == null )
//                {
//                    // Create new parent node
//                    LockingWorkflowDTO newParentLW = new LockingWorkflowDTO();
//                    newParentLW.setLockRecId( existingRoot.getId() );
//                    newParentLW.setLot( existingRoot.getLot() );
//                    newParentLW.setSublot( existingRoot.getSublot() );
//                    newParentLW.setInvSuffix( existingRoot.getInvSuffix() );
//                    newParentLW.setOpSeq( existingRoot.getOpSeq() );
//
//                    ArrayList<LockingWorkflowDTO> childs = new ArrayList<>();
//                    newParentLW.setChilds( childs );
//                    childs.add( existingRoot );
//                    existingRoot.setParent( newParentLW );
//                    rootElements.remove( existingRoot );
//                    existingRoot = newParentLW;
//                    rootElements.add( existingRoot );
//                }
//                lockingWorkflowDTO.setParent( existingRoot );
//                existingRoot.getChilds().add( lockingWorkflowDTO );
//                Collections.sort( existingRoot.getChilds(), new Comparator<LockingWorkflowDTO>()
//                {
//                    @Override
//                    public int compare( LockingWorkflowDTO o1, LockingWorkflowDTO o2 )
//                    {
//                        final String o1SortKey = buildInnerSortKey( o1 );
//                        final String o2SortKey = buildInnerSortKey( o2 );
//                        return o1SortKey.compareTo( o2SortKey );
//                    }
//                } );
//            }
//            else if ( lockingWorkflowDTO.createStrip() )
//            {
//                LockingWorkflowDTO newParentLW = new LockingWorkflowDTO();
//                newParentLW.setLockRecId( lockingWorkflowDTO.getId() );
//                newParentLW.setLot( lockingWorkflowDTO.getLot() );
//                newParentLW.setSublot( lockingWorkflowDTO.getSublot() );
//                newParentLW.setInvSuffix( lockingWorkflowDTO.getInvSuffix() );
//                newParentLW.setOpSeq( lockingWorkflowDTO.getOpSeq() );
//                ArrayList<LockingWorkflowDTO> childs = new ArrayList<>();
//                lockingWorkflowDTO.setParent( newParentLW );
//                childs.add( lockingWorkflowDTO );
//                newParentLW.setChilds( childs );
//                rootElements.add( newParentLW );
//            }
//            else
//            {
                rootElements.add( lockingWorkflowDTO );
//            }
        }

        // Build tree
        List<TreeItem<LockingWorkflowDTO>> rootTreeElements = new ArrayList<>();

        long count = 0;
        for ( final LockingWorkflowDTO lockingWorkflowDTO : rootElements )
        {
            TreeItem<LockingWorkflowDTO> lockingWorkflowTreeItem = new TreeItem<>( lockingWorkflowDTO );

            int innerCount = 0;
            if ( lockingWorkflowDTO.getChilds() != null )
            {
                lockingWorkflowTreeItem.setExpanded( true );

                for ( LockingWorkflowDTO childElement : lockingWorkflowDTO.getChilds() )
                {
                    TreeItem<LockingWorkflowDTO> lockingWorkflowChildTreeItem = new TreeItem<>( childElement );
                    lockingWorkflowTreeItem.getChildren().add( lockingWorkflowChildTreeItem );
                    innerCount++;
                }
            }

            if ( innerCount > 0 )
            {
                count = count + innerCount;
            }
            else
            {
                count++;
            }
            rootTreeElements.add( lockingWorkflowTreeItem );
        }
        root.getChildren().addAll( rootTreeElements );

        SummaryUtil.addCount( summary, count );
    }

    public IndexedCheckModel<String> getOwnerModel()
    {
        return ownerModel;
    }

    public void setOwnerModel( IndexedCheckModel<String> ownerModel )
    {
        this.ownerModel = ownerModel;
    }

    public IndexedCheckModel<CostCenterEnum> getCostCenterModel()
    {
        return costCenterModel;
    }

    public void setCostCenterModel( IndexedCheckModel<CostCenterEnum> costCenterModel )
    {
        this.costCenterModel = costCenterModel;
    }

//    private String buildInnerSortKey( LockingWorkflowDTO lockingWorkflowDTO )
    //    {
    //        if ( lockingWorkflowDTO == null )
    //        {
    //            return "";
    //        }
    //        final StringBuilder sortKeyBuilder = new StringBuilder();
    //        if ( lockingWorkflowDTO.getCutId() != null )
    //        {
    //            sortKeyBuilder.append( N4F.format( lockingWorkflowDTO.getCutId() ) );
    //        }
    //        else
    //        {
    //            sortKeyBuilder.append( N4F.format( 0 ) );
    //        }
    //        if ( lockingWorkflowDTO.getPaletteId() != null )
    //        {
    //            sortKeyBuilder.append( N4F.format( lockingWorkflowDTO.getPaletteId() ) );
    //        }
    //        else
    //        {
    //            sortKeyBuilder.append( N4F.format( 0 ) );
    //        }
    //        if ( StringTools.isFilled( lockingWorkflowDTO.getDropId() ) )
    //        {
    //            sortKeyBuilder.append( lockingWorkflowDTO.getDropId() );
    //        }
    //
    //        return sortKeyBuilder.toString();
    //    }

}
