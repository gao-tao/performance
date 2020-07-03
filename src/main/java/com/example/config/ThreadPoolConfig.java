package com.example.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池
 **/
@Configuration
public class ThreadPoolConfig {
    /**
     * common thread pool executor
     */
    @Bean(name = "commonThreadPool")
    public ThreadPoolExecutor commonThreadPool() {
        return new ThreadPoolExecutor(30, 50, 10, TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(1000),
                (new ThreadFactoryBuilder()).setNameFormat("commonThreadPool" + "-pool-%d").build(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

}
