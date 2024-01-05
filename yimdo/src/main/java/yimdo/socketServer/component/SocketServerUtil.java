package yimdo.socketServer.component;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import yimdo.serverConfig.server.ServerConfig;

/**
 * 소켓통신 로직 수행에 쓰이는 유틸성 메소드를 가지고 있는 클래스.
 */
@Component
@Slf4j
public class SocketServerUtil {
	
	private Map<String, Socket> socketMap = SocketServerContext.getSocketMap();
	private Set<String> runningBreakers = SocketServerContext.getRunningBreakers();
	
    /**
     * data가 유효한 데이터인지 검사하여 boolean으로 리턴.
     * @param data 받은 데이터
     * @return 모든 검사를 통과하면 true, 그렇지 않다면 false
     */
	public boolean validateData(byte[] data) {

		byte reciveDataProtocalId = (byte) data[0];
		String breakerId = hexDataToString((byte) data[1], (byte) data[2]);
		Socket socket = (Socket) socketMap.get(breakerId);
		int dataLengthByHeader = (byte) data[5] & 0xff;
		int realDataLength = data.length - ServerConfig.HEADER_LENGTH;
		
		boolean isValid = false;

		if (reciveDataProtocalId != ServerConfig.PROTOCAL_ID)
			log.debug("식별되지 않은 Protocol ID. => {}", reciveDataProtocalId);
		
		else if (socket == null)
			log.debug("\"{}\" 차단기에 연결된 소켓 없음.", breakerId);
		
		else if (socket.isClosed())
			log.debug("\"{}\" 차단기에 연결된 소켓 이미 종료됨.", breakerId);
		
		else if (dataLengthByHeader != realDataLength)
			log.debug("헤더에 명시된 데이터 길이와 수신한 데이터 길이가 다름. => 명시된 길이: {}, 실제 길이: {}", dataLengthByHeader, realDataLength);
		
		else
			isValid = true;

		return isValid;
	}
	
	/**
	 * 보낼 데이터를 형식에 맞게 가공하여 byte[] 데이터로 반환
	 * @param breakerId 데이터를 보낼 차단기ID
	 * @param commandId 명령 식별자
	 * @param payloadLength 데이터 크기
	 * @param payload 데이터 내용
	 * @return 크기가 6 + payloadLength 인 byte[] 데이터
	 */
	public byte[] makeByteData(String breakerId, byte commandId, byte payloadLength, byte[] payload) {
		
		byte protocolId = ServerConfig.PROTOCAL_ID;
		byte[] deviceId = deviceIdToByteArray(breakerId);
		byte seq = (byte) 0x01;
		
		byte[] sendData = new byte[ServerConfig.HEADER_LENGTH + payload.length];
		sendData[0] = protocolId;
		sendData[1] = deviceId[0];
		sendData[2] = deviceId[1];
		sendData[3] = seq;
		sendData[4] = commandId;
		sendData[5] = payloadLength;
		
		for (int i = 0; i < payload.length; i++) {
			
			sendData[ServerConfig.HEADER_LENGTH + i] = payload[i];
		}
		
		return sendData;
	}
	
	/**
	 * 16진수 데이터를 그대로 문자열로 변환한후 합쳐 길이가 4인 문자열을 반환하는 메서드.
	 * 
	 * @param frontHexData 앞 2글자 (예시: (byte) 0x50)
	 * @param backHexData 뒤 2글자 (예시: (byte) 0x01)
	 * @return (frontBreakerId + backBreakerId) 문자열 (예시결과: "5001")
	 */
    public String hexDataToString(byte frontHexData, byte backHexData) {
    	
    	String deviceId1 = Integer.toHexString(frontHexData & 0xff);
    	String deviceId2 = Integer.toHexString(backHexData & 0xff);
    	
    	if (deviceId1.length() <= 1) deviceId1 = "0" + deviceId1;
    	if (deviceId2.length() <= 1) deviceId2 = "0" + deviceId2;
    	
    	String deviceIdtoString = deviceId1 + deviceId2;
    	
    	return deviceIdtoString;
    }
    
