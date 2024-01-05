package yimdo.serverConfig.security.component;

import java.io.IOException;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import yimdo.serverConfig.security.vo.CustomUser;
import yimdo.serverConfig.server.ServerConfig;

@Slf4j
public class CustomFilter extends OncePerRequestFilter {

	private final AesEncrypter aesEncrypter;
	
	public CustomFilter(AesEncrypter aesEncrypter) {
		
		this.aesEncrypter = aesEncrypter;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
        
		try {
			
			if (!"/logout".equals(request.getRequestURI())) {
				
				validateAuthentication(request, response);
			}
			
		} catch (DifferentAuthenticatedInfoException e) {
			
			response.sendRedirect("/logout");
			return;
		}
		
		filterChain.doFilter(request, response);
	}
	
	private void validateAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication instanceof AnonymousAuthenticationToken) {
			
			log.debug("비로그인 사용자");
			return;
		}
	    
		CustomUser user = (CustomUser) authentication.getPrincipal();
		String authenticatedIp = user.getAuthenticatedIp();
		String requestIp = request.getRemoteAddr();
		
		/*
		 * AuthenticationException 및 AccessDeniedException 를 발생시켜야 ExceptionTranslationFilter 에서 처리가능.
		 */
		
		if (!authenticatedIp.equals(requestIp)) {
			
			throw new DifferentAuthenticatedInfoException("요청ip와 인증된ip가 다름 (요청ip: " + requestIp + ", 인증된ip: " + authenticatedIp + ")");
		}
		
		String authenticatedSessionId = user.getAuthenticatedSessionId();
		String requestSessionId = request.getRequestedSessionId();
		
		if (!authenticatedSessionId.equals(requestSessionId)) {
			
			throw new DifferentAuthenticatedInfoException("요청sessionId와 인증된sessionId가 다름 (요청sessionId: " + requestSessionId + ", 인증된sessionId: " + authenticatedSessionId + ")");
		}
		
		String identifyTokenValue = user.getIdentifyTokenValue();
		String requestIdentifyTokenValue = "";
		Cookie[] cookies = request.getCookies();
		
		for (Cookie cookie : cookies) {
			
			if (ServerConfig.IDENTIFY_TOKEN_NAME.equals(cookie.getName())) {
				
				try {
					
					requestIdentifyTokenValue = aesEncrypter.decrypt(cookie.getValue());
				
				} catch (Exception e) {
					
					throw new DifferentAuthenticatedInfoException("복호화 도중 문제 발생: " + e.getMessage());
				}
			}
		}
		
		if (!identifyTokenValue.equals(requestIdentifyTokenValue)) {
			
			throw new DifferentAuthenticatedInfoException("요청identifyTokenValue와 저장된identifyTokenValue가 다름 (요청identifyTokenValue: " + requestIdentifyTokenValue + ", 저장된identifyTokenValue: " + identifyTokenValue + ")");
		}
	}
}
