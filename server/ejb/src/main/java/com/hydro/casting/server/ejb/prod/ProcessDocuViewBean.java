package com.hydro.casting.server.ejb.prod;

import com.hydro.casting.server.contract.dto.*;
import com.hydro.casting.server.contract.prod.ProcessDocuView;
import com.hydro.casting.server.ejb.prod.service.ProcessDocuService;
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
public class ProcessDocuViewBean implements ProcessDocuView
{
    private Map<Class<?>, DetailProvider<ProcessDocuDTO, ?>> detailProviders = new HashMap<>();

    @EJB
    private ProcessDocuService processDocuService;

    @EJB( beanName = "ChargeDetailProvider" )
    private DetailProvider<ProcessDocuDTO, CasterScheduleDTO> chargeDetailProvider;

    @EJB( beanName = "EquipmentConditionProvider" )
    private DetailProvider<ProcessDocuDTO, EquipmentConditionDTO> equipmentConditionProvider;

    @EJB( beanName = "ProcessDocuAnalysisDetailProvider" )
    private DetailProvider<ProcessDocuDTO, AnalysisDetailDTO> analysisDetailProvider;

    @EJB( beanName = "ActualValuesDetailProvider" )
    private DetailProvider<ProcessDocuDTO, ActualValuesDTO> actualValuesDetailProvider;

    @EJB( beanName = "VisualInspectionDetailProvider" )
    private DetailProvider<ProcessDocuDTO, VisualInspectionDTO> visualInspectionDetailProvider;

    @EJB( beanName = "CastingPreparationDetailProvider" )
    private DetailProvider<ProcessDocuDTO, CastingPreparationDTO> castingPreparationDetailProvider;

    @EJB( beanName = "CastingPreparationExaminationDetailProvider" )
    private DetailProvider<ProcessDocuDTO, CastingPreparationExaminationDTO> castingPreparationExaminationDetailProvider;

    @PostConstruct
    private void postConstruct()
    {
        detailProviders.put( CasterScheduleDTO.class, chargeDetailProvider );
        detailProviders.put( EquipmentConditionDTO.class, equipmentConditionProvider );
        detailProviders.put( AnalysisDetailDTO.class, analysisDetailProvider );
        detailProviders.put( ActualValuesDTO.class, actualValuesDetailProvider );
        detailProviders.put( VisualInspectionDTO.class, visualInspectionDetailProvider );
        detailProviders.put( CastingPreparationDTO.class, castingPreparationDetailProvider );
        detailProviders.put( CastingPreparationExaminationDTO.class, castingPreparationExaminationDetailProvider );
    }

    @Override
    public List<ProcessDocuDTO> loadBySearchType( SearchType searchType, Map<String, Object> parameters )
    {
        return processDocuService.loadBySearchType( searchType, parameters );
    }

    @Override
    public <D extends ViewDTO> D loadDetail( Class<D> dto, ProcessDocuDTO master, Map<String, String> context )
    {
        @SuppressWarnings( "unchecked" )
        DetailProvider<ProcessDocuDTO, D> provider = (DetailProvider<ProcessDocuDTO, D>) detailProviders.get( dto );
        if ( provider != null )
        {
            return provider.loadDetail( master, context );
        }
        return null;
    }

    @Override
    public ViewModel<ProcessDocuDTO> validate( String currentUser, List<ProcessDocuDTO> ProcessDocuDTOList )
    {
        final ViewModel<ProcessDocuDTO> viewModel = new ViewModel<>();
        viewModel.setCurrentDTOs( ProcessDocuDTOList.toArray( new ProcessDocuDTO[0] ) );

        return viewModel;
    }
}