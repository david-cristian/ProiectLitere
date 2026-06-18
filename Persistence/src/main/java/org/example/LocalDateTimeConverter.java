package org.example;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;

@Converter(autoApply = true)
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, String> {

    private static final DateTimeFormatter WRITE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private static final DateTimeFormatter READ_FORMATTER =
            new DateTimeFormatterBuilder()
                    .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                    .optionalStart()
                    .appendFraction(ChronoField.NANO_OF_SECOND, 1, 9, true)
                    .optionalEnd()
                    .toFormatter();

    @Override
    public String convertToDatabaseColumn(LocalDateTime locDateTime) {
        return (locDateTime == null ? null : locDateTime.format(WRITE_FORMATTER));
    }

    @Override
    public LocalDateTime convertToEntityAttribute(String sqlTimestamp) {
        if (sqlTimestamp == null) return null;
        try {
            return LocalDateTime.parse(sqlTimestamp, READ_FORMATTER);
        } catch (DateTimeParseException e) {
            // fallback pentru valori vechi fara secunde in DB
            return LocalDateTime.parse(sqlTimestamp,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        }
    }
}