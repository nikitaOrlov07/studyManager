package com.example.mainservice.config.Validation.DateFormat;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


@Slf4j
public class DateFormatValidator implements ConstraintValidator<ValidDateFormat, String> {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATE_REGEX = "^\\d{4}-\\d{2}-\\d{2}$";

    private final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

    @Override
    public boolean isValid(String dateStr, ConstraintValidatorContext context) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return true; // Или false, если пустые значения должны считаться некорректными
        }
        log.debug("Validating date: {}", dateStr);

        // Проверяем, соответствует ли формат yyyy-MM-dd
        if (!dateStr.matches(DATE_REGEX)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Incorrect date format. Use yyyy-MM-dd")
                    .addConstraintViolation();
            return false;
        }

        try {
            dateFormat.setLenient(false);
            Date date = dateFormat.parse(dateStr);
            return dateFormat.format(date).equals(dateStr);
        } catch (ParseException e) {
            return false;
        }
    }
}
