package com.bluntsoftware.ludwig.exception.handler;

import com.bluntsoftware.ludwig.exception.AppException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.core.MethodParameter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.AbstractBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;



import java.lang.reflect.Method;


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
    void handleApplicationExceptionTest(){
        ResponseEntity<?>  ret = appExceptionHandler.handleExampleException(new AppException(HttpStatus.BAD_REQUEST,"Test"),webRequest);
        Assertions.assertEquals(ret.getStatusCode(), HttpStatus.BAD_REQUEST);
        log.info("{}",ret);
    }

    @Test
    void handleAllExceptionsTest(){
        ResponseEntity<?>  ret = appExceptionHandler.handleAll(new AppException(HttpStatus.INTERNAL_SERVER_ERROR,"Test"),webRequest);
        Assertions.assertEquals(ret.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        log.info("{}",ret);
    }

}
