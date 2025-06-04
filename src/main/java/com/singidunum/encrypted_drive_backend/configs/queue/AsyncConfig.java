package com.singidunum.encrypted_drive_backend.configs.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {
    private static final Logger logger = LoggerFactory.getLogger(AsyncConfig.class);

    @Bean(name = "mailExecutor")
    public Executor mailExecutor() {
       ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
       executor.setCorePoolSize(2);
       executor.setMaxPoolSize(4);
       executor.setQueueCapacity(100);
       executor.setThreadNamePrefix("MailExecutor-");
       executor.initialize();
        logger.info("Initialized mailExecutor with corePoolSize={}, maxPoolSize={}, queueCapacity={}",
                executor.getCorePoolSize(),
                executor.getMaxPoolSize(),
                executor.getQueueCapacity());
       return executor;
    }
}
