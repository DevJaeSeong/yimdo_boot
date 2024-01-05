package  yimdo.serverConfig.security.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class UserDetailVo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String userId;
	private String userNm;
	private String mbtlNum;
	private String carNumber;
	private String affiliation;
	private String email;
}
