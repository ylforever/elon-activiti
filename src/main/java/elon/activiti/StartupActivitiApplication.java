package elon.activiti;

import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 服务启动类
 */
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, DataSourceAutoConfiguration.class})
public class StartupActivitiApplication {

    public static void main(String[] args) {
        SpringApplication.run(StartupActivitiApplication.class);
        System.out.printf("Start up activiti application success.");
    }
}