    /**
     * 4자리 문자열로된 deviceId 를 바이트 배열로 리턴.
     * @param 4자리 문자열로된 deviceId
     * @return 크기가 2인 byte[]
     */
    public byte[] deviceIdToByteArray(String deviceId) {
    	
    	String deviceId1 = deviceId.substring(0, 2);
    	String deviceId2 = deviceId.substring(2, 4);
    	
    	byte byteDeviceId1 = (byte) Integer.parseInt(deviceId1, 16);
    	byte byteDeviceId2 = (byte) Integer.parseInt(deviceId2, 16);
    	
    	byte[] deviceyteId = new byte[] {byteDeviceId1, byteDeviceId2};
    	
    	return deviceyteId;
    }
    
	
	/**
	 * 시간포멧 문자열을 나눠 두개의 dateTimeFormat을 맵에 담아 리턴
	 * @param dateTime 
	 * @return 0000-00-00 00:00:00 형태의 문자열과 식별번호를 담은 map(reciveDateTime, resDateTime) 을 리턴
	 */
	public Map<String, Object> dateTimeSplit(String dateTime) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		String reciveDateTime = "";
		String resDateTime = "";
		
		try {
			
			reciveDateTime = makeDateTimeFormat(dateTime.substring(0, 15));
			resDateTime = makeDateTimeFormat(dateTime.substring(15, 30));
			
		} catch (Exception e) {

			log.error("문자열 변환 실패.");
			e.printStackTrace();
		}
		
		map.put("reciveDateTime", reciveDateTime);
		map.put("resDateTime", resDateTime);
		
