package com.bluntsoftware.ludwig.exception.handler;

import com.bluntsoftware.ludwig.exception.AppException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.AbstractBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.*;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Method;
import java.util.Set;

@Slf4j
class AppExceptionHandlerTest {
    ServletWebRequest webRequest;
    BindingResult bindingResult;
    MethodParameter methodParameter;
    AppExceptionHandler appExceptionHandler;
    Validator validator;
    @Data
    @AllArgsConstructor
    public static class TestClass {
        @NotNull
        String test;
        public String testMethod(String test){
            return test;
        }
    }

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        webRequest = new ServletWebRequest(new MockHttpServletRequest());
        bindingResult = new AbstractBindingResult("test") {
            @Override public Object getTarget() {return "Bad  Error";}
            @Override protected Object getActualFieldValue(String field) {return "Input field " + field;}
        };
        bindingResult.addError(new ObjectError("Test","Test Error"));
        bindingResult.rejectValue("test","505");
        Method method = TestClass.class.getMethod("testMethod",String.class);
        HandlerMethod handlerMethod = new HandlerMethod(new TestClass("Test"), method);
        MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
        methodParameter= methodParameters[0];
        appExceptionHandler  = new AppExceptionHandler();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void handleMethodArgumentNotValidTest(){
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter,bindingResult);
        ResponseEntity<?>  ret = appExceptionHandler.handleMethodArgumentNotValid(ex,HttpHeaders.EMPTY,HttpStatus.BAD_REQUEST, webRequest);
        Assertions.assertEquals(ret.getStatusCode(), HttpStatus.BAD_REQUEST);
        log.info("{}",ret);
    }

    @Test
    void handleTypeMismatchTest(){
        TypeMismatchException ex = new TypeMismatchException( "Tester",TestClass.class );
        ResponseEntity<?>  ret = appExceptionHandler.handleTypeMismatch(ex,HttpHeaders.EMPTY,HttpStatus.BAD_REQUEST, webRequest);
        Assertions.assertEquals(ret.getStatusCode(), HttpStatus.BAD_REQUEST);
        log.info("{}",ret);
    }

    @Test
    void handleBindExceptionTest(){
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter,bindingResult);
        ResponseEntity<?>  ret = appExceptionHandler.handleBindException(ex,HttpHeaders.EMPTY,HttpStatus.BAD_REQUEST, webRequest);
        Assertions.assertEquals(ret.getStatusCode(), HttpStatus.BAD_REQUEST);
        log.info("{}",ret);
    }

    @Test
    void handleMissingServletRequestPartTest(){
        MissingServletRequestPartException ex = new MissingServletRequestPartException("");
        ResponseEntity<?>  ret = appExceptionHandler.handleMissingServletRequestPart(ex,HttpHeaders.EMPTY,HttpStatus.BAD_REQUEST, webRequest);
        Assertions.assertEquals(ret.getStatusCode(), HttpStatus.BAD_REQUEST);
        log.info("{}",ret);
    }

    @Test
    void handleMissingServletRequestParameterTest(){
        MissingServletRequestParameterException ex = new MissingServletRequestParameterException("","");
        ResponseEntity<?>  ret = appExceptionHandler.handleMissingServletRequestParameter(ex,HttpHeaders.EMPTY,HttpStatus.BAD_REQUEST, webRequest);
        Assertions.assertEquals(ret.getStatusCode(), HttpStatus.BAD_REQUEST);
        log.info("{}",ret);
    }

    @Test
    void handleMethodArgumentTypeMismatchTest(){
        ResponseEntity<?>  ret = appExceptionHandler.handleMethodArgumentTypeMismatch(
                new MethodArgumentTypeMismatchException(null,String.class, "Test", methodParameter,
                        new AppException(AppError.builder().build())), webRequest);
        Assertions.assertEquals(ret.getStatusCode(), HttpStatus.BAD_REQUEST);
        log.info("{}",ret);
    }

    @Test
    void handleConstraintViolationTest() {
        Set<ConstraintViolation<TestClass>> violations = validator.validate(new TestClass(null));
        ResponseEntity<?>  ret = appExceptionHandler.handleConstraintViolation(new ConstraintViolationException( violations),  webRequest);
        Assertions.assertEquals(ret.getStatusCode(), HttpStatus.BAD_REQUEST);
        log.info("{}",ret);
    }

    @Test
    void handleNoHandlerFoundExceptionTest() {
        HttpHeaders httpHeaders = new HttpHeaders();
        ResponseEntity<?>  ret = appExceptionHandler.handleNoHandlerFoundException(new NoHandlerFoundException(HttpMethod.GET.toString(),"",new HttpHeaders()), httpHeaders,HttpStatus.BAD_REQUEST, webRequest);
        Assertions.assertEquals(ret.getStatusCode(), HttpStatus.NOT_FOUND);
        log.info("{}",ret);
    }
    @Test
    void handleApplicationExceptionTest(){
        ResponseEntity<?>  ret = appExceptionHandler.handleExampleException(new AppException(HttpStatus.BAD_REQUEST,"Test"),webRequest);
        Assertions.assertEquals(ret.getStatusCode(), HttpStatus.BAD_REQUEST);
        log.info("{}",ret);
    }

    @Test
    void handleAllExceptionsTest(){
        ResponseEntity<?>  ret = appExceptionHandler.handleAll(new AppException(HttpStatus.BAD_REQUEST,"Test"),webRequest);
        Assertions.assertEquals(ret.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        log.info("{}",ret);
    }
    @Test
    void handleHttpMediaTypeNotSupportedTest(){
        ResponseEntity<?>  ret = appExceptionHandler.handleHttpMediaTypeNotSupported(new HttpMediaTypeNotSupportedException("Test")
                , HttpHeaders.EMPTY,HttpStatus.BAD_REQUEST, webRequest);
        Assertions.assertEquals(ret.getStatusCode(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        log.info("{}",ret);
    }
    @Test
    void handleHttpRequestMethodNotSupportedTest(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization","test");
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("GET", new String[]{"PUT", "POST"},"test");
        ResponseEntity<?>  ret = appExceptionHandler.handleHttpRequestMethodNotSupported(ex, httpHeaders,HttpStatus.BAD_REQUEST, webRequest);
        Assertions.assertEquals(ret.getStatusCode(), HttpStatus.METHOD_NOT_ALLOWED);
        log.info("{}",ret);
    }
}
