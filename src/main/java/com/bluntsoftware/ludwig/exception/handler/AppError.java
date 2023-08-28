package com.bluntsoftware.ludwig.exception.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppError {
    private int code;
    private HttpStatus status;
    private String message;
    private List<String> errors;
}
