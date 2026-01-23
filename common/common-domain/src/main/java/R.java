import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class R<T> {
    private int code;
    private String msg;
    private T data;

    private static <T> R<T> construct(int code, String msg, T data) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    /**
     * 成功情况
     * @return
     * @param <T>
     */
    public static <T> R<T> success() {
        return construct(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), null);
    }
    public static <T> R<T> success(T data) {
        return construct(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), data);
    }
    public static <T> R<T> success(String msg) {
        return construct(ResultCode.SUCCESS.getCode(), msg, null);
    }
    public static <T> R<T> success(String msg, T data) {
        return construct(ResultCode.SUCCESS.getCode(), msg, data);
    }

    /**
     * 失败情况
     * @return
     * @param <T>
     */
    public static <T> R<T> fail() {
        return construct(ResultCode.ERROR.getCode(), ResultCode.ERROR.getMsg(), null);
    }
    public static <T> R<T> fail(String msg) {
        return construct(ResultCode.ERROR.getCode(), msg, null);
    }
    public static <T> R<T> fail(String msg, T data) {
        return construct(ResultCode.ERROR.getCode(), msg, data);
    }
    public static <T> R<T> fail(ResultCode resultCode) {
        return construct(resultCode.getCode(), resultCode.getMsg(), null);
    }
}
