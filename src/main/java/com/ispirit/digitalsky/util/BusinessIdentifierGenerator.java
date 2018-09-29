package com.ispirit.digitalsky.util;

import java.util.UUID;

public class BusinessIdentifierGenerator {

    public static String generatePilotBusinessIdentifier() {
        return generateRandomUUID();
    }

    public static String generateIndividualOperatorBusinessIdentifier() {
        return generateRandomUUID();
    }

    public static String generateOrganizationOperatorBusinessIdentifier() {
        return generateRandomUUID();
    }

    public static String generateManufacturerBusinessIdentifier() {
        return generateRandomUUID();
    }

    private static String generateRandomUUID() {
        UUID uuid = UUID.randomUUID();
        String uuidWithoutHyphen = uuid.toString().replace("-", "");
        return uuidWithoutHyphen;
    }

}
