package com.hydro.casting.gui.solver;

import com.hydro.casting.gui.model.Transfer;
import com.hydro.casting.gui.solver.exception.NoSolutionFoundException;
import com.hydro.casting.gui.solver.model.SolverVariable;

import java.util.List;

public interface ISolver
{
    enum SolverHint
    {
        FIND_TARGETS, // Sucht bei Elementen mit min und max die Mitte zu treffen (Zeitaufwendig)
        FIND_SPEC_MATERIAL_WEIGHT, // Setzt nur das Gewicht vom artgleichen Schrott (kein Solver-Lauf)
        FIND_CONCRETE_TARGET, // Deaktiviert den Näherungsalgorithmus und setzt ein konkretes Ziel in der
                              // Mitte
        FIND_WINDOW_MIN_TO_MIDDLE // Setzt ein Zielfenster von min bis mitte
    };

    void solve( List<SolverVariable> solverVariables, Transfer transfer, List<SolverVariable> fixedTransferMaterials, SolverVariable fillmentTransferMaterial, double varWeights,
            SolverHint... solverHints ) throws NoSolutionFoundException;

    void cancel();
}
