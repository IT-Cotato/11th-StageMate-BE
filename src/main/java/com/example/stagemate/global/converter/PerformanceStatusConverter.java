package com.example.stagemate.global.converter;

import com.example.stagemate.domain.performances.PerformanceStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class PerformanceStatusConverter implements AttributeConverter<PerformanceStatus, String> {
    @Override
    public String convertToDatabaseColumn(PerformanceStatus performanceStatus) {
        return performanceStatus.getDescription();
    }

    @Override
    public PerformanceStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        String status = dbData.trim();

        return PerformanceStatus.fromDescription(status);
    }

}
