package net.svab.project.controllers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Locale;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

@ControllerAdvice
public class ValidationHandler {

    private final MessageSource msgSource;

    @Autowired public ValidationHandler(MessageSource msgSource) {
        this.msgSource = msgSource;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrors processValidationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        return new ValidationErrors(result.getAllErrors().stream().map(this::extractMessage).collect(toList()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrors processMessageNotReadable(HttpMessageNotReadableException ex) {
        if (ex.getCause() instanceof JsonMappingException) {
            return new ValidationErrors(singletonList(((JsonMappingException)ex.getCause()).getOriginalMessage()));
        }
        return new ValidationErrors(singletonList(ex.getMessage()));
    }

    private String extractMessage(ObjectError error) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        return msgSource.getMessage(error.getDefaultMessage(), null, currentLocale);
    }

    static final class ValidationErrors {
        private final List<String> errors;

        @JsonCreator private ValidationErrors(@JsonProperty("errors") List<String> errors) {
            this.errors = errors;
        }

        public List<String> getErrors() { return errors; }
    }
}
