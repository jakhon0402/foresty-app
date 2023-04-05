package uz.platform.forestyapp.controller;

import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error");
    }

    @ExceptionHandler( JwtException.class)
    public ResponseEntity<Object> handleJwtException(JwtException jex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error");
    }
}
