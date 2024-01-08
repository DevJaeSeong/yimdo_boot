package yimdo.socketServer.component;

import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import yimdo.serverConfig.server.ServerConfig;
import yimdo.socketServer.mapper.SocketServerMapper;
import yimdo.socketServer.vo.BreakerControllerVo;
import yimdo.socketServer.vo.SocketServerVo;
import yimdo.web.admin.weatherData.vo.WeatherDataVo;

/**
 * 소켓통신을 통해 받은 데이터를 처리하는 클래스.
 */
@Component
@Slf4j
public class SocketServerReceiver {

	private Map<String, Socket> socketMap = SocketServerContext.getSocketMap();
	private Set<String> runningBreakers = SocketServerContext.getRunningBreakers();
	private Set<String> carDetectionBreakers = SocketServerContext.getCarDetectionBreakers();
	private Set<String> carDetectionOpenedBreakers = SocketServerContext.getCarDetectionbopenedbreakers();
	
	private SocketServerMapper socketServerMapper;
	private SocketServerUtil socketServerUtil;
	private BreakerController breakerController;
	private ExecutorService executorService;
	private SocketServerSender socketServerSender;
	
	public SocketServerReceiver(SocketServerMapper socketServerMapper
								, SocketServerUtil socketServerUtil
								, BreakerController breakerController
								, ExecutorService executorService
								, SocketServerSender socketServerSender) {
		
		this.socketServerMapper = socketServerMapper;
		this.socketServerUtil = socketServerUtil;
		this.breakerController = breakerController;
		this.executorService = executorService;
		this.socketServerSender = socketServerSender;
	}
	
	/**
     * 받은 byte[] 데이터를 파싱하여 Command ID값에 따라 분기를 나눠 데이터 처리.
     * @param data 받은 데이터
     */
    protected void processReceivedData(byte[] data, String breakerId) {
		
    	int payloadLength = (byte) data[5] & 0xff;
    	byte[] payload = socketServerUtil.getPayload(data, payloadLength);
    	byte commandID = (byte) data[4];
    	
    	switch (commandID) {
    	
			case 0x23: // 차단기 연결요청
				connectionRequest(breakerId);
				break;
			
			case 0x21: // 차단기 상태보고
				presenceReport(breakerId, payload);
				break;
			
			case 0x14: // 정상요청 응답
				normalBreakerResponse(breakerId, payload);
				break;
				
			case 0x15: // 강제요청 응답
				emergencyBreakerResponse(breakerId, payload);
				break;
				
			case 0x27: // 차단기 차량 감지
				carDetectionEventReport(breakerId, payload);
				break;
				
			case 0x28: // 차단기 사람 감지
				log.debug("사람 감지");
				break;
				
			case 0x29: // 스마트폰이 비컨 감지시 서버에 인증요청
				log.debug("비콘 감지");
				break;
				
			case 0x30: // 차단기에서 수집된 기상정보
				weatherReport(breakerId, payload);
				break;
				
			default:
				log.warn("정의되지않은 commandID");
				break;
		}
    	
	}

    /**
     * 차단기가 서버와 연결시 최초로 보내는 요청.
     * 
     * @param breakerId 요청을 보내는 차단기ID
     */
	public void connectionRequest(String breakerId) {
		log.debug("\"{}\" 차단기 연결 요청 수신.", breakerId);
		
		Socket socket = socketMap.get(breakerId);
		SocketServerVo socketServerVo = new SocketServerVo(breakerId, socket.getInetAddress().getHostAddress());
		
		try {
			
			socketServerMapper.insertBreakerInfo(socketServerVo);
			
		} catch (Exception e) {

			log.error("차단기 정보 갱신중 에러 발생.");
			e.printStackTrace();
		}
		
		socketServerSender.connectionResponse(breakerId);
	}
	
