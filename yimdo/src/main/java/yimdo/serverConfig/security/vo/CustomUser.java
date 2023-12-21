package yimdo.serverConfig.security.vo;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class CustomUser extends User {
	
	private static final long serialVersionUID = 1L;

	private String authenticatedIp;
	private String authenticatedSessionId;
	private String identifyTokenValue;
	
	public CustomUser(String username, String password, boolean enabled, boolean accountNonExpired, 
			boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
		
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
	}
	
	public String getAuthenticatedIp() {
		return authenticatedIp;
	}

	public void setAuthenticatedIp(String authenticatedIp) {
		this.authenticatedIp = authenticatedIp;
	}

	public String getAuthenticatedSessionId() {
		return authenticatedSessionId;
	}

	public void setAuthenticatedSessionId(String authenticatedSessionId) {
		this.authenticatedSessionId = authenticatedSessionId;
	}
	
	public String getIdentifyTokenValue() {
		return identifyTokenValue;
	}

	public void setIdentifyTokenValue(String identifyTokenValue) {
		this.identifyTokenValue = identifyTokenValue;
	}

	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		//sb.append(getClass().getName());
		sb.append("[");
		sb.append("Username=").append(this.getUsername()).append(", ");
		sb.append("authenticatedIP=").append(this.authenticatedIp).append(", ");
		sb.append("authenticatedSessionId=").append(this.authenticatedSessionId).append(", ");
		sb.append("identifyTokenValue=").append(this.identifyTokenValue).append(", ");
		/*
		sb.append("Password=[PROTECTED], ");
		sb.append("Enabled=").append(this.isEnabled()).append(", ");
		sb.append("AccountNonExpired=").append(this.isAccountNonExpired()).append(", ");
		sb.append("credentialsNonExpired=").append(this.isCredentialsNonExpired()).append(", ");
		sb.append("AccountNonLocked=").append(this.isAccountNonLocked()).append(", ");
		*/
		sb.append("Granted Authorities=").append(this.getAuthorities());
		sb.append("]");
		
		return sb.toString();
	}
}
