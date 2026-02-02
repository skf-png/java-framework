package framework.redis.service;

import com.fasterxml.jackson.core.type.TypeReference;
import framework.core.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class RedisService {
    @Autowired
    private static RedisTemplate redisTemplate;

    //---------------------------------------基本操作-----------------------------------

    public Boolean expire(final String key, final long timeout) {
        return redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }


    //redis设置数据有效时间（可指定时间单位）
    public Boolean expire(final String key, final long timeout, final TimeUnit timeUnit) {
        return redisTemplate.expire(key, timeout, timeUnit);
    }

    /**
     * 获取有效时间
     *
     * @param key Redis键
     * @return 有效时间
     */
    public long getExpire(final String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 判断 key是否存在
     *
     * @param key 键
     * @return true=存在；false=不存在
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 根据提供的键模式查找 Redis 中匹配的键
     *
     * @param pattern 要查找的键的模式
     * @return 键列表
     */
    public Collection<String> keys(final String pattern) {
        return redisTemplate.keys(pattern);
    }


    /**
     * 重命名key
     *
     * @param oldKey 原来key
     * @param newKey 新key
     */
    public void renameKey(String oldKey, String newKey) {
        redisTemplate.rename(oldKey, newKey);
    }

    /**
     * 删除单个数据
     *
     * @param key 缓存的键值
     * @return 是否成功  true=删除成功；false=删除失败
     */
    public boolean deleteObject(final String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 删除多个数据
     *
     * @param collection 多个数据对应的缓存的键值
     * @return 是否删除了对象 true=删除成功；false=删除失败
     */
    public boolean deleteObject(final Collection collection) {
        return redisTemplate.delete(collection) > 0;
    }
    //---------------------------------------字符串操作-----------------------------------
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
    //---------------------------------------列表操作-----------------------------------

    /**
     * 设置新的list，也可以是尾差一个数组
     * @param key
     * @param list
     * @return 当前操作list的长度
     * @param <T>
     */
    public <T> Long setCacheList(final String key, final List<T> list) {
        return redisTemplate.opsForList().rightPushAll(key, list);
    }

    public <T> Long leftPushForListAll(final String key, final List<T> value) {
        return redisTemplate.opsForList().leftPushAll(key, value);
    }

    public <T> Long leftPushForList(final String key, T... value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    public <T> Long rightPushForListAll(final String key, final List<T> value) {
        return redisTemplate.opsForList().rightPushAll(key, value);
    }

    /**
     * 尾插，和setCacheList方法一样，用语义区分一下
     * @param key
     * @param value
     * @return
     * @param <T>
     */
    public <T> Long rightPushForList(final String key, final T... value) {
        return redisTemplate.opsForList().rightPushAll(key, value);
    }

    /**
     * 从左往右删除第一个匹配的数
     * @param key
     * @param value
     * @return
     * @param <T>
     */
    public <T> Long leftPopForList(final String key, final T value) {
        return redisTemplate.opsForList().remove(key, 1, value);
    }

    /**
     * 删除所有匹配的数值
     * @param key
     * @param value
     * @return
     * @param <T>
     */
    public <T> Long removeListAll(final String key, final T value) {
        return redisTemplate.opsForList().remove(key, 0, value);
    }

    /**
     * 自定义移除匹配的数据
     * @param key
     * @param count
     * @param value
     * @return 移除的个数
     * @param <T>
     */
    public <T> Long remove(final String key,Long count, final T value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }

    /**
     * 头删第一个数据
     * @param key
     */
    public void leftPopForList(final String key) {
        redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 尾删最后一个数据
     * @param key
     */
    public void rightPopForList(final String key) {
        redisTemplate.opsForList().rightPop(key);
    }

    /**
     * 根据下标修改
     * @param key
     * @param idx
     * @param newValue
     * @param <T>
     */
    public <T> void setIndexForList(final String key, int idx, T newValue) {
        redisTemplate.opsForList().set(key, idx, newValue);
    }

    /**
     * 获取完整list，支持简单嵌套
     * @param key
     * @param clazz
     * @return
     * @param <T>
     */
    public <T> List<T> getAllList(final String key, Class<T> clazz) {
        List list = redisTemplate.opsForList().range(key, 0, -1);
        return JsonUtils.StringToList(JsonUtils.ObjectToString(list), clazz);
    }

    /**
     * 获取完整list，支持复杂嵌套
     * @param key
     * @param typeReference
     * @return
     * @param <T>
     */
    public <T> List<T> getAllList(final String key, TypeReference<List<T>> typeReference) {
        List list = redisTemplate.opsForList().range(key, 0, -1);
        List<T> res = JsonUtils.StringToObject(JsonUtils.ObjectToString(list), typeReference);
        return res;
    }

    /**
     * 获取指定范围的list，支持简单嵌套
     * @param key
     * @param start
     * @param end
     * @param clazz
     * @return
     * @param <T>
     */
    public <T> List<T> getRangeList(final String key, final long start, final long end, final Class<T> clazz) {
        List list = redisTemplate.opsForList().range(key, start, end);
        return JsonUtils.StringToList(JsonUtils.ObjectToString(list), clazz);
    }

    /**
     * 获取指定范围的list，支持复杂嵌套
     * @param key
     * @param start
     * @param end
     * @param typeReference
     * @return
     * @param <T>
     */
    public <T> List<T> getRangeList(final String key, long start, long end, final TypeReference<List<T>> typeReference) {
        List list = redisTemplate.opsForList().range(key, start, end);
        List<T> res = JsonUtils.StringToObject(JsonUtils.ObjectToString(list), typeReference);
        return res;
    }

    /**
     * 获取list长度
     * @param key
     * @return
     */
    public Long getListSize(final String key) {
        return redisTemplate.opsForList().size(key);
    }
    //---------------------------------------set操作-----------------------------------

    /**
     * 添加set值
     * @param key
     * @param value
     */
    public void addForSet(final String key, Object... value) {
        redisTemplate.opsForSet().add(key, value);
    }

    /**
     * 从set中移除值
     * @param key
     * @param value
     */
    public void removeForSet(final String key, Object... value) {
        redisTemplate.opsForSet().remove(key, value);
    }

    /**
     * 指定set的大小
     * @param key
     * @return
     */
    public Long getSetSize(final String key) {
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 判断value是否在set里面
     * @param key
     * @param value
     * @return
     */
    public Boolean isMember(final String key, final Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 获取set，支持复杂嵌套
     * @param key
     * @param typeReference
     * @return
     * @param <T>
     */
    public <T> Set<T> getSet(final String key, TypeReference<Set<T>> typeReference) {
        Set set = redisTemplate.opsForSet().members(key);
        return JsonUtils.StringToObject(JsonUtils.ObjectToString(set), typeReference);
    }

    //---------------------------------------zset操作-----------------------------------
    /**
     * 添加元素
     * @param key key
     * @param value 值
     * @param seqNo 分数
     */
    public void addMemberZSet(String key, Object value, double seqNo) {
        redisTemplate.opsForZSet().add(key, value, seqNo);
    }

    /**
     * 删除元素
     * @param key    key
     * @param value  值
     */
    public void delMemberZSet(String key, Object value) {
        redisTemplate.opsForZSet().remove(key, value);
    }

    /**
     * 根据排序分值删除
     *
     * @param key key
     * @param minScore 最小分
     * @param maxScore 最大分
     */
    public void removeZSetByScore(final String key, double minScore, double maxScore) {
        redisTemplate.opsForZSet().removeRangeByScore(key, minScore, maxScore);
    }


    /**
     * 获取有序集合数据（支持复杂的泛型嵌套）
     *
     * @param key key信息
     * @param typeReference 类型模板
     * @return 有序集合
     * @param <T> 对象类型
     */
    public <T> Set<T> getCacheZSet(final String key, TypeReference<LinkedHashSet<T>> typeReference) {
        Set data = redisTemplate.opsForZSet().range(key, 0, -1);
        return JsonUtils.StringToObject(JsonUtils.ObjectToString(data), typeReference);
    }

    /**
     * 降序获取有序集合（支持复杂的泛型嵌套）
     * @param key key信息
     * @param typeReference 类型模板
     * @return 降序的有序集合
     * @param <T> 对象类型信息
     */
    public <T> Set<T> getCacheZSetDesc(final String key, TypeReference<LinkedHashSet<T>> typeReference) {
        Set data = redisTemplate.opsForZSet().reverseRange(key, 0, -1);

        return JsonUtils.StringToObject(JsonUtils.ObjectToString(data), typeReference);
    }

    //---------------------------------------hash操作-----------------------------------
    /**
     * 缓存Map数据
     * @param key key
     * @param dataMap map
     * @param <T> 对象类型
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap) {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

    /**
     * 往Hash中存入单个数据
     * @param key Redis键
     * @param hKey Hash键
     * @param value 值
     * @param <T> 对象类型
     */
    public <T> void setCacheMapValue(final String key, final String hKey, final T value) {
        redisTemplate.opsForHash().put(key, hKey, value);
    }

    /**
     * 删除Hash中的某条数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return 是否成功
     */
    public boolean deleteCacheMapValue(final String key, final String hKey) {
        return redisTemplate.opsForHash().delete(key, hKey) > 0;
    }

    /**
     * 获取缓存的map数据（支持复杂的泛型嵌套）
     * @param key key
     * @param typeReference 类型模板
     * @return hash对应的map
     * @param <T> 对象类型
     */
    public <T> Map<String, T> getCacheMap(final String key, TypeReference<Map<String, T>> typeReference) {
        Map data= redisTemplate.opsForHash().entries(key);
        return JsonUtils.StringToObject(JsonUtils.ObjectToString(data), typeReference);
    }

    /**
     * 获取Hash中的单个数据
     * @param key Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     * @param <T> 对象类型
     */
    public <T> T getCacheMapValue(final String key, final String hKey) {
        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(key, hKey);
    }

    /**
     * 获取Hash中的多个数据
     *
     * @param key Redis键
     * @param hKeys Hash键集合
     * @param typeReference 对象模板
     * @return 获取的多个数据的集合
     * @param <T> 对象类型
     */
    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<String> hKeys, TypeReference<List<T>> typeReference) {
        List data = redisTemplate.opsForHash().multiGet(key, hKeys);

        return JsonUtils.StringToObject(JsonUtils.ObjectToString(data), typeReference);
    }


    //******************************** LUA脚本 ***********************************
    /**
     * 删除指定值对应的 Redis 中的键值（compare and delete）
     *
     * @param key   缓存key
     * @param value value
     * @return 是否完成了比较并删除
     */
    public boolean cad(String key, String value) {
        if (key.contains(StringUtils.SPACE) || value.contains(StringUtils.SPACE)) {
            return false;
        }

        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

        // 通过lua脚本原子验证令牌和删除令牌
        Long result = (Long) redisTemplate.execute(new DefaultRedisScript<>(script, Long.class),
                Collections.singletonList(key),
                value);
        return !Objects.equals(result, 0L);
    }


}
