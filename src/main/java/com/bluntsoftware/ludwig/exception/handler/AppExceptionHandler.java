package com.bluntsoftware.ludwig.exception.handler;

import com.bluntsoftware.ludwig.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;

@Slf4j
@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    // 400
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,  final HttpHeaders headers,  final HttpStatus status,  final WebRequest request) {
        log.info(ex.getClass().getName() + "-handleMethodArgumentNotValid");
        //
        final List<String> errors = new ArrayList<String>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        final AppError appError = new AppError(HttpStatus.BAD_REQUEST.value(),HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
        return handleExceptionInternal(ex, appError, headers, appError.getStatus(), request);
    }


    @Override
    protected ResponseEntity<Object> handleBindException(final BindException ex,  final HttpHeaders headers,  final HttpStatus status,  final WebRequest request) {
        log.info(ex.getClass().getName() + "-handleBindException");
        //
        final List<String> errors = new ArrayList<String>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        final AppError appError = new AppError(HttpStatus.BAD_REQUEST.value(),HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
        return handleExceptionInternal(ex, appError, headers, appError.getStatus(), request);
    }


    @Override
    protected ResponseEntity<Object> handleTypeMismatch(final TypeMismatchException ex,  final HttpHeaders headers,  final HttpStatus status,  final WebRequest request) {
        log.info(ex.getClass().getName()+ "-handleTypeMismatch");
        //
        final String error = ex.getValue() + " value for " + ex.getPropertyName() + " should be of type " + ex.getRequiredType();

        final AppError appError = new AppError(HttpStatus.BAD_REQUEST.value(),HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(),  Collections.singletonList(error));
        return new ResponseEntity<Object>(appError, new HttpHeaders(), appError.getStatus());
    }


    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(final MissingServletRequestPartException ex,  final HttpHeaders headers,  final HttpStatus status,  final WebRequest request) {
        log.info(ex.getClass().getName());
        //
        final String error = ex.getRequestPartName() + " part is missing";
        final AppError appError = new AppError(HttpStatus.BAD_REQUEST.value(),HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(),  Collections.singletonList(error));
        return new ResponseEntity<Object>(appError, new HttpHeaders(), appError.getStatus());
    }


    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(final MissingServletRequestParameterException ex,  final HttpHeaders headers,  final HttpStatus status,  final WebRequest request) {
        log.info(ex.getClass().getName());
        //
        final String error = ex.getParameterName() + " parameter is missing";
        final AppError appError = new AppError(HttpStatus.BAD_REQUEST.value(),HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), Collections.singletonList(error));
        return new ResponseEntity<Object>(appError, new HttpHeaders(), appError.getStatus());
    }

    //

    @ExceptionHandler({ MethodArgumentTypeMismatchException.class })
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(final MethodArgumentTypeMismatchException ex, final WebRequest request) {
        log.info(ex.getClass().getName());
        //
        final String error = ex.getName() + " should be of type " + Objects.requireNonNull(ex.getRequiredType()).getName();

        final AppError appError = new AppError(HttpStatus.BAD_REQUEST.value(),HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), Collections.singletonList(error));
        return new ResponseEntity<Object>(appError, new HttpHeaders(), appError.getStatus());
    }

    @ExceptionHandler({ ConstraintViolationException.class })
    public ResponseEntity<Object> handleConstraintViolation(final ConstraintViolationException ex, final WebRequest request) {
        log.info(ex.getClass().getName());
        //
        final List<String> errors = new ArrayList<String>();
        for (final ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getMessage());
        }

        final AppError appError = new AppError(HttpStatus.BAD_REQUEST.value(),HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
        return new ResponseEntity<Object>(appError, new HttpHeaders(), appError.getStatus());
    }

    // 404

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(final NoHandlerFoundException ex,  final HttpHeaders headers,  final HttpStatus status,  final WebRequest request) {
        log.info(ex.getClass().getName());
        //
        final String error = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();

        final AppError appError = new AppError(HttpStatus.NOT_FOUND.value(),HttpStatus.NOT_FOUND, ex.getLocalizedMessage(),  Collections.singletonList(error));
        return new ResponseEntity<Object>(appError, new HttpHeaders(), appError.getStatus());
    }

    // 405

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(final HttpRequestMethodNotSupportedException ex,  final HttpHeaders headers,  final HttpStatus status,  final WebRequest request) {
        log.info(ex.getClass().getName());
        //
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(" method is not supported for this request. Supported methods are ");
        Objects.requireNonNull(ex.getSupportedHttpMethods()).forEach(t -> builder.append(t).append(" "));

        final AppError appError = new AppError(HttpStatus.METHOD_NOT_ALLOWED.value(),HttpStatus.METHOD_NOT_ALLOWED, ex.getLocalizedMessage(),  Collections.singletonList(builder.toString()));
        return new ResponseEntity<Object>(appError, new HttpHeaders(), appError.getStatus());
    }

    // 415

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(final HttpMediaTypeNotSupportedException ex,  final HttpHeaders headers,  final HttpStatus status,  final WebRequest request) {
        log.info(ex.getClass().getName());
        //
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(" "));

        final AppError appError = new AppError(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex.getLocalizedMessage(), Collections.singletonList(builder.substring(0, builder.length() - 2)));
        return new ResponseEntity<Object>(appError, new HttpHeaders(), appError.getStatus());
    }

    // 500
    @ExceptionHandler({ Exception.class })
    public ResponseEntity<Object> handleAll(final Exception ex, final WebRequest request) {
        log.info(ex.getClass().getName());
        log.error("error", ex);
        //
        final AppError appError = new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(),HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage(),  Collections.singletonList("error occurred"));
        return new ResponseEntity<Object>(appError, new HttpHeaders(), appError.getStatus());
    }

    @ExceptionHandler({ AppException.class })
    public ResponseEntity<Object> handleExampleException(final AppException ex, final WebRequest request) {
        log.error("error: {}", ex.getAppError().getMessage());
        return new ResponseEntity<Object>(ex.getAppError(), new HttpHeaders(), ex.getAppError().getStatus());
    }

}
