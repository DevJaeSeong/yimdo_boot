package yimdo.serverConfig.security.component;

import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import yimdo.serverConfig.security.vo.CustomUser;

@Slf4j
public class RequestPrintFilter extends OncePerRequestFilter {

	private final Executor executor;
	
	public RequestPrintFilter(Executor executor) {
		
		this.executor = executor;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
        
		request = new CustomHttpServletRequestWrapper(request, executor);
		
		printRequest((CustomHttpServletRequestWrapper) request);
		
		filterChain.doFilter(request, response);
	}
	
	/**
	 * 서버에 접근하는 사용자의 정보를 콘솔에 출력.
	 */
	private void printRequest(CustomHttpServletRequestWrapper request) {
		
		String requestURI = request.getRequestURI();
		String remoteAddr = request.getRemoteAddr();
		String sessionId = request.getRequestedSessionId();
		String method = request.getMethod();
		String requestBody = readRequest(request);
		String principal = "";
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication instanceof UsernamePasswordAuthenticationToken) {
			
			principal = ((CustomUser) authentication.getPrincipal()).toString();
			
		} else if (authentication instanceof AnonymousAuthenticationToken) {
			
			principal = (String) authentication.getPrincipal();
		}
		
		log.debug("사용자: [\"{}\", \"{}\", {}]", remoteAddr, sessionId, principal);
		log.debug("요청내용: [({}) \"{}\", \"{}\"]", method, requestURI, requestBody);
	}

	private String readRequest(CustomHttpServletRequestWrapper request) {
		
		String method = request.getMethod();
		String requestData = readParameters(request);
		
		if (!"GET".equals(method)) {
			
			if (request.getContentType().toLowerCase().contains("json")) {
				
				requestData = new String(request.getRequestData());
			} 
		}
		
		return requestData;
	}
	
    private String readParameters(HttpServletRequest request) {
    	
        Enumeration<String> parameterNames = request.getParameterNames();
        Map<String, Object> parameterMap = new LinkedHashMap<String, Object>();
        
        while (parameterNames.hasMoreElements()) {
        	
            String parameterName = parameterNames.nextElement();
            String parameterValue = request.getParameter(parameterName);
            
            parameterMap.put(parameterName, parameterValue);
        }
		
    	return parameterMap.toString();
    }
}
