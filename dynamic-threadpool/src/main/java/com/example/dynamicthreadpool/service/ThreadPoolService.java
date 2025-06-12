// ThreadPoolService.java
package com.example.dynamicthreadpool.service;

import com.example.dynamicthreadpool.model.ThreadPoolUpdateEvent;
import com.example.dynamicthreadpool.config.ThreadPoolProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ThreadPoolService {

    @Autowired
    private ThreadPoolTaskExecutor executor;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ChannelTopic topic;

    @Autowired
    private ThreadPoolProperties properties;

    public void updateThreadPool(int corePoolSize, int maxPoolSize, int queueCapacity) {
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);

        saveConfigToRedis(corePoolSize, maxPoolSize, queueCapacity);
        publishUpdateEvent(corePoolSize, maxPoolSize, queueCapacity);
    }

    private void saveConfigToRedis(int corePoolSize, int maxPoolSize, int queueCapacity) {
        Map<String, Integer> config = new HashMap<>();
        config.put("corePoolSize", corePoolSize);
        config.put("maxPoolSize", maxPoolSize);
        config.put("queueCapacity", queueCapacity);

        redisTemplate.opsForHash().put("thread-pool-config", properties.getAppName(), config);
    }

    private void publishUpdateEvent(int corePoolSize, int maxPoolSize, int queueCapacity) {
        ThreadPoolUpdateEvent event = new ThreadPoolUpdateEvent(
                properties.getAppName(), corePoolSize, maxPoolSize, queueCapacity);
        redisTemplate.convertAndSend(topic.getTopic(), event);
    }

    public Map<String, Object> getThreadPoolInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("corePoolSize", executor.getCorePoolSize());
        info.put("maxPoolSize", executor.getMaxPoolSize());
        info.put("queueCapacity", executor.getQueueCapacity());
        info.put("activeCount", executor.getActiveCount());
        info.put("poolSize", executor.getPoolSize());
        info.put("queueSize", executor.getThreadPoolExecutor().getQueue().size());
        return info;
    }
}