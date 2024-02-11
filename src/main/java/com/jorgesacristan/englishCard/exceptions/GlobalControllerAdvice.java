package com.jorgesacristan.englishCard.exceptions;
import com.jorgesacristan.englishCard.dtos.ErrorDetailsDto;
import com.jorgesacristan.englishCard.response.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.jorgesacristan.englishCard.response.StandardResponse;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalControllerAdvice extends ResponseEntityExceptionHandler {

    @Autowired
    private ResponseBuilder responseBuilder;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        final List<ErrorDetailsDto> details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> {
                    return new ErrorDetailsDto(error.getCode(), error.getField(), error.getDefaultMessage());
                }).collect(Collectors.toList());


        final StandardResponse err = new StandardResponse(
                String.valueOf(status.value()),
                status.toString(),
                "Validation Error",
                Instant.now(),"",
                details );
        return new ResponseEntity<>(err, status);
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<?> handleBaseException (BaseException e){

        if(e.getCode().equals(HttpStatus.NOT_FOUND.toString())){
            return responseBuilder.buildResponse(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    null
            );
        }

        if(e.getCode().equals(HttpStatus.UNAUTHORIZED.toString())){
            return responseBuilder.buildResponse(
                    HttpStatus.UNAUTHORIZED.value(),
                    e.getMessage(),
                    null
            );
        }

        if (e.getCode().equals(HttpStatus.BAD_REQUEST.toString())){
            return responseBuilder.buildResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage(),
                    null
            );
        }

        if (e.getCode().equals(HttpStatus.NO_CONTENT.toString())){
            return responseBuilder.buildResponse(
                    HttpStatus.NO_CONTENT.value(),
                    e.getMessage(),
                    null
            );
        }

        return responseBuilder.buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e.getMessage(),
                null
        );
    }
/*
    @ExceptionHandler({ AccessDeniedException.class })
    public ResponseEntity<Object> handleAccessDeniedException(
            Exception ex, WebRequest request) {
        return new ResponseEntity<Object>(
                "Access denied", new HttpHeaders(), HttpStatus.
                FORBIDDEN);
    }
 */


/*
    @ExceptionHandler(NewUserWithDifferentPasswordsException.class)
    public ResponseEntity<ApiError> handleNewUserErrors(Exception ex) {
        return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

*/

/*
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatus status, WebRequest request) {
        ApiError  apiError = new ApiError(status, ex.getMessage());
        return ResponseEntity.status(status).headers(headers).body(apiError);
    }
*/

    private ResponseEntity<ApiError> buildErrorResponseEntity(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(ApiError.builder()
                        .estado(status)
                        .mensaje(message)
                        .build());

    }


}
