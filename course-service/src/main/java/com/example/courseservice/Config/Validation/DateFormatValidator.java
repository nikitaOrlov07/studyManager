package com.example.courseservice.Config.Validation;

import com.example.courseservice.Dto.Homework.HomeworkRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class DateFormatValidator implements ConstraintValidator<ValidDateFormat, HomeworkRequest> {

    private static final String DATE_FORMAT = "dd-MM-yyyy";
    private static final String DATE_REGEX = "^\\d{2}-\\d{2}-\\d{4}$";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

    @Override
    public boolean isValid(HomeworkRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true;
        };
        log.error("Validating HomeworkRequest: {}", request);
        boolean startDateValid = isValidDate(request.getStartDate());
        boolean endDateValid = isValidDate(request.getEndDate());

        if (!startDateValid || !endDateValid) {
            context.disableDefaultConstraintViolation();
            if (!startDateValid) {
                context.buildConstraintViolationWithTemplate("Неверный формат даты начала. Используйте дд-мм-гггг")
                        .addPropertyNode("startDate")
                        .addConstraintViolation();
            }
            if (!endDateValid) {
                context.buildConstraintViolationWithTemplate("Неверный формат даты окончания. Используйте дд-мм-гггг")
                        .addPropertyNode("endDate")
                        .addConstraintViolation();
            }
            return false;
        }

        return true;
    }

    private boolean isValidDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return false;
        }
        log.error("Invalid date format: "+dateStr);
        // Проверка на соответствие формату дд-мм-гггг
        if (!dateStr.matches(DATE_REGEX)) {
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
