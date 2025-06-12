// ThreadPoolMetrics.java
package com.example.dynamicthreadpool.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThreadPoolMetrics implements Serializable {
    private String appName;
    private long timestamp;
    private int corePoolSize;
    private int maxPoolSize;
    private int activeCount;
    private int poolSize;
    private int queueSize;
    private int queueCapacity;
}