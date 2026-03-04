package framework.portal;

import framework.message.service.AliSmsService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class test1 {
    @Resource
    private AliSmsService aliSmsService;

    @Test
    void contextLoads() {
        aliSmsService.sendMobileCode("18333717058", "1234");
    }
}
