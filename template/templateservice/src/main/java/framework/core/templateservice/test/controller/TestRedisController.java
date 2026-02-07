package framework.core.templateservice.test.controller;

import framework.core.templateservice.test.pojo.Student;
import framework.domain.R;
import framework.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/t4")
@Slf4j
@RefreshScope
public class TestRedisController {
    @Autowired
    private RedisService redisService;

    @Value("${spring.data.redis.host:1}")
    private String ip;

    @RequestMapping("/as")
    public void addCache(String key,String value){
        log.info(ip);
        redisService.setCacheObject(key,value);
    }

    @RequestMapping("/ase")
    public void addStringExpire(String key,String value, Long timeout){
        redisService.setCacheObject(key,value, timeout, TimeUnit.SECONDS);
    }

    @RequestMapping("/asia")
    public void addStringIfAbsent(String key,String value){
        redisService.setCacheIfAbsent(key,value);
    }

    @RequestMapping("/ao")
    public R<Student> addStudent(String key){
        Student student = new Student("1", 1);
        redisService.setCacheObject(key,student);
        return R.success(student);
    }

    @RequestMapping("/get")
    public R<Student> get(String key){
        return R.success(redisService.getCacheObject(key, Student.class));
    }

    @RequestMapping("/je")
    public R<Long> je(String key){
        return R.success(redisService.getExpire(key));
    }

    @RequestMapping("/haskey")
    public R<Boolean> hasKey(String key){
        return R.success(redisService.hasKey(key));
    }

    @RequestMapping("/renameKey")
    public void renameKey(String oldKey,String newKey){
        redisService.renameKey(oldKey, newKey);
    }

    @RequestMapping("/deleteObject")
    public void deleteObject(String key){
        redisService.deleteObject(key);
    }

    @RequestMapping("rl")
    public R<Long> rl(String key){
        Long l = redisService.rightPushForList(key, 1, 2, 3, 4);
        return R.success(l);
    }

}
