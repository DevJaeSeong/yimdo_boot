package yimdo.serverConfig.server;

import java.util.concurrent.Executor;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import yimdo.ftpServer.FtpServerManager;
import yimdo.socketServer.component.SocketServer;

@Component
public class ServerEvent implements ApplicationRunner, ApplicationListener<ContextClosedEvent> {
	
	private final Executor executor;
	private final TaskScheduler taskScheduler;
	private final SocketServer socketServer;
	private final FtpServerManager ftpServerManager;
	
	public ServerEvent(Executor executor
					   , TaskScheduler taskScheduler
					   , SocketServer socketServer
					   , FtpServerManager ftpServerManager) {
		
		this.executor = executor;
		this.taskScheduler = taskScheduler;
		this.socketServer = socketServer;
		this.ftpServerManager = ftpServerManager;
	}
	
	/**
	 * 서버 시작시
	 */
	@Override
	public void run(ApplicationArguments args) throws Exception {
		
		// 소켓 서버 실행
		executor.execute(() -> {
			
			socketServer.openServerSocket();
			socketServer.acceptRequest();
		});
		
		// FTP 서버 실행
		executor.execute(() -> {
			
			ftpServerManager.openServer();
		});
	}
	
	/**
	 * 서버 종료시
	 */
	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		
		if (executor instanceof ThreadPoolTaskExecutor threadPoolTaskExecutor) {
			
			threadPoolTaskExecutor.shutdown();
		}
		
		if (taskScheduler instanceof ThreadPoolTaskScheduler threadPoolTaskScheduler) {
			
			threadPoolTaskScheduler.shutdown();
		}
		
		socketServer.closeServerSocket();
		ftpServerManager.closeServer();
	}
}
