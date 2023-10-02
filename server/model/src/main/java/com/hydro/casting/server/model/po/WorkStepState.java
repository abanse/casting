package com.hydro.casting.server.model.po;

public interface WorkStepState
{
    int UNPLANNED = 100;
    int PLANNED = 200;
    int IN_PROGRESS = 300;
    int SUCCESS = 400;
    int PAUSED = 350;
    int FAILED = 500;
    int CANCELED = 600;
}

