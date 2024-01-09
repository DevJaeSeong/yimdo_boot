package yimdo.serverConfig.scheduling;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import yimdo.serverConfig.server.ServerConfig;

@Configuration
@EnableScheduling
public class SchedulingConfig {
	
    @Bean
    TaskScheduler taskScheduler() {
    	
    	ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
    	
    	threadPoolTaskScheduler.setPoolSize(ServerConfig.CPU_CORE_SIZE + 1);
    	threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(false);
    	threadPoolTaskScheduler.setAwaitTerminationSeconds(0);
    	threadPoolTaskScheduler.setThreadNamePrefix("SchedulTask-");
    	
        return threadPoolTaskScheduler;
    }
}
