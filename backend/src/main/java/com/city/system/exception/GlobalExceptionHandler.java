package com.city.system.exception;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import java.time.Instant;
import java.util.*;
@RestControllerAdvice
public class GlobalExceptionHandler {
  record ErrorResponse(Instant timestamp,int status,String error,List<String> messages){}
  @ExceptionHandler(ApiException.class) ResponseEntity<ErrorResponse> api(ApiException e){return body(HttpStatus.BAD_REQUEST,e.getMessage());}
  @ExceptionHandler(NoSuchElementException.class) ResponseEntity<ErrorResponse> missing(NoSuchElementException e){return body(HttpStatus.NOT_FOUND,"Resource not found");}
  @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException e){List<String> m=e.getBindingResult().getFieldErrors().stream().map(x->x.getField()+": "+x.getDefaultMessage()).toList();return body(HttpStatus.BAD_REQUEST,"Validation failed",m);}
  @ExceptionHandler(MaxUploadSizeExceededException.class) ResponseEntity<ErrorResponse> upload(MaxUploadSizeExceededException e){return body(HttpStatus.BAD_REQUEST,"Image must not exceed 5 MB");}
  @ExceptionHandler(Exception.class) ResponseEntity<ErrorResponse> other(Exception e){e.printStackTrace();return body(HttpStatus.INTERNAL_SERVER_ERROR,"Unexpected server error");}
  private ResponseEntity<ErrorResponse> body(HttpStatus s,String m){return body(s,m,List.of(m));}
  private ResponseEntity<ErrorResponse> body(HttpStatus s,String m,List<String> msgs){return ResponseEntity.status(s).body(new ErrorResponse(Instant.now(),s.value(),m,msgs));}
}