	/**
	 * 주기적으로 보고되는 차단기의 상태를 처리.
	 * 
	 * @param breakerId
	 * @param payload
	 */
	public void presenceReport(String breakerId, byte[] payload) {
		
    	String breakerStatus = "05";
    	
    	switch (payload[0]) {
    	
			case 0x01: breakerStatus = "01"; break;
			case 0x02: breakerStatus = "02"; break;
			case 0x03: breakerStatus = "03"; break;
			case 0x04: breakerStatus = "04"; break;
			case 0x05: breakerStatus = "05"; break;
			case 0x06: breakerStatus = "06"; break;
		}
    	
    	String breakerPolicyCode = "";
    	
    	// 차단기 상태 기록 및 업데이트
    	try {
    		
    		Map<String, String> map = new HashMap<String, String>();
    		map.put("breakerId", breakerId);
    		map.put("breakerStatus", breakerStatus);
    		
    		socketServerMapper.insertPresenceReport(map);
    		socketServerMapper.updateBreakerStatus(map);
    		
    		breakerPolicyCode = socketServerMapper.selectBreakerPolicy(breakerId);
    		
		} catch (Exception e) {
			
			log.error("DB통신 에러");
			e.printStackTrace();
			return;
		}
    	
    	// 차단기 상태가 "06" 인 경우
    	if ("06".equals(breakerStatus)) {
    		
    		carDetectionOpenedBreakers.add(breakerId);
    		breakerStatus = "01";
    		
    	} else {
    		
    		// 차단기 개방상태에서 차량감지중 + 차량감지 신호를 더이상 보내지 않는 상태
    		if (carDetectionOpenedBreakers.contains(breakerId) && !carDetectionBreakers.contains(breakerId)) {
    			
    			carDetectionOpenedBreakers.remove(breakerId);
    			
    			try {
					
    				socketServerMapper.updateBreakerPolicy(Map.of("breakerId", breakerId, "breakerPolicyCode", ServerConfig.BREAKER_POLICY_NORMAL_CLOSE));
    				
				} catch (Exception e) {
					
					log.error("DB 에러.");
					e.printStackTrace();
				}
    			
        		BreakerControllerVo breakerControllerVo = new BreakerControllerVo(breakerId, 
        																		  ServerConfig.BREAKER_POLICY_NORMAL_CLOSE, 
																				  ServerConfig.SYSTEM_MODIFIER, 
																				  ServerConfig.SYSTEM_ELEMENT_CODE, 
																				  "차량이 더이상 감지되지 않아 차단기 차단요청.");
				
				breakerController.breakerRequest(breakerControllerVo);
    			
    			return;
    		}
    	}
    	
    	// 차단기 상태와 정책이 일치하지 않을 경우
    	if (!isMatchPolicyAndStatus(breakerPolicyCode, breakerStatus, breakerId)) {
    		
    		log.debug("\"{}\" 차단기의 정책과 상태가 일치하지않음. => breakerPolicyCode: {}, breakerStatus: {}", breakerId, breakerPolicyCode, breakerStatus);
    		
    		BreakerControllerVo breakerControllerVo = new BreakerControllerVo(breakerId, 
    																		  breakerPolicyCode, 
    								  										  ServerConfig.SYSTEM_MODIFIER, 
    								  										  ServerConfig.SYSTEM_ELEMENT_CODE, 
    								  										  "차단기의 정책과 상태 불일치로 인한 재요청.");
    		
    		breakerController.breakerRequest(breakerControllerVo);
    	}
	}
	
	/**
	 * 차단기 상태와 차단기 정책이 일치하는지 확인하는 메서드.
	 * 
	 * @param breakerPolicyCode 차단기 정책
	 * @param breakerStatus 차단기 상태
	 * @return 상태와 정책 일치하면 true, 일치하지 않으면 false
	 */
	private boolean isMatchPolicyAndStatus(String breakerPolicyCode, String breakerStatus, String breakerId) {
		
		boolean isMatch = false;
		
		switch (breakerStatus) {
		
			case "01":
				isMatch = breakerPolicyCode.equals(ServerConfig.BREAKER_POLICY_NORMAL_OPEN);
				break;
				
			case "02":
				isMatch = breakerPolicyCode.equals(ServerConfig.BREAKER_POLICY_NORMAL_CLOSE);
				break;
				
			case "03":
				isMatch = breakerPolicyCode.equals(ServerConfig.BREAKER_POLICY_EMERGENCY_OPEN);
				break;
				
			case "04":
				isMatch = breakerPolicyCode.equals(ServerConfig.BREAKER_POLICY_EMERGENCY_CLOSE);
				break;
				
			case "05":
				log.debug("\"{}\" 차단기로부터 상태를 오류상태로 보고받음. => breakerStatus: {}", breakerId, breakerStatus);
				isMatch = true;
				break;
				
			case "06":
				log.debug("\"{}\" 차단기로부터 차량이 감지되는 도중 정상개방중인 상태를 보고받음. => breakerStatus: {}", breakerId, breakerStatus);
				isMatch = true;
				break;
		}

		return isMatch;
	}
	
