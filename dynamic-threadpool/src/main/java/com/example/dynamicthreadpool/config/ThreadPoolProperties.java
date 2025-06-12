// ThreadPoolProperties.java
package com.example.dynamicthreadpool.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "thread.pool")
public class ThreadPoolProperties {
    private int corePoolSize = 5;
    private int maxPoolSize = 10;
    private int queueCapacity = 100;
    private String threadNamePrefix = "dynamic-thread-";
    private String appName = "default-app";
}