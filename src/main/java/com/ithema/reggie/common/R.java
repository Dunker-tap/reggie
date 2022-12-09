package com.ithema.reggie.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用的返回结果类
 *
 * @param <T>
 */
@Data
public class R<T> {

    private Integer code; //编码：1成功，0和其它数字为失败

    private String msg; //错误信息，如果登录失败就会为这个字段赋值

    private T data; //数据，实体的数据，具体的存储形式局势json

    private Map map = new HashMap(); //动态数据，封装的是动态数据，使用的场合不多

    //登录成功调用的对象
    public static <T> R<T> success(T object) {
        R<T> r = new R<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    //操作失败调用的对象
    public static <T> R<T> error(String msg) {
        R r = new R();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

}
