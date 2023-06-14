package com.km.recipe.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.commons.lang3.StringUtils;

@Converter
public class NameConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String s) {
        return StringUtils.capitalize(StringUtils.lowerCase(s));
    }

    @Override
    public String convertToEntityAttribute(String s) {
        return s;
    }
}