		return map;
	}
	
	/**
     * 00000000T000000 형태의 문자열을 0000-00-00 00:00:00 으로 변환하여 리턴.
     * @param dateTime 00000000T000000 형태의 문자열
     * @return 0000-00-00 00:00:00 형태의 문자열
     */
	public String makeDateTimeFormat(String dateTime) throws Exception {
		
		String year = dateTime.substring(0, 4);
		String month = dateTime.substring(4, 6);
		String day = dateTime.substring(6, 8);
		String hour = dateTime.substring(9, 11);
		String min = dateTime.substring(11, 13);
		String sec = dateTime.substring(13);
		
		/*
		log.debug("year: {}", year);
		log.debug("month: {}", month);
		log.debug("day: {}", day);
		log.debug("hour: {}", hour);
		log.debug("min: {}", min);
		log.debug("sec: {}", sec);
		*/
		
		String dateTimeFormat = year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec;
		//log.debug("dateTimeFormat: {}", dateTimeFormat);
		
		return dateTimeFormat;
	}
	
	/**
	 * int => byte[]
	 * 
	 * @param value 바이트 배열로 변환할 정수
	 * @return 크기가 4인 byte[]
	 */
	public byte[] intToByteArray(int value) {
		
		byte[] byteArray = new byte[4];
		
	    byteArray[0] = (byte) (value >> 24);
	    byteArray[1] = (byte) (value >> 16);
	    byteArray[2] = (byte) (value >> 8);
	    byteArray[3] = (byte) value;
	    
	    return byteArray;
	}
	
	/**
	 * byte[] => int
	 * 
	 * @param byteArray int로 변환할 byte배열
	 * @return 변환된 int 타입 값
	 * @throws IllegalArgumentException byte배열의 크기가 4가 아닌경우
	 */
	public int byteArrayToInt(byte[] byteArray) throws IllegalArgumentException {
		
		if (byteArray.length != 4)
			throw new IllegalArgumentException("크기가 4인 바이트 배열만 가능합니다.");
		
		int byteArrayToInt = (byteArray[0] & 0xFF) << 24
						   | (byteArray[1] & 0xFF) << 16
						   | (byteArray[2] & 0xFF) << 8
						   | (byteArray[3] & 0xFF);
		
		return byteArrayToInt;
	}
	
	/**
	 * byte[] 데이터 내용을 콘솔에 출력합니다.
	 * @param data 받은 byte[] 데이터
	 */
	public void printByteData(byte[] data) {
		
        StringBuilder sb = new StringBuilder("[");
        
        for (byte b : data) {
        	
            //sb.append(String.format("0x%02X", b & 0xFF)).append(", ");
            sb.append(String.format("%02X", b & 0xFF)).append(", ");
        }
        
        sb.setLength(sb.length() - 2);
        sb.append("]");

        log.debug("{}", sb.toString());
	}
	
	public String printByteDataToString(byte[] data) {
		
        StringBuilder sb = new StringBuilder("[");
        
        for (byte b : data) {
        	
            //sb.append(String.format("0x%02X", b & 0xFF)).append(", ");
            sb.append(String.format("%02X", b & 0xFF)).append(", ");
        }
        
        sb.setLength(sb.length() - 2);
        sb.append("]");

        return sb.toString();
	}
    
    /**
     * 바이트 배열을 특정 데이터를 기준으로 나눠서 반환.
     * 
     * @param source 나누려하는 byte[]
     * @param delimiter 배열을 나눌 기준 byte 데이터
     * @return delimiter를 기준으로 나눠진 byte[]를 담은 List
     * @throws Exception
     */
    public List<byte[]> splitByteArray(byte[] source, byte delimiter) throws Exception {
    	
        List<byte[]> result = new ArrayList<>();
        int start = 0;

        for (int i = 0; i < source.length; i++) {
        	
            if (source[i] == delimiter) {
            	
                byte[] segment = Arrays.copyOfRange(source, start, i);
                result.add(segment);
                start = i + 1;
            }
        }

        byte[] lastSegment = Arrays.copyOfRange(source, start, source.length);
        result.add(lastSegment);

        return result;
    }

    /**
     * 수신받은 바이트 배열에서 헤더 부분을 제외한 데이터를 반환.
     * 
     * @param data 추출할 데이터
     * @param payloadLength payload 길이
     * @return payload 에 해당되는 byte 배열
     */
	public byte[] getPayload(byte[] data, int payloadLength) {
		
		byte[] payload = new byte[payloadLength];
		
    	for (int i = 0; i < payloadLength; i++) {
    		
    		payload[i] = (byte) data[ServerConfig.HEADER_LENGTH + i];
    	}
		
		return payload;
	}

	public boolean validateBreakerStatus(String breakerId, String breakerStatus, String breakerPolicyCode) {

		if (socketMap.get(breakerId) == null) {
			
			log.debug("\"{}\" 차단기에 연결된 소켓이 없습니다.", breakerId);
			return false;
		}
		
		if (runningBreakers.contains(breakerId)) {
			
			log.debug("\"{}\" 차단기가 이미 작동중입니다.", breakerId);
			return false;
		}
		
		if 
		(
			(("01".equals(breakerStatus) || "06".equals(breakerStatus)) && ServerConfig.BREAKER_POLICY_NORMAL_OPEN.equals(breakerPolicyCode))
			|| ("02".equals(breakerStatus) && ServerConfig.BREAKER_POLICY_NORMAL_CLOSE.equals(breakerPolicyCode))
			|| ("03".equals(breakerStatus) && ServerConfig.BREAKER_POLICY_EMERGENCY_OPEN.equals(breakerPolicyCode))
			|| ("04".equals(breakerStatus) && ServerConfig.BREAKER_POLICY_EMERGENCY_CLOSE.equals(breakerPolicyCode))
		) 
		{
			log.debug("\"{}\" 차단기의 상태와 정책이 이미 일치합니다.", breakerId);
			return false;
		}
			
		return true;
	}
}
