package framework.gateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;

@Slf4j
// 核心：排除所有数据库相关自动配置
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class, // 数据源核心配置
        DataSourceTransactionManagerAutoConfiguration.class, // 数据源事务配置
        HibernateJpaAutoConfiguration.class, // JPA配置（兜底）
        // 额外：若仍报错，可补充排除MyBatis-Plus自动配置
        com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration.class,
        ValidationAutoConfiguration.class
})
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
