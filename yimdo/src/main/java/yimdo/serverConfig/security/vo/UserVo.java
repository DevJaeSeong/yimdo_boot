package yimdo.serverConfig.security.vo;

import lombok.Data;

/**
 * 주의) 이 클래스는 사용자 인증에 사용되는 클래스 이므로 이름, 휴대전화번호 등등<br>
 * 기타 정보에 관한것은 {@link UserDetailVo}를 사용해야함.
 * 
 * @see UserDetailVo
 */
@Data
public class UserVo {

	private String userId;
	private String password;
	private int authorityId;
	private boolean isAccountNonExpired;
	private boolean isAccountNonLocked;
	private boolean isCredentialsNonExpired;
	private boolean isEnabled;
}
