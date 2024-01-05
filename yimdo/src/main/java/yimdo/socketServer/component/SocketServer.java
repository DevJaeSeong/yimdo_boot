package yimdo.socketServer.component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import yimdo.serverConfig.server.ServerConfig;

/**
 * 차단기와 소켓 통신을 위한 클래스.
 */
@Component
@Slf4j
public class SocketServer {
	
	private ServerSocket serverSocket;
	private final Map<String, Socket> socketMap = SocketServerContext.getSocketMap();
	private final Set<String> runningBreakers = SocketServerContext.getRunningBreakers();
	
	private Executor executor;
	private SocketServerUtil socketServerUtil;
	private SocketServerReceiver socketServerReceiver;
	
	public SocketServer(Executor executor,
						SocketServerUtil socketServerUtil,
						SocketServerReceiver socketServerReceiver) {
		
		this.executor = executor;
		this.socketServerUtil = socketServerUtil;
		this.socketServerReceiver = socketServerReceiver;
	}
	
	/**
	 * 서버 소켓을 개방합니다.
	 * 
	 * @return 성공시 true<br>
	 * 		   실패시 false
	 */
    public boolean openServerSocket() {
    	
    	try {
    		
    		log.debug("\"{}\"포트로 서버 소켓 개방.", ServerConfig.SOCKET_PORT);
    		this.serverSocket = new ServerSocket(ServerConfig.SOCKET_PORT);
    		return true;
    		
		} catch (Exception e) {
			
			log.error("\"{}\"포트로 서버소켓 개방중 오류발생.", ServerConfig.SOCKET_PORT);
			e.printStackTrace();
			
			try {
				
				serverSocket.close();
				
			} catch (IOException e1) {
				
				log.error("서버 소켓 종료중 에러 발생.");
				e1.printStackTrace();
			}
			
			return false;
		}
    }
	
    /**
     * 소켓 통신 요청을 수신.
     */
    public void acceptRequest() {
    	
		while (!serverSocket.isClosed()) { 
			
			Socket socket = null;
			
			try {
				
				socket = serverSocket.accept();
				log.debug("연결된 socket: {}", socket);
				
				executor.execute(new CustomRunnable(socket)); 
				
			} catch (Exception e) {
				
				if (socket != null) {
					
					try {
						
						socket.close();
						
					} catch (IOException e1) {
						
						log.error("소켓 종료중 에러 발생.");
						e1.printStackTrace();
					}
				}
				
				
				if (Thread.interrupted()) { log.debug("해당 스레드가 block 상태에서 중지 요청이 들어왔습니다."); } 
				else 					  { log.error("에러 발생"); e.printStackTrace(); }
			}
		}
    }
    
	/**
	 * {@link TaskExecutor}의 작업내용을 정의한 {@link Runnable}의 구현체.
	 */
	private class CustomRunnable implements Runnable {

		private Socket socket;
		
		public CustomRunnable(Socket socket) {
			
			this.socket = socket;
		}
		
		@Override
		public void run() {
			log.debug("run() 시작");
			
			String breakerId = null;
			
			try {
				
				InputStream inputStream = socket.getInputStream();
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				
				byte[] buffer = new byte[1024];	// 수신 데이터량에 맞게 크기 조정
				int bufferLen = 0;
				
				/*
				 * read() 메소드는 데이터가 들어올때까지 blocking 되어이있다가 데이터가 들어오면 읽기 로직 수행.
				 * 연결이 종료되어 더이상 읽을 데이터가 없다면 -1을 반환.
				 * 데이터를 보내지 않아도 연결되어있다면 -1을 반환하는게 아니라 blocking 되어 작업 대기.
				 */
				while ((bufferLen = inputStream.read(buffer)) != -1) {
					
					byteArrayOutputStream.reset();
					byteArrayOutputStream.write(buffer, 0, bufferLen);
					
					byte[] data = byteArrayOutputStream.toByteArray();
					breakerId = socketServerUtil.hexDataToString((byte) data[1], (byte) data[2]);
					
					log.debug("데이터 수신: {}", socketServerUtil.printByteDataToString(data));
					
                	if (!socketMap.containsKey(breakerId)) {
                		
                		socketMap.put(breakerId, socket);
                		log.debug("\"{}\" 차단기에 연결된 소켓 socketMap에 추가 => socketMap: {}", breakerId, socketMap);
                	}
                	
                	if (socketServerUtil.validateData(data))
                		socketServerReceiver.processReceivedData(data, breakerId);
				}
			
			} catch (Exception e) {

				log.error("소켓 처리 중 에러발생");
				e.printStackTrace();
				
			} finally {
				
				try {
					
					socket.close();
					log.debug("\"{}\" 차단기에 연결된 소켓 연결 종료.", breakerId);
					
				} catch (Exception e) {
					
					log.error("소켓 닫기 중 에러발생");
					e.printStackTrace();
				}
				
				if (breakerId != null) {
					
					if (socketMap.containsKey(breakerId)) {
						
						socketMap.remove(breakerId);
						log.debug("\"{}\" 차단기에 연결된 소켓 socketMap에 제거 => socketMap: {}", breakerId, socketMap);
					}
					
					if (runningBreakers.contains(breakerId)) {
						
						runningBreakers.remove(breakerId);
						log.debug("\"{}\" 차단기 runningBreakers에서 제거 => runningBreakers: {}", breakerId, runningBreakers);
					}
				}
			}
			
			log.debug("run() 끝");
		}
	}
	
	/**
	 * 서버 소켓을 종료.
	 */
	public void closeServerSocket() {
		
		if (serverSocket == null) {
			
			log.debug("연결된 서버 소켓이 없습니다.");
			return;
		}
		
		if (serverSocket.isClosed()) {
			
			log.debug("서버 소켓이 이미 종료되었습니다.");
			return;
		}
		
		try {
			
			serverSocket.close();
			log.debug("서버 소켓 연결 종료.");
			
		} catch (Exception e) {
			
			log.error("서버 소켓 종료 실패.");
			e.printStackTrace();
		}
	}
}
