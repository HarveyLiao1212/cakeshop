package com.harvey.cakeshop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 處理 DTO 驗證錯誤（例如 @NotNull, @NotBlank 失敗）
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String field = error.getField();
            String message = error.getDefaultMessage();
            fieldErrors.put(field, message);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("message", "欄位驗證錯誤");
        response.put("errors", fieldErrors);

        return ResponseEntity.badRequest().body(response);
    }

    // 處理 Service 層主動丟出的錯誤（有 status code）
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getReason());
        return ResponseEntity.status(ex.getStatusCode()).body(error);
    }

    // 處理其他未捕捉的錯誤（包含 enum 錯誤檢查）
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleOtherExceptions(Exception ex) {
        Map<String, String> error = new HashMap<>();

        // 檢查是否是 enum 轉換錯誤
        Throwable cause = ex;
        while (cause != null) {
            if (cause instanceof IllegalArgumentException &&
                    cause.getMessage() != null &&
                    cause.getMessage().contains("No enum constant")) {

                error.put("message", "資料庫中的類型無法轉換為 enum，請確認是否有非法值。");
                error.put("details", cause.getMessage());
                return ResponseEntity.badRequest().body(error);
            }
            cause = cause.getCause();
        }

        // 其他錯誤
        error.put("message", "伺服器發生未知錯誤");
        error.put("details", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}




