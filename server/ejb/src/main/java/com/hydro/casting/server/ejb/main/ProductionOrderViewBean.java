package com.hydro.casting.server.ejb.main;

import com.hydro.casting.server.contract.dto.ProductionOrderDTO;
import com.hydro.casting.server.contract.dto.WorkStepListDTO;
import com.hydro.casting.server.contract.main.ProductionOrderView;
import com.hydro.casting.server.ejb.main.service.ProductionOrderService;
import com.hydro.core.server.contract.workplace.DetailProvider;
import com.hydro.core.server.contract.workplace.SearchType;
import com.hydro.core.server.contract.workplace.ViewModel;
import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class ProductionOrderViewBean implements ProductionOrderView
{
    private Map<Class<?>, DetailProvider<ProductionOrderDTO, ?>> detailProviders = new HashMap<>();

    @EJB
    private ProductionOrderService productionOrderService;

    @EJB( beanName = "WorkStepDetailProvider" )
    private DetailProvider<ProductionOrderDTO, WorkStepListDTO> workStepDetailProvider;

    @PostConstruct
    private void postConstruct()
    {
        detailProviders.put( WorkStepListDTO.class, workStepDetailProvider );
    }

    @Override
    public List<ProductionOrderDTO> loadBySearchType( SearchType searchType, Map<String, Object> parameters )
    {
        return productionOrderService.loadBySearchType( searchType, parameters );
    }

    @Override
    public <D extends ViewDTO> D loadDetail( Class<D> dto, ProductionOrderDTO master, Map<String, String> context )
    {
        @SuppressWarnings( "unchecked" )
        DetailProvider<ProductionOrderDTO, D> provider = (DetailProvider<ProductionOrderDTO, D>) detailProviders.get( dto );
        if ( provider != null )
        {
            return provider.loadDetail( master, context );
        }
        return null;
    }

    @Override
    public ViewModel<ProductionOrderDTO> validate( String currentUser, List<ProductionOrderDTO> productionOrderDTOList )
    {
        final ViewModel<ProductionOrderDTO> viewModel = new ViewModel<>();
        viewModel.setCurrentDTOs( productionOrderDTOList.toArray( new ProductionOrderDTO[productionOrderDTOList.size()] ) );

        return viewModel;
    }
}