package com.um.springbootprojstructure.config;

import com.um.springbootprojstructure.dto.OperationResponse;
import com.um.springbootprojstructure.service.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger promptLogger = LoggerFactory.getLogger("USER_PROMPT_LOGGER");

    private static String opNameFromPath(String path) {
        // deterministic enough; you can hardcode per controller later if preferred
        return path;
    }

    @ExceptionHandler(DuplicateAccountException.class)
    public ResponseEntity<OperationResponse> handleDuplicate(DuplicateAccountException ex,
                                                            jakarta.servlet.http.HttpServletRequest req) {
        String op = opNameFromPath(req.getRequestURI());
        promptLogger.info("REJECT op={} reason={} msg={}", op, ex.getReasonCode(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(OperationResponse.rejected(op, ex.getReasonCode(), ex.getMessage(), null));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<OperationResponse> handleUnauthorized(UnauthorizedException ex,
                                                               jakarta.servlet.http.HttpServletRequest req) {
        String op = opNameFromPath(req.getRequestURI());
        promptLogger.info("REJECT op={} reason={} msg={}", op, ex.getReasonCode(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(OperationResponse.rejected(op, ex.getReasonCode(), ex.getMessage(), null));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<OperationResponse> handleNotFound(NotFoundException ex,
                                                           jakarta.servlet.http.HttpServletRequest req) {
        String op = opNameFromPath(req.getRequestURI());
        promptLogger.info("REJECT op={} reason={} msg={}", op, ex.getReasonCode(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(OperationResponse.rejected(op, ex.getReasonCode(), ex.getMessage(), null));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<OperationResponse> handleBadRequest(BadRequestException ex,
                                                             jakarta.servlet.http.HttpServletRequest req) {
        String op = opNameFromPath(req.getRequestURI());
        promptLogger.info("REJECT op={} reason={} msg={}", op, ex.getReasonCode(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(OperationResponse.rejected(op, ex.getReasonCode(), ex.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<OperationResponse> handleValidation(MethodArgumentNotValidException ex,
                                                             jakarta.servlet.http.HttpServletRequest req) {
        String op = opNameFromPath(req.getRequestURI());

        Map<String, Object> details = new LinkedHashMap<>();
        details.put("fieldErrors", ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.of(
                        "field", fe.getField(),
                        "code", fe.getCode(),
                        "message", fe.getDefaultMessage()
                ))
                .collect(Collectors.toList()));

        promptLogger.info("REJECT op={} reason=VALIDATION_ERROR details={}", op, details);

        return ResponseEntity.badRequest()
                .body(OperationResponse.rejected(op, "VALIDATION_ERROR", "Request validation failed", details));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<OperationResponse> handleGeneric(Exception ex,
                                                          jakarta.servlet.http.HttpServletRequest req) {
        String op = opNameFromPath(req.getRequestURI());
        // avoid leaking stack traces to clients
        promptLogger.error("ERROR op={} msg={}", op, ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(OperationResponse.rejected(op, "INTERNAL_ERROR", "Unexpected error", null));
    }
}
