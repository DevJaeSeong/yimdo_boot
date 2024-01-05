package yimdo.web.general.account.vo;

import lombok.Data;

@Data
public class CreateAccountVo {
	
	private String userId;
	private String password;
	private String userNm;
	private String mbtlNum;
	private String email;
	private String carNumber;
	private String affiliation;
}
