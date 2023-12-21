package yimdo.serverConfig.async;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 비동기 처리 설정.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

	@Bean
	public Executor executor() {
		
		ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();	// java21 추가된 가상스레드
		//ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();	// spring 멀티스레드
		
		return executorService;
	}
}
