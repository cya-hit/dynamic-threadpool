// ThreadPoolController.java
package com.example.dynamicthreadpool.controller;

import com.example.dynamicthreadpool.model.ThreadPoolMetrics;
import com.example.dynamicthreadpool.service.ThreadPoolMetricsService;
import com.example.dynamicthreadpool.service.ThreadPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/thread-pool")
public class ThreadPoolController {

    @Autowired
    private ThreadPoolService threadPoolService;

    @Autowired
    private ThreadPoolMetricsService metricsService;



    @PutMapping("/config")
    public ResponseEntity<?> updateConfig(
            @RequestParam int corePoolSize,
            @RequestParam int maxPoolSize,
            @RequestParam int queueCapacity) {
        threadPoolService.updateThreadPool(corePoolSize, maxPoolSize, queueCapacity);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getThreadPoolInfo() {
        return ResponseEntity.ok(threadPoolService.getThreadPoolInfo());
    }

    @GetMapping("/metrics")
    public ResponseEntity<List<ThreadPoolMetrics>> getAllMetrics() {
        return ResponseEntity.ok(metricsService.getAllMetrics());
    }

    @GetMapping("/suggest")
    public ResponseEntity<String> getOptimizationSuggestions() {
        Map<String, Object> info = threadPoolService.getThreadPoolInfo();
        int corePoolSize = (int) info.get("corePoolSize");
        int maxPoolSize = (int) info.get("maxPoolSize");
        int activeCount = (int) info.get("activeCount");
        int queueSize = (int) info.get("queueSize");
        int queueCapacity = (int) info.get("queueCapacity");

        StringBuilder suggestions = new StringBuilder();

        if (activeCount > corePoolSize) {
            suggestions.append("建议增加核心线程数，当前活跃线程数(")
                    .append(activeCount)
                    .append(")高于核心线程数(")
                    .append(corePoolSize)
                    .append(")\n");
        }

        double queueUsage = (double) queueSize / queueCapacity;
        if (queueUsage > 0.7) {
            suggestions.append("队列使用率过高(")
                    .append(String.format("%.2f", queueUsage * 100))
                    .append("%)，建议增加队列容量或最大线程数\n");
        }

        double threadUsage = (double) activeCount / maxPoolSize;
        if (threadUsage < 0.3) {
            suggestions.append("线程池资源利用率低(")
                    .append(String.format("%.2f", threadUsage * 100))
                    .append("%)，建议减少最大线程数\n");
        }

        return ResponseEntity.ok(suggestions.toString());
    }
}