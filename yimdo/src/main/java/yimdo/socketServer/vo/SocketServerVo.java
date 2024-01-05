package yimdo.socketServer.vo;

import lombok.Data;

@Data
public class SocketServerVo {

	String breakerId;
	String breakerIp;
	
	public SocketServerVo(String breakerId, String breakerIp) {
		
		this.breakerId = breakerId;
		this.breakerIp = breakerIp;
	}
}
