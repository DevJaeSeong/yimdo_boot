package yimdo.serverConfig.security.vo;

import lombok.Data;

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
