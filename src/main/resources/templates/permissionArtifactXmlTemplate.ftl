<UAPermission>
    <Permission>
    <Owner operatorID="${operatorId}">
        <Pilot id="${pilotId}" validTo="NA"/>
    </Owner>
    <FlightDetails>
        <UADetails uinNo="${uinNumber}"/>
        <FlightPurpose shortDesc="${purposeOfFlight}"/>
        <PayloadDetails payLoadWeightInKg="${payloadWeightInKg}" payloadDetails="${payloadDetails}"/>
        <FlightParameters flightStartTime="${startDateTime}" flightEndTime="${endDateTime}" recurrenceTimeExpression="${(recurrenceTimeExpression)!}" recurrenceTimeExpressionType="${(recurrenceTimeExpressionType)!}" recurringTimeDurationInMinutes="${(recurringTimeDurationInMinutes)!}">
            <Coordinates>
                ${coordinates}
            </Coordinates>
        </FlightParameters>
    </FlightDetails>
    </Permission>
</UAPermission>