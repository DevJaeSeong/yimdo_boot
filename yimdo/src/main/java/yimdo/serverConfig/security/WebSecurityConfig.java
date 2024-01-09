package yimdo.serverConfig.security;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;

import lombok.extern.slf4j.Slf4j;
import yimdo.serverConfig.security.component.AesEncrypter;
import yimdo.serverConfig.security.component.CustomFilter;
import yimdo.serverConfig.security.component.DifferentAuthenticatedInfoException;
import yimdo.serverConfig.security.component.RequestPrintFilter;
import yimdo.serverConfig.security.mapper.SecurityMapper;
import yimdo.serverConfig.security.vo.CustomUser;
import yimdo.serverConfig.security.vo.UserDetailVo;
import yimdo.serverConfig.security.vo.UserVo;
import yimdo.serverConfig.server.ServerConfig;
import yimdo.utils.CookieUtil;

@EnableWebSecurity(debug = false)
@Configuration
@Slf4j
public class WebSecurityConfig {

	private final SecurityMapper securityMapper;
	private final AesEncrypter aesEncrypter;
	private final Executor executor;
	
	public WebSecurityConfig(SecurityMapper securityMapper, AesEncrypter aesEncrypter, Executor executor) {
		
		this.securityMapper = securityMapper;
		this.aesEncrypter = aesEncrypter;
		this.executor = executor;
	}

	@Bean
	WebSecurityCustomizer webSecurityCustomizer() {
		
		return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		/*
		 * 주의: 내장WAS는 빈으로 등록한 필터를 SecurityFilterChain와 무관하게
		 * 		 ApplicationFilterChain에 등록되어 실행됨으로 web.ignoring() 적용받지 않는다
		 *       이를 해결하려면 그러므로 SecurityFilterChain에 종속시킬 필터는
		 *       빈으로 등록하지 않아야 한다.
		 */
		http.addFilterAfter(new RequestPrintFilter(executor), CsrfFilter.class)
			.addFilterAfter(new CustomFilter(aesEncrypter), AnonymousAuthenticationFilter.class);
		
		
		http.exceptionHandling((exceptionHandlingCustomizer) -> exceptionHandlingCustomizer
				
			.authenticationEntryPoint((request, response, authException) -> {
				
				switch (authException) {
				
					case DifferentAuthenticatedInfoException exception -> {
						
						log.debug("인증 에러 발생");
						exception.printStackTrace();
						response.sendRedirect("/error");
					}
					
					default -> {
						
						log.debug("인증 에러 발생");
						authException.printStackTrace();
						response.sendRedirect("/error");
					}
				}
			})
			.accessDeniedHandler((request, response, accessDeniedException) -> {
				
				log.debug("인증 에러 발생");
				accessDeniedException.printStackTrace();
				response.sendRedirect("/error");
			})
		);
		
		http.authorizeHttpRequests((requests) -> requests
				
			.requestMatchers("/", "/home", "/logout", "/error", "/test/**").permitAll()
			.requestMatchers("/createAccount", "/createAccountConfirm").permitAll()
			.requestMatchers("/admin/**").hasRole("ADMIN")
			.requestMatchers("/user/**").hasAnyRole("ADMIN", "USER")
			.anyRequest().authenticated()
		);
		
		http.formLogin((form) -> form
				
			.loginPage("/login")
			.loginProcessingUrl("/loginConfirm")
			.permitAll()
			.usernameParameter("id")
		    .passwordParameter("pw")
			.successHandler((request, response, authentication) -> {
				
				// 식별정보 추가
				String identifyTokenValue = aesEncrypter.generateRandomString(16);
				String encryptedValue = "";
				
				try { encryptedValue = aesEncrypter.encrypt(identifyTokenValue); } 
				catch (Exception e) {
					
					log.error("식별값 암호화중 에러발생.");
					e.printStackTrace();
				}
				
				CustomUser customUser = (CustomUser) authentication.getPrincipal();
				customUser.setAuthenticatedIp(request.getRemoteAddr());
				customUser.setAuthenticatedSessionId(request.getSession().getId());
				customUser.setIdentifyTokenValue(identifyTokenValue);

				CookieUtil.createIdentifyToken(encryptedValue, request, response);
				
				// 계정 상세정보 추가
				try {
					
					UserDetailVo userDetailVo = securityMapper.getUserDetailById(authentication.getName());
					UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
					token.setDetails(userDetailVo);
					
				} catch (Exception e) {
					
					log.error("인증정보에 userDetailVo 추가하려던 도중 에러 발생.");
					e.printStackTrace();
				}
				
				response.sendRedirect("/home");
			})
			.failureHandler((request, response, authentication) -> {
				
				response.sendRedirect("/loginFail");
			})
		);
		
		http.logout((logout) -> logout
				
			.logoutUrl("/logoutConfirm")
			.permitAll()
			.deleteCookies(ServerConfig.IDENTIFY_TOKEN_NAME, "remember-me")
			.logoutSuccessHandler((request, response, authentication) -> {
				
				response.sendRedirect("/home");
			})
		);
		
		http.csrf((csrfCustomizer) -> csrfCustomizer
				
			.ignoringRequestMatchers("/logoutConfirm")
		);
		
		return http.build();
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		
		return new BCryptPasswordEncoder(14);
	}
	
	@Bean
	UserDetailsService userDetailsService() {
		
		return new UserDetailsService() {
			
			@Override
			public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
				
				UserVo userVo = null;
				
				try {
					
					userVo = securityMapper.getUserByIdForLogin(username);
					
				} catch (Exception e) {
					
					log.error("DB 에러 발생");
					e.printStackTrace();
				}
				
				if (userVo == null)
					throw new UsernameNotFoundException("\"" + username + "\" 를 DB에서 조회하지 못함");
				
				CustomUser customUser = new CustomUser(userVo.getUserId(), 
													   userVo.getPassword(), 
													   userVo.isEnabled(), 
													   userVo.isAccountNonExpired(), 
													   userVo.isCredentialsNonExpired(), 
													   userVo.isAccountNonLocked(), 
													   getAuthorities(userVo.getAuthorityId()));
				
				return customUser;
			}
			
			private Collection<? extends GrantedAuthority> getAuthorities(int AuthorityId) {

				List<GrantedAuthority> authorities;
				
				switch (AuthorityId) {
				
					case 1 	-> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
					case 2 	-> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
					default -> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
				}
				
				return authorities;
			}
		};
	}
}
