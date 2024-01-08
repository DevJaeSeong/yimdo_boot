package yimdo.serverConfig.async;

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
	public ExecutorService executorService() {
		
		/* 기존 spring에서 제공하는 스레드 풀 구현체
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.initialize();
		ExecutorService executorService = (ExecutorService) threadPoolTaskExecutor;
		*/
		
		// java 21 추가된 가상스레드
		ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
		
		return executorService;
	}
}
