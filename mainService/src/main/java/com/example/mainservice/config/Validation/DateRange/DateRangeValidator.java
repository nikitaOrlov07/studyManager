package com.example.mainservice.config.Validation.DateRange;
import com.example.mainservice.Dto.Homeworks.HomeworkRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, HomeworkRequest> {

    @Override
    public void initialize(ValidDateRange constraintAnnotation) {
        // Initialization code, if needed
    }

    @Override
    public boolean isValid(HomeworkRequest request, ConstraintValidatorContext context) {
        if (request.getStartDate() == null || request.getEndDate() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Start date and end date are required")
                    .addPropertyNode("startDate")
                    .addConstraintViolation();
            context.buildConstraintViolationWithTemplate("Start date and end date are required")
                    .addPropertyNode("endDate")
                    .addConstraintViolation();
            return false;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            Date startDate = sdf.parse(request.getStartDate());
            Date endDate = sdf.parse(request.getEndDate());

            if (startDate.after(endDate)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("End date must be after start date")
                        .addPropertyNode("endDate")
                        .addConstraintViolation();
                return false;
            }
            return true;
        } catch (ParseException e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Invalid date format")
                    .addPropertyNode("startDate")
                    .addConstraintViolation();
            context.buildConstraintViolationWithTemplate("Invalid date format")
                    .addPropertyNode("endDate")
                    .addConstraintViolation();
            return false;
        }
    }

}
