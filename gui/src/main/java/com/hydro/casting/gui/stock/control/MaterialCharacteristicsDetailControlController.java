package com.hydro.casting.gui.stock.control;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hydro.casting.server.contract.dto.MaterialCharacteristicDTO;
import com.hydro.casting.server.contract.dto.MaterialCharacteristicsDTO;
import com.hydro.casting.server.contract.dto.MaterialDTO;
import com.hydro.casting.server.contract.stock.StockMaterialView;
import com.hydro.core.gui.comp.ServerTableView;
import com.hydro.core.gui.control.DetailController;
import javafx.fxml.FXML;

import java.util.ArrayList;
import java.util.List;

public class MaterialCharacteristicsDetailControlController extends DetailController<MaterialDTO, MaterialCharacteristicsDTO>
{
    public MaterialCharacteristicsDetailControlController()
    {
        super( StockMaterialView.class, MaterialCharacteristicsDTO.class );
    }

    @Inject
    private Injector injector;

    @FXML
    private ServerTableView<MaterialCharacteristicDTO> characteristicTable;

    private List<MaterialCharacteristicDTO> materialCharacteristics = new ArrayList<>();

    /**
     * Initializes the controller class.
     */
    @FXML
    private void initialize()
    {
        characteristicTable.connect( injector, () -> materialCharacteristics );
    }

    @Override
    public void loadDetail( MaterialCharacteristicsDTO data )
    {
        materialCharacteristics.clear();
        if ( data != null && data.getMaterialCharacteristics() != null )
        {
            materialCharacteristics.addAll( data.getMaterialCharacteristics() );
        }
        characteristicTable.loadData();
    }
}
