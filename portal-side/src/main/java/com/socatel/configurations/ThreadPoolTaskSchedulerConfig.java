package com.socatel.configurations;

import com.socatel.components.Methods;
import com.socatel.utils.NextGroupStepRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;

@Configuration
public class ThreadPoolTaskSchedulerConfig {

    private Logger logger = LoggerFactory.getLogger(ThreadPoolTaskSchedulerConfig.class);

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(1000);
        threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
        return threadPoolTaskScheduler;
    }

    @Component
    public class GroupScheduler {
        private HashMap<Integer, ScheduledFuture<?>> scheduledTasks = new HashMap<>();
        private final ThreadPoolTaskScheduler threadPoolTaskScheduler;
        private final Methods methods;

        @Autowired
        public GroupScheduler(ThreadPoolTaskScheduler threadPoolTaskScheduler, Methods methods) {
            this.threadPoolTaskScheduler = threadPoolTaskScheduler;
            this.methods = methods;
        }

        /**
         * Cancel a future
         * @param id future id
         */
        public void cancelTask(Integer id) {
            logger.debug("Cancel task " + id);
            ScheduledFuture<?> future = scheduledTasks.get(id);
            if (future != null) future.cancel(true);
        }

        /**
         * Create a future to change to the next step
         * @param id future id = group_id
         * @param timestamp timestamp
         */
        public void createNewTask(Integer id, Timestamp timestamp) {
            logger.debug("Create task " + id);
            ScheduledFuture<?> future = scheduledTasks.get(id);
            if (future != null) future.cancel(true);
            future = threadPoolTaskScheduler.schedule(new NextGroupStepRunnable(methods, id), timestamp);
            scheduledTasks.put(id, future);
        }

    }
}
