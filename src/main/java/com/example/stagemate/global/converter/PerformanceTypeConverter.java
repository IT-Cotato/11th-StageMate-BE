package com.example.stagemate.global.converter;

import com.example.stagemate.domain.performances.PerformanceType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class PerformanceTypeConverter implements AttributeConverter<PerformanceType, String> {
    @Override
    public String convertToDatabaseColumn(PerformanceType performanceType) {
        return performanceType.getDescription();
    }

    @Override
    public PerformanceType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        String type = dbData.trim();

        return PerformanceType.fromDescription(type);

    }
}
