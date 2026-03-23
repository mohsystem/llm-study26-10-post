package com.um.springbootprojstructure.dto;

import java.time.Instant;
import java.util.Map;

public class OperationResponse {
    private boolean accepted;
    private String operation;
    private String status;     // e.g. REGISTERED, LOGIN_OK, PASSWORD_CHANGED
    private String reasonCode; // for rejected: DUPLICATE_EMAIL, INVALID_CREDENTIALS, VALIDATION_ERROR, ...
    private String message;    // human-readable, stable enough for logs
    private Instant timestamp = Instant.now();
    private Map<String, Object> data; // optional payload

    public OperationResponse() {}

    public static OperationResponse accepted(String operation, String status, Map<String, Object> data) {
        OperationResponse r = new OperationResponse();
        r.accepted = true;
        r.operation = operation;
        r.status = status;
        r.data = data;
        return r;
    }

    public static OperationResponse rejected(String operation, String reasonCode, String message, Map<String, Object> data) {
        OperationResponse r = new OperationResponse();
        r.accepted = false;
        r.operation = operation;
        r.reasonCode = reasonCode;
        r.message = message;
        r.data = data;
        return r;
    }

    public boolean isAccepted() { return accepted; }
    public void setAccepted(boolean accepted) { this.accepted = accepted; }

    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReasonCode() { return reasonCode; }
    public void setReasonCode(String reasonCode) { this.reasonCode = reasonCode; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }
}
