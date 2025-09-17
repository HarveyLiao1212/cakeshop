package com.harvey.cakeshop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 處理 Service 層主動丟出的錯誤（有 status code）
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getReason());
        return ResponseEntity.status(ex.getStatusCode()).body(error);
    }

    // 處理其他未捕捉的錯誤
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleOtherExceptions(Exception ex) {
        Map<String, String> error = new HashMap<>();

        // 搜尋內層錯誤，特別針對 enum 轉換錯誤
        Throwable cause = ex;
        while (cause != null) {
            if (cause instanceof IllegalArgumentException &&
                    cause.getMessage() != null &&
                    cause.getMessage().contains("No enum constant")) {

                error.put("message", "資料庫中的類型無法轉換為 enum，請確認是否有非法值。");
                error.put("details", cause.getMessage());
                return ResponseEntity.badRequest().body(error); // 回傳 400
            }
            cause = cause.getCause();
        }

        // 若不是 enum 錯誤，回傳 500 通用訊息
        error.put("message", "伺服器發生未知錯誤");
        error.put("details", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}



