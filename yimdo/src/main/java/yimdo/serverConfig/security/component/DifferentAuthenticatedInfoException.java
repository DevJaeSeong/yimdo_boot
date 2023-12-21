package yimdo.serverConfig.security.component;

import org.springframework.security.core.AuthenticationException;

public class DifferentAuthenticatedInfoException extends AuthenticationException {

	private static final long serialVersionUID = 1L;

	public DifferentAuthenticatedInfoException(String msg) {
		super(msg);
	}
}
