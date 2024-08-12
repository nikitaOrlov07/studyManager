package com.example.courseservice.Config.Validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateFormatValidator.class) // The @ValidDateFormat annotation will use a validator that implements the ConstraintValidator interface. In this case, this is the DateFormatValidator class. This class contains the validation logic that will be performed when the @ValidDateFormat annotation is applied.
@Target({ ElementType.TYPE }) // The @ValidDateFormat annotation can be applied to types (classes, interfaces, enumerations) but not to fields, methods or parameters. In this case, the annotation is used to validate an entire class.
@Retention(RetentionPolicy.RUNTIME) // annotation must be available at program execution time (in runtime). This is necessary so that validation can be performed during program execution and not only at the compilation stage.
public @interface ValidDateFormat { // This is the declaration of the annotation itself. In Java, annotations are created using the @interface keyword
    String message() default "Invalid date format"; // specifies an error message if the check fails. The default value is "Invalid date format". This message will be displayed when the date format validation is broken.
    Class<?>[] groups() default {}; // This is an annotation element that can be used to group checks.
    Class<? extends Payload>[] payload() default {}; //This is an annotation element that can be used to pass additional information to the validator. For example, it can be used to pass metadata that may be useful for error handling. In most cases this element is left empty.
}