package com.chinaunicom.edu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.core.task.TaskDecorator;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步任务配置 - 高并发优化版
 * 支持多业务场景线程池隔离，提升系统吞吐量
 */
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    /**
     * 默认异步执行器 - 通用任务
     * 用于普通异步操作，如日志记录、通知发送等
     */
    @Override
    @Primary
    @Bean(name = "taskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // CPU核心数 * 2，充分利用CPU资源
        executor.setCorePoolSize(8);
        // 最大线程数，应对突发流量
        executor.setMaxPoolSize(32);
        // 无界队列改为有界队列，防止OOM
        executor.setQueueCapacity(500);
        // 线程名前缀
        executor.setThreadNamePrefix("async-");
        // 线程空闲存活时间（秒）
        executor.setKeepAliveSeconds(60);
        // 允许核心线程超时回收
        executor.setAllowCoreThreadTimeOut(true);
        // 优雅关闭，等待任务完成
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        // 任务装饰器，传递上下文
        executor.setTaskDecorator(new ContextPropagatingTaskDecorator());
        // 拒绝策略：当线程池和队列都满时，由调用线程处理（降级保证可用性）
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 初始化
        executor.initialize();
        return executor;
    }

    /**
     * 推荐算法专用线程池
     * 用于推荐计算、协同过滤等CPU密集型任务
     */
    @Bean(name = "recommendationExecutor")
    public Executor recommendationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 推荐算法是CPU密集型，核心线程数设为CPU核心数
        executor.setCorePoolSize(4);
        // 最大线程数，防止过多线程竞争CPU
        executor.setMaxPoolSize(8);
        // 队列容量，缓冲突发请求
        executor.setQueueCapacity(200);
        // 线程名前缀
        executor.setThreadNamePrefix("recommend-");
        // 线程空闲存活时间
        executor.setKeepAliveSeconds(120);
        // 允许核心线程超时
        executor.setAllowCoreThreadTimeOut(true);
        // 优雅关闭
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(120);
        // 传递上下文
        executor.setTaskDecorator(new ContextPropagatingTaskDecorator());
        // 拒绝策略：丢弃最老的任务，保证新任务执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        executor.initialize();
        return executor;
    }

    /**
     * 数据处理专用线程池
     * 用于批量数据处理、报表生成等IO密集型任务
     */
    @Bean(name = "dataProcessingExecutor")
    public Executor dataProcessingExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // IO密集型任务，可以设置更多线程
        executor.setCorePoolSize(16);
        executor.setMaxPoolSize(64);
        // 较大的队列容量
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("data-process-");
        executor.setKeepAliveSeconds(300);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(300);
        executor.setTaskDecorator(new ContextPropagatingTaskDecorator());
        // 拒绝策略：抛出异常，让调用方感知
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.initialize();
        return executor;
    }

    /**
     * 定时任务专用线程池
     * 用于定时清理、数据同步等任务
     */
    @Bean(name = "scheduledExecutor")
    public Executor scheduledExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("scheduled-");
        executor.setKeepAliveSeconds(60);
        executor.setAllowCoreThreadTimeOut(false);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.setTaskDecorator(new ContextPropagatingTaskDecorator());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    /**
     * 高优先级任务线程池
     * 用于实时性要求高的任务，如用户交互响应
     */
    @Bean(name = "highPriorityExecutor")
    public Executor highPriorityExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(16);
        // 小队列，快速失败或扩容
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("high-priority-");
        executor.setKeepAliveSeconds(30);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.setTaskDecorator(new ContextPropagatingTaskDecorator());
        // 拒绝策略：直接运行，不排队
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, params) -> {
            System.err.println("Async task failed - Method: " + method.getName());
            System.err.println("Exception: " + throwable.getMessage());
            throwable.printStackTrace();
        };
    }

    /**
     * 上下文传递装饰器
     * 用于在异步线程中传递请求上下文（如用户认证信息）
     * 支持非Web环境（如定时任务）
     */
    public static class ContextPropagatingTaskDecorator implements TaskDecorator {
        @Override
        public Runnable decorate(Runnable runnable) {
            // 尝试获取请求上下文，如果在非Web环境（如定时任务）则为null
            RequestAttributes context = null;
            try {
                context = RequestContextHolder.currentRequestAttributes();
            } catch (IllegalStateException e) {
                // 非Web环境，没有请求上下文，这是正常的
            }
            
            final RequestAttributes requestContext = context;
            return () -> {
                try {
                    if (requestContext != null) {
                        RequestContextHolder.setRequestAttributes(requestContext);
                    }
                    runnable.run();
                } finally {
                    if (requestContext != null) {
                        RequestContextHolder.resetRequestAttributes();
                    }
                }
            };
        }
    }
}
