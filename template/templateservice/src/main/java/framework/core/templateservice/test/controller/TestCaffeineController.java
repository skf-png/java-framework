package framework.core.templateservice.test.controller;
import com.github.benmanes.caffeine.cache.Cache;

import com.fasterxml.jackson.core.type.TypeReference;
import framework.cache.utils.CacheUtils;
import framework.core.templateservice.test.pojo.Student;
import framework.redis.service.RedisService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/t5")
public class TestCaffeineController {
    @Resource
    private Cache<String, Object> caffeineCache;

    @Resource
    private RedisService redisService;

    @RequestMapping("/getS")
    public Student getStudent(String key) {
        Student student = CacheUtils.getL2Cache(redisService, key, new TypeReference<Student>() {
        }, caffeineCache);

        if (student != null) {
            return student;
        }
        student = new Student("1", 2);
        CacheUtils.setL2Cache(redisService, key, student, caffeineCache, 10L, TimeUnit.MINUTES);
        System.out.println(key + "数据库获取");
        return student;
    }


}
