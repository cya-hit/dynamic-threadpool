// ThreadPoolMetricsService.java
package com.example.dynamicthreadpool.service;

import com.example.dynamicthreadpool.model.ThreadPoolMetrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import com.example.dynamicthreadpool.config.ThreadPoolProperties;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ThreadPoolMetricsService {

    @Autowired
    private ThreadPoolTaskExecutor executor;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ThreadPoolProperties properties;

    private static final String METRICS_KEY = "thread-pool-metrics";

    @Scheduled(fixedRate = 10000)
    public void collectMetrics() {
        ThreadPoolMetrics metrics = new ThreadPoolMetrics();
        metrics.setAppName(properties.getAppName());
        metrics.setTimestamp(System.currentTimeMillis());
        metrics.setCorePoolSize(executor.getCorePoolSize());
        metrics.setMaxPoolSize(executor.getMaxPoolSize());
        metrics.setActiveCount(executor.getActiveCount());
        metrics.setPoolSize(executor.getPoolSize());
        metrics.setQueueSize(executor.getThreadPoolExecutor().getQueue().size());
        metrics.setQueueCapacity(executor.getQueueCapacity());

        redisTemplate.opsForHash().put(METRICS_KEY, properties.getAppName(), metrics);
        checkAndWarn(metrics);
    }

    private void checkAndWarn(ThreadPoolMetrics metrics) {
        double queueUsage = (double) metrics.getQueueSize() / metrics.getQueueCapacity();
        if (queueUsage > 0.8) {
            System.err.printf("WARNING: Queue usage is %.2f%% for app %s%n",
                    queueUsage * 100, metrics.getAppName());
        }

        double threadUsage = (double) metrics.getActiveCount() / metrics.getMaxPoolSize();
        if (threadUsage > 0.9) {
            System.err.printf("WARNING: Thread usage is %.2f%% for app %s%n",
                    threadUsage * 100, metrics.getAppName());
        }
    }

    public List<ThreadPoolMetrics> getAllMetrics() {
        List<Object> values = redisTemplate.opsForHash().values(METRICS_KEY);
        return values.stream()
                .map(obj -> (ThreadPoolMetrics) obj)
                .collect(Collectors.toList());
    }
}