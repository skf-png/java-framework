package framework.domain;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ServiceException extends RuntimeException {
    Integer code;
    String msg;

    /**
     * 完全自定义异常
     * @param code
     * @param msg
     */
    public ServiceException(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 根据统一结果构造异常
     * @param resultCode
     */
    public ServiceException(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.msg = resultCode.getMsg();
    }

    /**
     * 默认错误异常
     * @param msg
     */
    public ServiceException(String msg) {
        this.msg = msg;
        this.code = ResultCode.ERROR.getCode();
    }
}
