package com.ispirit.digitalsky.util;

import com.ispirit.digitalsky.domain.AirspaceCategory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Converter(autoApply = true)
public class AirspaceCategoryTypeConverter implements AttributeConverter<AirspaceCategory.Type, String> {

    @Override
    public String convertToDatabaseColumn(AirspaceCategory.Type type) {
        return (type == null ? null : type.name());
    }

    @Override
    public AirspaceCategory.Type convertToEntityAttribute(String type) {
        return (type == null ? null : AirspaceCategory.Type.valueOf(type));
    }
}
