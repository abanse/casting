package com.hydro.casting.server.contract.stock;

import com.hydro.casting.server.contract.dto.MaterialDTO;
import com.hydro.core.server.contract.workplace.MasterDetailProvider;

import javax.ejb.Remote;

@Remote
public interface StockMaterialView extends MasterDetailProvider<MaterialDTO>
{
}
