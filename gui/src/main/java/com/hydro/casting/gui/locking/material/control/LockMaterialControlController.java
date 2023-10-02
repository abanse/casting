package com.hydro.casting.gui.locking.material.control;

import com.hydro.casting.server.contract.dto.SlabDTO;
import com.hydro.casting.server.contract.locking.material.dto.LockMaterialRequestDTO;
import com.hydro.casting.server.contract.locking.material.dto.LockableMaterialDTO;
import com.hydro.core.gui.binding.BeanPathAdapter;
import com.hydro.core.gui.comp.AutoCompletionTextField;
import com.hydro.core.gui.comp.StringTextField;
import com.hydro.core.gui.validation.MESValidationSupport;
import com.hydro.core.server.contract.workplace.dto.KeyDescriptionDTO;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.controlsfx.control.CheckTreeView;
import org.controlsfx.validation.Validator;

import java.util.List;
import java.util.function.Predicate;

public class LockMaterialControlController
{
    private static LockMaterialRequestDTO EMPTY_BEAN = new LockMaterialRequestDTO();

    @FXML
    private AutoCompletionTextField<KeyDescriptionDTO> materialLockType;

    @FXML
    private StringTextField materialLockTypeDescription;

    @FXML
    private AutoCompletionTextField<KeyDescriptionDTO> materialLockLocation;

    @FXML
    private StringTextField materialLockLocationDescription;

    @FXML
    private AutoCompletionTextField<KeyDescriptionDTO> imposationMachine;

    @FXML
    private StringTextField imposationMachineDescription;

    @FXML
    private StringTextField reason;

    @FXML
    private CheckTreeView<LockableMaterialDTO> lockableMaterials;

    private BeanPathAdapter<LockMaterialRequestDTO> beanPathAdapter;

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize()
    {

        lockableMaterials.setRoot( new CheckBoxTreeItem<>() );
        lockableMaterials.setShowRoot( false );

        Callback<TreeItem<LockableMaterialDTO>, ObservableValue<Boolean>> getSelectedProperty = item -> {
            if ( item instanceof CheckBoxTreeItem<?> )
            {
                return ( (CheckBoxTreeItem<?>) item ).selectedProperty();
            }
            return null;
        };
        lockableMaterials.setCellFactory( CheckBoxTreeCell.forTreeView( getSelectedProperty, new StringConverter<>()
        {
            @Override
            public String toString( TreeItem<LockableMaterialDTO> treeItem )
            {
                if ( treeItem == null || treeItem.getValue() == null )
                {
                    if ( treeItem.getChildren().isEmpty() )
                    {
                        return "Barren";
                    }
                    else
                    {
                        return "Barren (" + treeItem.getChildren().size() + ")";
                    }
                }
                LockableMaterialDTO lockableMaterialDTO = treeItem.getValue();
                StringBuilder stb = new StringBuilder( lockableMaterialDTO.getMaterialName() );
                if ( lockableMaterialDTO instanceof SlabDTO )
                {
                    stb.append( " " );
                    stb.append( ( (SlabDTO) lockableMaterialDTO ).getAlloy() );
                    stb.append( " " );
                    stb.append( ( (SlabDTO) lockableMaterialDTO ).getWidth() );
                    stb.append( "x" );
                    stb.append( ( (SlabDTO) lockableMaterialDTO ).getLength() );
                }
                return stb.toString();
            }

            @Override
            public TreeItem<LockableMaterialDTO> fromString( String string )
            {
                return null;
            }
        } ) );

        lockableMaterials.getCheckModel().getCheckedItems().addListener( (ListChangeListener<TreeItem<LockableMaterialDTO>>) change -> {
            if ( beanPathAdapter.getBean() == null || beanPathAdapter.getBean().getNewLock() == null || beanPathAdapter.getBean().getNewLock().getLockableMaterials() == null )
            {
                return;
            }
            while ( change.next() )
            {
                if ( change.wasPermutated() == false && change.wasUpdated() == false )
                {
                    final List<LockableMaterialDTO> lockableMaterialDTOList = beanPathAdapter.getBean().getNewLock().getLockableMaterials();
                    for ( TreeItem<LockableMaterialDTO> remitem : change.getRemoved() )
                    {
                        if ( remitem.getValue() == null || remitem.getValue().getMaterialName() == null )
                        {
                            continue;
                        }
                        if ( lockableMaterialDTOList.contains( remitem.getValue() ) )
                        {
                            lockableMaterialDTOList.remove( remitem.getValue() );
                        }
                    }
                    for ( TreeItem<LockableMaterialDTO> additem : change.getAddedSubList() )
                    {
                        if ( additem.getValue() == null || additem.getValue().getMaterialName() == null )
                        {
                            continue;
                        }
                        if ( lockableMaterialDTOList.contains( additem.getValue() ) == false )
                        {
                            lockableMaterialDTOList.add( additem.getValue() );
                        }
                    }
                }
            }
        } );

        beanPathAdapter = new BeanPathAdapter<LockMaterialRequestDTO>( EMPTY_BEAN );

        beanPathAdapter.bindBidirectional( "newLock.materialLockType", materialLockType.textProperty() );
        beanPathAdapter.bindBidirectional( "newLock.materialLockLocation", materialLockLocation.textProperty() );
        beanPathAdapter.bindBidirectional( "newLock.imposationMachine", imposationMachine.textProperty() );
        beanPathAdapter.bindBidirectional( "newLock.reason", reason.textProperty() );

        materialLockTypeDescription.textProperty().bind( materialLockType.keyDescriptionProperty() );
        materialLockLocationDescription.textProperty().bind( materialLockLocation.keyDescriptionProperty() );
        imposationMachineDescription.textProperty().bind( imposationMachine.keyDescriptionProperty() );
    }

    public void loadData( LockMaterialRequestDTO data )
    {
        if ( lockableMaterials != null )
        {
            lockableMaterials.getRoot().getChildren().clear();
        }

        if ( data != null )
        {
            // Autocomplete values
            materialLockType.setValues( data.getMaterialLockTypes() );
            materialLockLocation.setValues( data.getMaterialLockLocations() );
            imposationMachine.setValues( data.getImposationMachines() );

            beanPathAdapter.setBean( data );

            final CheckBoxTreeItem<LockableMaterialDTO> groupElement = new CheckBoxTreeItem<>();
            groupElement.setExpanded( true );
            lockableMaterials.getRoot().getChildren().add( groupElement );

            final List<LockableMaterialDTO> lockableMaterialDTOList = data.getNewLock().getLockableMaterials();
            for ( LockableMaterialDTO lockableMaterialDTO : lockableMaterialDTOList )
            {
                CheckBoxTreeItem<LockableMaterialDTO> childOpHistTI = new CheckBoxTreeItem<>( lockableMaterialDTO );
                groupElement.getChildren().add( childOpHistTI );
            }
            lockableMaterials.getCheckModel().checkAll();

            materialLockType.requestFocus();
        }
        else
        {
            beanPathAdapter.setBean( EMPTY_BEAN );
        }
    }

    public void setValidationSupport( MESValidationSupport validationSupport )
    {
        materialLockType.setValidationSupport( validationSupport, "Code" );
        materialLockLocation.setValidationSupport( validationSupport, "Ort" );
        imposationMachine.setValidationSupport( validationSupport, "Verursacher" );
        reason.setValidationSupport( validationSupport, "Sperrmeldung" );

        validationSupport.registerValidator( lockableMaterials, false,
                Validator.createPredicateValidator( (Predicate<Integer>) numberOfCheckedItems -> numberOfCheckedItems > 0, "Es wurden keine Barren selektiert" ) );
    }
}
