package com.hydro.casting.gui.solver.impl;

import com.hydro.casting.gui.solver.ISolver;
import org.jacop.core.Var;
import org.jacop.search.DepthFirstSearch;

public abstract class ASolver implements ISolver
{
    protected DepthFirstSearch<? extends Var> search;

    protected boolean containHint( SolverHint[] currentHints, SolverHint hint )
    {
        for ( SolverHint currentHint : currentHints )
        {
            if ( currentHint == hint )
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void cancel()
    {
        if ( search != null )
        {
            search.setTimeOut( 0 );
        }
    }
}
