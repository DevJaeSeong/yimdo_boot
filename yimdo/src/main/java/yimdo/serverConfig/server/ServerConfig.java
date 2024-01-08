package yimdo.serverConfig.server;

import org.springframework.stereotype.Component;

@Component
public class ServerConfig {
	
	/*
	 * 직접 지정하는 영역
	 */
	
	/** 서버 cpu core 개수 */
	public static final int CPU_CORE_SIZE = 2;
	
	/** 프로토콜 식별자 */
	public static final byte PROTOCAL_ID = (byte) 0xAB;
	
	/** 헤더 길이 */
	public static final int HEADER_LENGTH = 6;
	
	/** 소켓통신 포트 */
	public static final int SOCKET_PORT = 8060;
	
	/** FTP 서버 포트 */
	public static final int FTP_PORT = 21;
	
	/** 세션 유효시간 (초) */
	public static final int SESSION_MAX_INACTIVE_INTERVAL = 3600;
	
	/** 관리자 세션 유효시간 (초) */
	public static final int SESSION_MAX_INACTIVE_INTERVAL_FOR_ADMIN = 36000;
	
	/** 공공데이터포털 API key */
	public static final String API_KEY = "biKzts5lxCF3gsMQ%2BiVXXz2w487E%2Fa0zUgVVFEYfUuCCC%2BshCkedVB4UZlzoXisZAxSKeJ%2FqOH5aDt%2F%2Fm0qmDw%3D%3D";
	
	/** 페이징 처리시 한 페이지 당 출력할 Row 개수*/
	public static final int PAGE_SIZE = 10;
	
	/** 페이징 처리시 한 페이지당 출력할 페이지 수 */
	public static final int PAGE_UNIT = 10;
	
	/** 쿠키 이름 */
	public static final String IDENTIFY_TOKEN_NAME = "y_identifier";
	
	/*
	 * 시스템에 의해 지정되는 영역
	 */
	
	/** 정상개방 코드 */
	public static final String BREAKER_POLICY_NORMAL_OPEN = "1001";
	
	/** 정상차단 코드 */
	public static final String BREAKER_POLICY_NORMAL_CLOSE = "1002";
	
	/** 강제개방 코드 */
	public static final String BREAKER_POLICY_EMERGENCY_OPEN = "2001";
	
	/** 강제차단 코드 */
	public static final String BREAKER_POLICY_EMERGENCY_CLOSE = "2002";
	
	/** 고장 코드 */
	public static final String BREAKER_POLICY_ERROR = "3001";
	
	public static final String SYSTEM_ELEMENT_CODE = "0001";
	public static final String SYSTEM_MODIFIER = "시스템";
	public static final String BEACON_ELEMENT_CODE = "0003";
}
