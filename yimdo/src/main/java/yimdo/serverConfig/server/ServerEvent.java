package yimdo.serverConfig.server;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import yimdo.socketServer.component.SocketServer;

@Component
@AllArgsConstructor
public class ServerEvent implements ApplicationRunner, ApplicationListener<ContextClosedEvent> {

	private final SocketServer socketServer;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		
		if (socketServer.openServerSocket()) {
			
			socketServer.acceptRequest();
			
		} else {
			
			Thread.sleep(5000);
			
			run(null);
		}
		
	}
	
	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		
	}
}
