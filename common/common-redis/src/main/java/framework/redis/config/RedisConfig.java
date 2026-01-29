package framework.redis.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import framework.domain.constants.CommonConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.management.GarbageCollectorMXBean;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //设置key
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        //设置value
        GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = createJackson2JsonRedisSerializer();
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        return redisTemplate;
    }

    private GenericJackson2JsonRedisSerializer createJackson2JsonRedisSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper =
                JsonMapper.builder().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                        .configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false)
                        .configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS,
                                false)
                        .configure(MapperFeature.USE_ANNOTATIONS, false)
                        .addModule(new JavaTimeModule())
                        .addModule(new SimpleModule()
                                .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(CommonConstants.STANDARD_FORMAT)))
                                .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(CommonConstants.STANDARD_FORMAT))))
                        .defaultDateFormat(new SimpleDateFormat(CommonConstants.STANDARD_FORMAT)) //todo 需要统一
                        .serializationInclusion(JsonInclude.Include.NON_NULL)
                        .build();
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }
}
