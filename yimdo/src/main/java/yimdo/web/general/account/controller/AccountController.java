package yimdo.web.general.account.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class AccountController {

	@GetMapping("/login")
	public String login() {
		
		return "account/login";
	}
	
	@GetMapping("/loginSuccess")
	public String loginSuccess() {
		log.debug("로그인 성공");
		
		return "account/loginSuccess";
	}
	
	@GetMapping("/loginFail")
	public String loginFail() {
		log.debug("로그인 실패");
		
		return "account/loginFail";
	}
	
	@GetMapping("/logout")
	public String logout() {
		
		return "account/logout";
	}
}
