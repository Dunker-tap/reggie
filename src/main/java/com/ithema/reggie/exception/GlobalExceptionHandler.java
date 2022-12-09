package com.ithema.reggie.exception;

import com.ithema.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器
 */

@ControllerAdvice(annotations = {RestController.class, Controller.class})   //捕获这类控制类的方法
@ResponseBody   //将异常结果输出为json格式@
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)   //指定处理的异常的类型，这是控制台报错出现的异常
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        log.error("当前异常为 => {}", ex.getMessage());  //通过断点调试可以，当前的异常处理类是可以实现的
        //为了万无一失，还需要判断当前的异常中是否还含有这种字段 —— "Duplicate entry"
        if (ex.getMessage().contains("Duplicate entry")) {
            String[] split = ex.getMessage().split(" ");
            String msg = "用户" + split[2] + "已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }
}
