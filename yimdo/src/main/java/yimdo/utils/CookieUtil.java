package yimdo.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import yimdo.serverConfig.server.ServerConfig;

public class CookieUtil {

	/**
	 * 원하는 쿠키 삭제 메서드.
	 * 
	 * @param cookieName 삭제할 쿠키 이름
	 * @param request
	 * @param response
	 */
    public static void deleteCookie(String cookieName, HttpServletRequest request, HttpServletResponse response) {
    	
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
        	
            for (Cookie cookie : cookies) {
            	
                if (cookie.getName().equals(cookieName)) {
                	
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }
    
    /**
     * 식별토큰 생성.
     * 
     * @param encryptedValue 암호화된 값
     * @param request
     * @param response
     */
    public static void createIdentifyToken(String encryptedValue, HttpServletRequest request, HttpServletResponse response) {
    	
		Cookie cookie = new Cookie(ServerConfig.IDENTIFY_TOKEN_NAME, encryptedValue);
		cookie.setDomain(request.getServerName());
		cookie.setPath("/");
		cookie.setSecure(false);
		cookie.setHttpOnly(true);
		
		response.addCookie(cookie);
    }    
    
	/**
	 * 식별토큰 삭제.
	 * 
	 * @param request
	 * @param response
	 */
    public static void deleteIdentifyToken(HttpServletRequest request, HttpServletResponse response) {
    	
		Cookie cookie = new Cookie(ServerConfig.IDENTIFY_TOKEN_NAME, null);
		cookie.setDomain(request.getServerName());
		cookie.setPath("/");
		cookie.setSecure(false);
		cookie.setMaxAge(0);
		
		response.addCookie(cookie);
    }
}
