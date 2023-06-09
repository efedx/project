package com.employee.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import jakarta.servlet.ServletException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
@RequiredArgsConstructor
public class HandleCustomExceptions {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(value = {TakenUserNameException.class, NoRolesException.class, NoEmployeeWithIdException.class})
    public ResponseEntity<Object> responseEntity(RuntimeException e) {

        HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        ExceptionResponse exceptionResponse = new ExceptionResponse(
                e.getMessage(),
                badRequest
        );

        return ResponseEntity
                .status(badRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .body(exceptionResponse);
    }

    @ExceptionHandler(value = {CustomSecurityException.class})
    public ResponseEntity<Object> responseEntityForSecurity(RuntimeException e) throws JsonProcessingException {

        if(!e.getMessage().isEmpty()) {
            return ResponseEntity
                    .status(400)
                    .headers(HttpHeaders.EMPTY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(e.getMessage());
        }
        else {
            return ResponseEntity
                    .status(400)
                    .headers(HttpHeaders.EMPTY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(null);
        }
    }
}