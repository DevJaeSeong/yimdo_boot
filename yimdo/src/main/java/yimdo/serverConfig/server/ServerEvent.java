package yimdo.serverConfig.server;

import java.util.concurrent.ExecutorService;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import yimdo.ftpServer.FtpServerManager;
import yimdo.socketServer.component.SocketServer;

@Component
public class ServerEvent implements ApplicationRunner, ApplicationListener<ContextClosedEvent> {
	
	private final ExecutorService executorService;
	private final SocketServer socketServer;
	private final FtpServerManager ftpServerManager;
	
	public ServerEvent(ExecutorService executorService
					   , SocketServer socketServer
					   , FtpServerManager ftpServerManager) {
		
		this.executorService = executorService;
		this.socketServer = socketServer;
		this.ftpServerManager = ftpServerManager;
	}
	
	/**
	 * 서버 시작시
	 */
	@Override
	public void run(ApplicationArguments args) throws Exception {
		
		// 소켓 서버 실행
		executorService.execute(() -> {
			
			socketServer.openServerSocket();
			socketServer.acceptRequest();
		});
		
		// FTP 서버 실행
		executorService.execute(() -> {
			
			ftpServerManager.openServer();
		});
	}
	
	/**
	 * 서버 종료시
	 */
	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		
	}
}
