package utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.lang.model.type.ReferenceType;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class JsonUtils {
    private static ObjectMapper OBJECT_MAPPER;
    static {
        OBJECT_MAPPER =
                JsonMapper.builder().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                        .configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false)
                        .configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS,
                                false)
                        .configure(MapperFeature.USE_ANNOTATIONS, false)
                        .addModule(new JavaTimeModule())
                        .defaultDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")) //todo 需要统一
                        .serializationInclusion(JsonInclude.Include.NON_NULL)
                        .build();
    }

    /**
     * 对象转json
     * @param obj 传入对象
     * @return 转回后的json，失败返回null
     * @param <T>
     */
    public static <T> String ObjectToString(T obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn(e.getMessage());
            return null;
        }
    }

    /**
     * 返回美观的json格式
     * @param obj 传入对象
     * @return 美化的json格式
     * @param <T> 对象格式
     */
    public static <T> String ObjectToStringPretty(T obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return (String) obj;
        }

        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            log.warn(e.getMessage());
            return null;
        }
    }

    /**
     * json字符串转对象
     * @param str json字符串
     * @param clazz 转换对象的类
     * @return 转换后结果
     * @param <T> 转换类型
     */
    public static <T> T StringToObject(String str, Class<T> clazz) {
        if (!StringUtils.hasLength(str) ||  clazz == null) {
            return null;
        }
        if (clazz == String.class) {
            return (T) str;
        }

        try {
            return OBJECT_MAPPER.readValue(str, clazz);
        } catch (JsonProcessingException e) {
            log.warn(e.getMessage());
            return null;
        }
    }

    /**
     * json字符串转对象
     * @param str json字符串
     * @param typeReference 转换对象的类
     * @return 转换后结果
     * @param <T> 转换类型
     */
    public static <T> T StringToObject(String str, TypeReference<T> typeReference) {
        if (!StringUtils.hasLength(str) ||  typeReference == null) {
            return null;
        }

        try {
            return OBJECT_MAPPER.readValue(str, typeReference);
        } catch (JsonProcessingException e) {
            log.warn(e.getMessage());
            return null;
        }
    }
    /**
     * json字符串转列表，避免泛型擦除
     * @param str json字符串
     * @param clazz 转换对象的类
     * @return 转换后结果
     * @param <T> 转换类型
     */
    public static <T> T StringToList(String str, Class<T> clazz) {
        if (!StringUtils.hasLength(str) ||  clazz == null) {
            return null;
        }
        JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(List.class, clazz);

        try {
            return OBJECT_MAPPER.readValue(str, javaType);
        } catch (JsonProcessingException e) {
            log.warn(e.getMessage());
            return null;
        }
    }

    /**
     * json字符串转哈希表
     * @param str json字符串
     * @param clazz 转换对象的类
     * @return 转换后结果
     * @param <T> 转换类型
     */
    public static <T> T StringToMap(String str, Class<T> clazz) {
        if (!StringUtils.hasLength(str) ||  clazz == null) {
            return null;
        }
        JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructMapType(HashMap.class, String.class,clazz);

        try {
            return OBJECT_MAPPER.readValue(str, javaType);
        } catch (JsonProcessingException e) {
            log.warn(e.getMessage());
            return null;
        }
    }
}
