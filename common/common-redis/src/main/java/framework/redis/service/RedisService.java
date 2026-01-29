package framework.redis.service;

import framework.core.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

public class RedisService {
    @Autowired
    private static RedisTemplate redisTemplate;

    /**
     * 设置字符串
     * @param key 键值
     * @param value 值
     */
    public void setCacheObject(final String key, final Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置字符串键值对，并且设置存在时间
     * @param key 键值
     * @param value 值
     * @param timeout 时间长度
     * @param timeUnit 时间单位
     */
    public void setCacheObject(final String key, final Object value,long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 如果不存在插入键值对
     * @param key 键值
     * @param value 值
     * @return 插入是否成功
     */
    public Boolean setCacheIfAbsent(final String key, final Object value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * 如果不存在插入键值对，并设置存在时长
     * @param key 键值
     * @param value 值
     * @param timeout 时间长度
     * @param timeUnit 时间单位
     * @return 插入是否成功
     */
    public Boolean setCacheIfAbsent(final String key, final Object value, long timeout, TimeUnit timeUnit) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, timeout, timeUnit);
    }
    /**
     * 如果存在覆盖键值对
     * @param key 键值
     * @param value 值
     * @return 插入是否成功
     */
    public Boolean setCacheIfPresent(final String key, final Object value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }
    /**
     * 如果存在覆盖插入键值对，并设置存在时长
     * @param key 键值
     * @param value 值
     * @param timeout 时间长度
     * @param timeUnit 时间单位
     * @return 插入是否成功
     */
    public Boolean setCacheIfPresent(final String key, final Object value, long timeout, TimeUnit timeUnit) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, timeout, timeUnit);
    }

    public <T> T getCacheObject(final String key, final Class<T> clazz) {
        Object object = redisTemplate.opsForValue().get(key);
        if (object == null) {
            return null;
        }
        //将object转换成json字符串，再转换成类
        return JsonUtils.StringToObject(JsonUtils.ObjectToString(object), clazz);
    }

}
