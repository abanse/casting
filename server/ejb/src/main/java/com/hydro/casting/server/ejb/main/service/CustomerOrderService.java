package com.hydro.casting.server.ejb.main.service;

import com.hydro.casting.server.model.mat.MaterialType;
import com.hydro.casting.server.model.po.CustomerOrder;
import com.hydro.casting.server.model.po.CustomerOrderItem;
import com.hydro.casting.server.model.po.dao.CustomerOrderHome;
import com.hydro.casting.server.model.po.dao.CustomerOrderItemHome;
import com.hydro.core.common.exception.BusinessException;
import com.hydro.core.common.exception.BusinessObjectNotFoundException;
import com.hydro.eai.cms.model.Kundenauftrag;
import com.hydro.eai.cms.model.dao.KundenauftragHome;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class CustomerOrderService
{
    @EJB
    private CustomerOrderHome customerOrderHome;
    @EJB
    private CustomerOrderItemHome customerOrderItemHome;
    @EJB
    private KundenauftragHome kundenauftragHome;
    @EJB
    private MaterialService materialService;

    public CustomerOrderItem findOrCreateCustomerOrderItem( final String customerOrderItemApk ) throws BusinessException
    {
        CustomerOrderItem customerOrderItem = customerOrderItemHome.findByApk( customerOrderItemApk );

        if ( customerOrderItem == null )
        {
            // Im CMS nach einem COI suchen
            final Kundenauftrag kundenauftrag = kundenauftragHome.findByAuftragsIdAndPosNr( customerOrderItemApk );
            if ( kundenauftrag == null )
            {
                throw new BusinessObjectNotFoundException( "Kundenauftrag", customerOrderItemApk );
            }
            CustomerOrder customerOrder = customerOrderHome.findByApk( kundenauftrag.getAuftragsid() );
            if ( customerOrder == null )
            {
                customerOrder = new CustomerOrder();
                customerOrder.setApk( kundenauftrag.getAuftragsid() );
                customerOrderHome.persist( customerOrder );
            }
            customerOrder.setOrderDate( kundenauftrag.getAuftragsdatum() );

            customerOrderItem = new CustomerOrderItem();
            customerOrderItem.setApk( customerOrderItemApk );
            customerOrderItem.setCustomerOrder( customerOrder );
            customerOrderItemHome.persist( customerOrderItem );
            customerOrderItem.setRequestedDateFrom( kundenauftrag.getLieferterminvon() );
            customerOrderItem.setRequestedDateTo( kundenauftrag.getLieferterminbis() );
            customerOrderItem.setRequestedQuantity( kundenauftrag.getMengestueck() );
            customerOrderItem.setExperimentNumber( kundenauftrag.getVersuchnr() );

            final MaterialType materialType = materialService.findOrCreateMaterialType( kundenauftrag.getArtikelnr() );
            customerOrderItem.setMaterialType( materialType );
        }

        return customerOrderItem;
    }

    public CustomerOrderItem find( String customerOrderItemApk ) throws BusinessException
    {
        CustomerOrderItem customerOrderItem = customerOrderItemHome.findByApk( customerOrderItemApk );

        if ( customerOrderItem == null )
        {
            throw new BusinessObjectNotFoundException( "CustomerOrderItem", customerOrderItemApk );
        }
        return customerOrderItem;
    }
}
