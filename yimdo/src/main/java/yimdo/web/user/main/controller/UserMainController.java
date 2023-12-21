package yimdo.web.user.main.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/user")
@Slf4j
public class UserMainController {

	@GetMapping("/main")
	public String main() {
		
		SecurityContextHolder.getContext().getAuthentication().getName();
		
		return "user/main";
	}
}
