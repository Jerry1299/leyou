package com.heima.common.advice;

import com.heima.common.exception.LyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @Classname BasicExceptionAdvice
 * @Description TODO
 * @Date 2019/8/13 19:10
 * @Created by YJF
 */
@ControllerAdvice
@Slf4j
public class BasicExceptionAdvice {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleException(LyException e) {
        return ResponseEntity.status(e.getStatus()).body(e.getMessage());
    }

}
