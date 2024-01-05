package yimdo.socketServer.component;

import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class SocketServerContext {
	
	/**
	 * <p>
	 * Map 안에는 연결된 모든 차단기에 관하여 각각 차단기마다 4자리 문자열로된 차단기ID를 key값으로 차단기와 연결된 Socket 이 저장되어있으며,
	 * 2차원 Map 구조이므로 Key 값으로 "socket", "isRunning" 을 통해 한번더 꺼내온뒤 각각 Socket, Boolean 으로 캐스팅 하여 사용합니다.
	 * </p>
	 * <p>
	 * 꺼내온 Socket을 통해 해당 차단기와 통신이 가능하며 해당 차당기가 요청수행중인지 확인은 Boolean을 통해 중복요청을 방지하는데 사용됩니다.<br>
	 * </p>
	 * <p>
	 * 각각 해당하는 소켓은 연결이 끊기면 Map에서 제거됩니다.
	 * </p>
	 * 
	 * @return map (key: 4자리 문자열로된 차단기ID, value: Map<String, Object>)
	 */
	private static final Map<String, Socket> socketMap = new ConcurrentHashMap<String, Socket>();
	
	/**
	 * 해당 Set에 차단기ID 가 들어있다면 해당 차단기는 요청을 수행중 입니다. (중복 요청 방지용)
	 */
	private static final Set<String> runningBreakers = new CopyOnWriteArraySet<String>();
	
	/**
	 * 차량감지 신호를 보낸 차단기를 저장한 집합.
	 */
	private static final Set<String> carDetectionBreakers = new CopyOnWriteArraySet<String>();
	
	/**
	 * 차단기가 개방된 상태에서 차량감지중인 상태인 차단기를 저장한 집합.
	 */
	private static final Set<String> carDetectionOpenedBreakers = new CopyOnWriteArraySet<String>();
	
	public static Map<String, Socket> getSocketMap() {
		return socketMap;
	}

	public static Set<String> getRunningBreakers() {
		return runningBreakers;
	}

	public static Set<String> getCarDetectionBreakers() {
		return carDetectionBreakers;
	}

	public static Set<String> getCarDetectionbopenedbreakers() {
		return carDetectionOpenedBreakers;
	}
}
