// ThreadPoolConfigListener.java
package com.example.dynamicthreadpool.listener;

import com.example.dynamicthreadpool.model.ThreadPoolUpdateEvent;
import com.example.dynamicthreadpool.config.ThreadPoolProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class ThreadPoolConfigListener implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolConfigListener.class);

    @Autowired
    private ThreadPoolTaskExecutor executor;

    @Autowired
    private ThreadPoolProperties properties;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            ThreadPoolUpdateEvent event = new ObjectMapper()
                    .readValue(message.getBody(), ThreadPoolUpdateEvent.class);

            if (properties.getAppName().equals(event.getAppName())) {
                executor.setCorePoolSize(event.getCorePoolSize());
                executor.setMaxPoolSize(event.getMaxPoolSize());
                executor.setQueueCapacity(event.getQueueCapacity());
                logger.info("Thread pool config updated: core={}, max={}, queue={}",
                        event.getCorePoolSize(), event.getMaxPoolSize(), event.getQueueCapacity());
            }
        } catch (Exception e) {
            logger.error("Error processing thread pool update message", e);
        }
    }
}