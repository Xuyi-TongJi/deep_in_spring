package edu.seu.deep_in_spring.beanPostProcessor;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "java")
@Component
@Data
public class Bean4 {
    private String version;
}