	public void normalBreakerResponse(String breakerId, byte[] payload) {
		
		if (runningBreakers.contains(breakerId))
    		runningBreakers.remove(breakerId);
		
		commonProcessing(payload);
	}
	
	public void emergencyBreakerResponse(String breakerId, byte[] payload) {
		
		if (runningBreakers.contains(breakerId))
    		runningBreakers.remove(breakerId);
		
		commonProcessing(payload);
	}
	
	private void commonProcessing(byte[] payload) {
		
		byte[] breakerHistoryIdByteArray = new byte[4];
		
		for (int i = 0; i < 4; i++) { 
			breakerHistoryIdByteArray[i] = payload[(payload.length + i) - 4]; 
		}
		
		String dateTime = "";
		
		for (int i = 0; i < payload.length - 4; i++) { 
			dateTime += (char) payload[i + 1]; 
		}
		
		Map<String, Object> map = socketServerUtil.dateTimeSplit(dateTime);
		map.put("breakerHistoryId", socketServerUtil.byteArrayToInt(breakerHistoryIdByteArray));
		
		try {
			
			socketServerMapper.updateBreakerHistory(map);
			
		} catch (Exception e) {
			
			log.error("DB에러.");
			e.printStackTrace();
		}
	}
	
	/**
	 * 차단기에서 전달한 기상정보를 DB에 저장.
	 * 
	 * @param deviceIdtoString 차단기ID
	 * @param payload 바이트 배열로 이뤄진 데이터
	 */
	private void weatherReport(String deviceIdtoString, byte[] payload) {

		List<byte[]> segments;
		
		try {
			
			segments = socketServerUtil.splitByteArray(payload, (byte) 0x2C);
			
		} catch (Exception e) {
			
			log.error("바이트 배열을 나누는 작업 도중 문제가 발생하여 해당 작업을 중지합니다.");
			e.printStackTrace();
			return;
		}
        
		WeatherDataVo weatherDataVo = new WeatherDataVo();
		
		weatherDataVo.setBreakerId(deviceIdtoString);
		weatherDataVo.setDir(getValue(segments.get(0)));
		weatherDataVo.setTmp(getValue(segments.get(1)));
		weatherDataVo.setHm(getValue(segments.get(2)));
		weatherDataVo.setWind(getValue(segments.get(3)));
		weatherDataVo.setGust(getValue(segments.get(4)));
		weatherDataVo.setRain(getValue(segments.get(5)));
		weatherDataVo.setUv(getValue(segments.get(6)));
		weatherDataVo.setLight(getValue(segments.get(7)));
		
		try {
			
			socketServerMapper.insertWeatherData(weatherDataVo);
			
		} catch (Exception e) {
			
			log.error("DB 에러");
			e.printStackTrace();
		}
	}
	
	/**
	 * 문자열 데이터 안에 숫자 부분만 추출.
	 * 
	 * @param byteData
	 * @return 성공시: 숫자 형태의 문자열<br>
	 * 		   실패시: null
	 */
	private String getValue(byte[] byteData) {
		
		String value = null;
		
		try {
			
			String stringData = new String(byteData);
			int index = stringData.indexOf(":");
			
			if (index != -1) { value = stringData.substring(index + 1); }
			
		} catch (Exception e) {
			
			log.error("데이터 추출중 에러 발생");
			e.printStackTrace();
		}
		
		return value;
	}
	
	/**
	 * 차량감지 신호 수신.
	 * 
	 * @param breakerId
	 * @param payload
	 */
	private void carDetectionEventReport(String breakerId, byte[] payload) {
		
		// 서버가 이미 해당 차단기가 차량감지중인 상태인걸 안다면 로직무시.
		if (carDetectionBreakers.contains(breakerId)) 
			return;
		
		executorService.execute(() -> {
			
			try {
				
				log.debug("\"{}\" 차단기에서 차량 감지", breakerId);
				
				carDetectionBreakers.add(breakerId);
				log.debug("\"{}\" 차단기 차량감지 추가, {}", breakerId, carDetectionBreakers);
				
				try 							{ Thread.sleep(5000); } 
				catch (InterruptedException e) 	{ e.printStackTrace(); }
				
				carDetectionBreakers.remove(breakerId);
				log.debug("\"{}\" 차단기 차량감지 제거, {}", breakerId, carDetectionBreakers);
				
			} catch (Exception e) {
				
				log.error("람다식 내부에서 에러 발생");
				e.printStackTrace();
			}
		});
	}
}
