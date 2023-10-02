package com.hydro.casting.server.ejb.stock.detail;

import com.hydro.casting.server.contract.dto.MaterialCharacteristicDTO;
import com.hydro.casting.server.contract.dto.MaterialCharacteristicsDTO;
import com.hydro.casting.server.contract.dto.MaterialDTO;
import com.hydro.casting.server.model.mat.MaterialCharacteristic;
import com.hydro.casting.server.model.mat.StockMaterial;
import com.hydro.casting.server.model.mat.dao.StockMaterialHome;
import com.hydro.core.server.contract.workplace.DetailProvider;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.*;

@Stateless( name = "MaterialCharacteristicsDetailProvider" )
public class MaterialCharacteristicsDetailProvider implements DetailProvider<MaterialDTO, MaterialCharacteristicsDTO>
{
    @EJB
    private StockMaterialHome stockMaterialHome;

    @Override
    public MaterialCharacteristicsDTO loadDetail( MaterialDTO master, Map<String, String> context )
    {
        if ( master == null )
        {
            return null;
        }
        final StockMaterial stockMaterial = stockMaterialHome.findById( master.getId() );
        if ( stockMaterial == null )
        {
            return null;
        }
        final List<MaterialCharacteristicDTO> materialCharacteristics = new ArrayList<>();
        for ( MaterialCharacteristic characteristic : stockMaterial.getCharacteristics() )
        {
            final MaterialCharacteristicDTO materialCharacteristicDTO = new MaterialCharacteristicDTO();
            materialCharacteristicDTO.setName( characteristic.getName() );
            materialCharacteristicDTO.setDescription( characteristic.getDescription() );
            materialCharacteristicDTO.setValue( characteristic.getValue() );
            materialCharacteristicDTO.setUnit( characteristic.getUnit() );
            materialCharacteristicDTO.setValueFormat( characteristic.getValueFormat() );

            materialCharacteristics.add( materialCharacteristicDTO );
        }

        Collections.sort( materialCharacteristics, Comparator.comparing( MaterialCharacteristicDTO::getName ) );

        final MaterialCharacteristicsDTO materialCharacteristicsDTO = new MaterialCharacteristicsDTO();
        materialCharacteristicsDTO.setMaterialCharacteristics( materialCharacteristics );
        return materialCharacteristicsDTO;
    }
}
