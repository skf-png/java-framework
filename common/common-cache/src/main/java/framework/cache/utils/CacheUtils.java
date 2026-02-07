package framework.cache.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.benmanes.caffeine.cache.Cache;
import framework.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class CacheUtils {
    /**
     * 读取二级缓存
     *
     * @param redisService redis服务
     * @param key 缓存key
     * @param valueTypeRef 模板类型
     * @param caffeineCache 本地缓存服务
     * @return 缓存信息
     * @param <T> 缓存类型
     */
    //查询数据的方法
    public static <T> T getL2Cache(RedisService redisService, String key, TypeReference<T> valueTypeRef, Cache<String, Object> caffeineCache) {
        T res = (T)caffeineCache.getIfPresent(key);
        if (res != null) {
            System.out.println(key + "本地获取");
            return res;
        }
        //从二级缓存中查询数据的逻辑
        res = redisService.getCacheObject(key, valueTypeRef);
        if (res != null) {
            caffeineCache.put(key, res);
            System.out.println(key + "redis获取");
            return res;
        }
        return null;
        //从db当中进行数据查询  代码逻辑  select   mysql
    }



    //存储数据的方法
    //修饰符  返回值类型 函数名  参数列表  函数体
    //存储到一级缓存的方法
    public static <T> void setL2Cache(String key, T value, Cache<String, Object> caffeineCache) {
        caffeineCache.put(key, value);
    }


    /**
     * 设置二级缓存
     *
     * @param redisService redis服务
     * @param key 缓存key
     * @param value  缓存对象值
     * @param caffeineCache 本地缓存信息
     * @param timeout 超时时间
     * @param timeUnit 超时单位
     * @param <T> 对象类型
     */
    //存储到二级缓存和一级缓存的方法
    public static <T> void setL2Cache(RedisService redisService, String key, T value,
                                      Cache<String, Object> caffeineCache, Long timeout, TimeUnit timeUnit) {
        redisService.setCacheObject(key, value, timeout, timeUnit);
        caffeineCache.put(key, value);
    }
}
