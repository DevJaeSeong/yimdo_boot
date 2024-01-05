package yimdo.socketServer.component;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import yimdo.serverConfig.server.ServerConfig;

/**
 * 소켓통신을 통해 데이터 전송을 처리하는 클래스.
 */
@Component
@Slf4j
public class SocketServerSender {

	private Map<String, Socket> socketMap = SocketServerContext.getSocketMap();
	private Set<String> runningBreakers = SocketServerContext.getRunningBreakers();
	
	private SocketServerUtil socketServerUtil;
	
	public SocketServerSender(SocketServerUtil socketServerUtil) {
		
		this.socketServerUtil = socketServerUtil;
	}
	
	/**
	 * 차단기에 정책코드에 대응하는 개방/차단 요청 전송.
	 * 
	 * @param breakerId 차단기ID
	 * @param breakerPolicyCode 정책코드
	 * @param breakerHistoryId 차단기 상태변경 기록 식별번호
	 */
	public void sendRequestToBreaker(String breakerId, String breakerPolicyCode, int breakerHistoryId) {
		
		byte[] breakerHistoryIdtoByteArray = socketServerUtil.intToByteArray(breakerHistoryId);
		byte commandId = 0x24;
		byte paylodadLength = (byte) 0x05;
		byte[] payload = new byte[paylodadLength & 0xff];
		
		switch (breakerPolicyCode) {
		
			case ServerConfig.BREAKER_POLICY_NORMAL_OPEN:
				commandId = (byte) 0x24;
				payload[0] = (byte) 0x01;
				break;
			
			case ServerConfig.BREAKER_POLICY_NORMAL_CLOSE:
				commandId = (byte) 0x24;
				payload[0] = (byte) 0x02;
				break;
				
			case ServerConfig.BREAKER_POLICY_EMERGENCY_OPEN:
				commandId = (byte) 0x25;
				payload[0] = (byte) 0x01;
				break;
				
			case ServerConfig.BREAKER_POLICY_EMERGENCY_CLOSE:
				commandId = (byte) 0x25;
				payload[0] = (byte) 0x02;
				break;
		}
		
		for (int i = 0; i < breakerHistoryIdtoByteArray.length; i++) {
			
			payload[i + 1] = breakerHistoryIdtoByteArray[i];
		}
		
		byte[] sendData = socketServerUtil.makeByteData(breakerId, commandId, paylodadLength, payload);
		
		try {
			
			log.debug("데이터 송신: {}", socketServerUtil.printByteDataToString(sendData));
			
			Socket socket = socketMap.get(breakerId);
			OutputStream out = socket.getOutputStream();
			out.write(sendData);
			out.flush();
			
			runningBreakers.add(breakerId);
			
		} catch (Exception e) {
			
			log.error("\"{}\" 차단기에 데이터 송신 실패.", breakerId);
			e.printStackTrace();
		}
	}

	/**
	 * 해당 차단기에 연결된 소켓을 통해 Connection Request에 대한 응답을 보냄.
	 * 
	 * @param breakerId 요청 보낸 차단기ID
	 * @param socket 응답 보내는 차단기에 연결된 소켓
	 */
	public void connectionResponse(String breakerId) {
		
		byte[] payload = {(byte) 0xAA};
		byte[] sendData = socketServerUtil.makeByteData(breakerId, (byte) 0x13, (byte) 0x01, payload);
		
		try {
			
			log.debug("\"{}\" 차단기 연결 요청 응답 송신.", breakerId);
			Socket socket = socketMap.get(breakerId);
			OutputStream out = socket.getOutputStream();
			out.write(sendData);
			out.flush();
			
		} catch (IOException e) {
			
			log.error("데이터 전송중 에러 발생.");
			e.printStackTrace();
		}
	}
}
