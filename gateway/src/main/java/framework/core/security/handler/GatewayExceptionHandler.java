package framework.core.security.handler;
import framework.domain.R;
import framework.domain.ResultCode;
import framework.domain.ServiceException;
import framework.core.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {
    /**
     * 处理器
     *
     * @param exchange ServerWebExchange
     * @param ex 异常信息
     * @return 无
     */
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        //响应已经提交到客户端，无法再对这个响应进行常规的异常处理修改了，直接返回一个包含原始异常ex的Mono.error(ex)
        if (response.isCommitted()) {
            return Mono.error(ex);
        }
        int retCode = ResultCode.ERROR.getCode();
        String retMsg = ResultCode.ERROR.getMsg();

        if (ex instanceof NoResourceFoundException) {
            retCode = ResultCode.SERVICE_NOT_FOUND.getCode();
            retMsg = ResultCode.SERVICE_NOT_FOUND.getMsg();
        } else if (ex instanceof ServiceException) {
            retMsg = ex.getMessage();
            retCode = ((ServiceException) ex).getCode();
        }

        int httpCode = Integer.parseInt(String.valueOf(retCode).substring(0,3));

        log.error("[网关异常处理]请求路径:{},异常信息:{}", exchange.getRequest().
                getPath(), ex.getMessage());

        return webFluxResponseWriter(response, HttpStatus.valueOf(httpCode),retMsg, retCode);
    }

    private static Mono<Void> webFluxResponseWriter(ServerHttpResponse response, HttpStatus status, Object value, int code) {
        return webFluxResponseWriter(response,
                MediaType.APPLICATION_JSON_VALUE, status, value, code);
    }

    private static Mono<Void> webFluxResponseWriter(ServerHttpResponse response, String contentType,
                                                    HttpStatus status, Object value, int code) {
        response.setStatusCode(status); //设置http响应
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, contentType); //设置响应体内容类型为json
        R<?> result = R.fail(code, value.toString()); //按照约定响应数据结构，构建响应体内容
        DataBuffer dataBuffer = response.bufferFactory().wrap(JsonUtils.ObjectToString(result).getBytes());
        return response.writeWith(Mono.just(dataBuffer)); //将响应体内容写⼊响应体
    }
}
