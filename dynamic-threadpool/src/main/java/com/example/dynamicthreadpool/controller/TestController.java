package com.example.dynamicthreadpool.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private ThreadPoolTaskExecutor executor;

    private final AtomicInteger taskCounter = new AtomicInteger(0);

    @GetMapping("/submit")
    public ResponseEntity<String> submitTask() {
        int taskNumber = taskCounter.incrementAndGet();

        executor.execute(() -> {
            long startTime = System.currentTimeMillis();
            System.out.println("Task #" + taskNumber + " started by " + Thread.currentThread().getName());

            try {
                // 模拟任务执行时间（1-3秒随机）
                Thread.sleep(1000 + (long)(Math.random() * 2000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            long duration = System.currentTimeMillis() - startTime;
            System.out.println("Task #" + taskNumber + " completed in " + duration + "ms");
        });

        return ResponseEntity.ok("Task #" + taskNumber + " submitted to thread pool");
    }

    @GetMapping("/submit-batch")
    public ResponseEntity<String> submitBatchTasks(@RequestParam(defaultValue = "5") int count) {
        for (int i = 0; i < count; i++) {
            submitTask();
        }
        return ResponseEntity.ok("Submitted " + count + " tasks to thread pool");
    }
}