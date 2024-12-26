package com.hydro.casting.server.ejb.analysis;

import com.hydro.casting.server.contract.analysis.LimsLabelPreregistrationView;
import com.hydro.casting.server.contract.dto.LimsLabelPreregistrationDTO;
import com.hydro.casting.server.ejb.analysis.service.LimsLabelPreregistrationService;
import com.hydro.core.common.exception.BusinessException;
import com.hydro.eai.lims.model.LimsLabelPreregistration;
import com.hydro.eai.lims.model.dao.LimsLabelPreregistrationHome;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

@Stateless
public class LimsLabelPreregistrationViewBean implements LimsLabelPreregistrationView {

    @EJB
    private LimsLabelPreregistrationService limsLabelPreregistrationService;

    @EJB
    private LimsLabelPreregistrationHome limsLabelPreregistrationHome;


    @Override
    public List<LimsLabelPreregistrationDTO> list(Map<String, Object> context) {
        return limsLabelPreregistrationService.load();
    }

    @Override
    public LimsLabelPreregistrationDTO create(Map<String, Object> context) throws BusinessException {
        return null;
    }

    @Override
    public List<LimsLabelPreregistrationDTO> copy(Map<String, Object> context, List<LimsLabelPreregistrationDTO> sourceViewDTOs) throws BusinessException {
        return List.of();
    }

    @Override
    public LimsLabelPreregistrationDTO update(Map<String, Object> context, LimsLabelPreregistrationDTO viewDTO, String id, Object oldValue, Object newValue) throws BusinessException {
        return null;
    }

    @Override
    public void delete(Map<String, Object> context, List<LimsLabelPreregistrationDTO> viewDTOs) throws BusinessException {

    }
}
