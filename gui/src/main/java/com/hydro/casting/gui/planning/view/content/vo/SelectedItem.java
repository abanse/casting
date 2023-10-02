package com.hydro.casting.gui.planning.view.content.vo;

import com.hydro.core.server.contract.workplace.dto.ViewDTO;

import java.util.List;

public class SelectedItem
{
    private Object source;
    private List<? extends ViewDTO> items;

    public SelectedItem( Object source, List<? extends ViewDTO> items )
    {
        super();
        this.source = source;
        this.items = items;
    }

    public Object getSource()
    {
        return source;
    }

    public void setSource( Object source )
    {
        this.source = source;
    }

    public List<? extends ViewDTO> getItems()
    {
        return items;
    }

    public void setItems( List<? extends ViewDTO> items )
    {
        this.items = items;
    }

    @Override
    public String toString()
    {
        return "SelectedItem [source=" + source + ", items=" + items + "]";
    }
}