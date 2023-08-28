package com.bluntsoftware.ludwig.exception;

import com.bluntsoftware.ludwig.exception.handler.AppError;
import com.bluntsoftware.ludwig.exception.type.ErrorType;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.function.Supplier;

@Data
public class AppException extends RuntimeException implements Supplier<AppException> {

    private final AppError appError;

    public AppException(AppError error){
        this.appError = error;
    }
    public AppException(ErrorType errorType){
        this.appError = errorType.error;
        this.appError.setMessage(String.format(this.appError.getMessage(),""));
    }
    public AppException(ErrorType errorType, Object... arguments){
        this.appError = errorType.error;
        this.appError.setMessage(String.format(this.appError.getMessage(), arguments));
    }
    public AppException(HttpStatus status,String message){
        this.appError = AppError.builder()
                .status(status)
                .code(status.value())
                .errors(List.of())
                .message(message).build();
    }

    public AppException(String message){
        this.appError = AppError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .code(HttpStatus.BAD_REQUEST.value())
                .errors(List.of())
                .message(message).build();
    }

    public AppException(String format, Object... arguments){
        this.appError = AppError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .code(HttpStatus.BAD_REQUEST.value())
                .errors(List.of())
                .message( String.format(format, arguments)).build();
    }

    @Override
    public AppException get() {
        return this;
    }
}
