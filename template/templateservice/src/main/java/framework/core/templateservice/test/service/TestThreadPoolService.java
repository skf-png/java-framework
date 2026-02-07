package framework.core.templateservice.test.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TestThreadPoolService {
    @Async("threadPoolTaskExecutor")
    public void info() {
        log.info("service thread-name :{}" ,Thread.currentThread().getName());
    }
}
