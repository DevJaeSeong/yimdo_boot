package yimdo.web.general.account.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;
import yimdo.web.general.account.service.AccountService;
import yimdo.web.general.account.vo.CreateAccountVo;

@Controller
@Slf4j
public class AccountController {

	private final AccountService accountService;
	
	public AccountController(AccountService accountService) {
		
		this.accountService = accountService;
	}
	
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
	
	@GetMapping("/createAccount")
	public String createAccount() {
		
		return "account/createAccount";
	}
	
	@PostMapping("/createAccountConfirm")
	@ResponseBody
	public Object createAccountConfirm(@RequestBody CreateAccountVo createAccountVo) {
		log.debug("<= {}", createAccountVo);
		
		String userId = createAccountVo.getUserId();
		Map<String, Object> responseData = new HashMap<>();
		
		if (accountService.isExistsAccount(createAccountVo.getUserId())) {

			log.debug("\"{}\" 은 이미 DB에 등록되어있음.", userId);
			responseData.put("result", "already_exists");
			return responseData;
		}
		
		if (accountService.createAccount(createAccountVo)) {
			
			log.debug("\"{}\" 등록 성공.", userId);
			responseData.put("result", "success");
			
		} else {
			
			log.debug("\"{}\" 등록 실패.", userId);
			responseData.put("result", "fail");
		}
		
		return responseData;
	}
}
