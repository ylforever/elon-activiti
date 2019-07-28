package elon.activiti;

import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 服务启动类
 */
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class StartupActivitiApplication {

    public static void main(String[] args) {
        SpringApplication.run(StartupActivitiApplication.class);
        System.out.printf("Start up activiti application success.");
    }
}
