package com.ispirit.digitalsky.util;

import com.ispirit.digitalsky.dto.Errors;
import com.ispirit.digitalsky.exception.ValidationException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

public class CustomValidator {

    private Validator validator;

    public CustomValidator(Validator validator) {
        this.validator = validator;
    }

    public void validate(Object bean) {
        BeanPropertyBindingResult errorsCollector = new BeanPropertyBindingResult(bean, bean.getClass().getSimpleName());
        validator.validate(bean, errorsCollector);
        Errors errors = new Errors();
        for (FieldError fieldError : errorsCollector.getFieldErrors()) {
            errors.getErrors().add(String.format("%s %s", fieldError.getField(), fieldError.getDefaultMessage()));
        }
        if (!errors.getErrors().isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
