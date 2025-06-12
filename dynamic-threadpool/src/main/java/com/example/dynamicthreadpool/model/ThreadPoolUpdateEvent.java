// ThreadPoolUpdateEvent.java
package com.example.dynamicthreadpool.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThreadPoolUpdateEvent implements Serializable {
    private String appName;
    private int corePoolSize;
    private int maxPoolSize;
    private int queueCapacity;
}