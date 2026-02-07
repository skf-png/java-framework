package framework.core.templateservice.test.controller;

import framework.core.templateservice.test.service.TestThreadPoolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/t3")
@Slf4j
public class TestThreadPoolController {
    @Autowired
    private TestThreadPoolService testThreadPoolService;

    @GetMapping("/info")
    public void info() {
        log.info("controller thread-name :{}"
                ,Thread.currentThread().getName());
        testThreadPoolService.info();
    }
}
