package com.ispirit.digitalsky.service.api;


import com.ispirit.digitalsky.domain.Pilot;

public interface PilotService {

    Pilot createNewPilot(Pilot pilot);

    Pilot updatePilot(long id, Pilot pilot);

    Pilot find(long id);
}
