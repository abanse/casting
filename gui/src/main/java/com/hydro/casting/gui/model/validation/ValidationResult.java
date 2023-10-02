package com.hydro.casting.gui.model.validation;

import com.hydro.casting.gui.model.ModelElement;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult
{
    private List<String> errors;
    private List<ModelElement> sources;

    public void addError( ModelElement source, String error )
    {
        if ( sources == null )
        {
            sources = new ArrayList<>();
        }
        sources.add( source );
        if ( errors == null )
        {
            errors = new ArrayList<>();
        }
        errors.add( error );
    }

    public boolean hasErrors()
    {
        return errors != null;
    }

    public List<String> getErrors()
    {
        return errors;
    }

    public List<ModelElement> getSources()
    {
        return sources;
    }
}
