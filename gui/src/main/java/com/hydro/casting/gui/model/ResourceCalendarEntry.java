package com.hydro.casting.gui.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDateTime;

public class ResourceCalendarEntry extends NamedModelElement
{
    private static final long serialVersionUID = 1L;

    private final SimpleObjectProperty<LocalDateTime> startTS;
    private final SimpleIntegerProperty duration;
    private final SimpleStringProperty description;

    private Resource resource;

    public ResourceCalendarEntry()
    {
        startTS = new SimpleObjectProperty<>();
        duration = new SimpleIntegerProperty();
        description = new SimpleStringProperty();
    }

    public LocalDateTime getStartTS()
    {
        return startTS.get();
    }

    public SimpleObjectProperty<LocalDateTime> startTSProperty()
    {
        return startTS;
    }

    public void setStartTS( LocalDateTime startTS )
    {
        this.startTS.set( startTS );
    }

    public int getDuration()
    {
        return duration.get();
    }

    public SimpleIntegerProperty durationProperty()
    {
        return duration;
    }

    public void setDuration( int duration )
    {
        this.duration.set( duration );
    }

    public String getDescription()
    {
        return description.get();
    }

    public SimpleStringProperty descriptionProperty()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description.set( description );
    }

    public Resource getResource()
    {
        return resource;
    }

    public void setResource( Resource resource )
    {
        this.resource = resource;
    }

    @Override
    public void invalidate()
    {
    }
}
