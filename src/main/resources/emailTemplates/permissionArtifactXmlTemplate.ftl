<UAPermission>
    <Permission>
    <Owner operatorID="${operatorId}" operatorType="${operatorType}">
        <Pilot id="${pilotId}" validTo="NA"/>
    </Owner>
    <FlightDetails>
        <UADetails uinNo="${uinNumber}"/>
        <FlightPurpose shortDesc="${purposeOfFlight}"/>
        <PayloadDetails payLoadWeightInKg="${payloadWeightInKg}" payloadDetails="${payloadDetails}"/>
        <FlightParameters flightStartTime="${startDateTime}" flightEndTime="${endDateTime}">
            <Coordinates>
                ${coordinates}
            </Coordinates>
        </FlightParameters>
    </FlightDetails>

    </Permission>
</UAPermission>