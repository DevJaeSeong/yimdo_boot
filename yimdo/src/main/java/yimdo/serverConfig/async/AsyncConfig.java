package yimdo.serverConfig.async;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.extern.slf4j.Slf4j;
import yimdo.serverConfig.server.ServerConfig;

/**
 * 비동기 처리 설정.
 */
@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig implements AsyncConfigurer {

	@Override
	@Bean
	public Executor getAsyncExecutor() {
		
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		
		threadPoolTaskExecutor.setCorePoolSize(ServerConfig.CPU_CORE_SIZE);
		threadPoolTaskExecutor.setMaxPoolSize(ServerConfig.CPU_CORE_SIZE + 1);
		threadPoolTaskExecutor.setQueueCapacity(100);
		threadPoolTaskExecutor.setThreadNamePrefix("AsyncTask-");
		
		return threadPoolTaskExecutor;
	}
	
	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		
		AsyncUncaughtExceptionHandler asyncUncaughtExceptionHandler = new AsyncUncaughtExceptionHandler() {

			@Override
			public void handleUncaughtException(Throwable ex, Method method, Object... params) {
				
				log.error("\"{}\" 메서드 비동기 처리도중 에러 발생.", method.getName());
				
				StringBuilder sb = new StringBuilder("[");
				for (Object param : params) {
					
					sb.append(param);
					sb.append(", ");
				}
				if (sb.length() > 2) sb.setLength(sb.length() - 2);
				sb.append("]");
				
				log.error("에러를 발생시킨 매개변수: {}", sb.toString());
			}
		};

		return asyncUncaughtExceptionHandler;
	}
}
