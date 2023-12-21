package yimdo.web.general.home.controller;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class HomeController {
	
	/*
	@Autowired
	private SecurityMapper securityMapper;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@GetMapping("/test/create")
	public void temp() throws Exception {
		
		Map<String, Object> map1 = Map.of("userId", "smartMg", "userPw", passwordEncoder.encode("12345"), "authorityId", 1);
		Map<String, Object> map2 = Map.of("userId", "smartUser", "userPw", passwordEncoder.encode("12345"), "authorityId", 2);
		
		securityMapper.createAccount(map1);
		securityMapper.createAccount(map2);
	}
	*/
	
	@GetMapping({"/", "/home"})
	public String home() {
		
		@SuppressWarnings("unchecked")
		String role = ((List<GrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities()).get(0).getAuthority();
		log.debug("{}", role);
		
		if ("ROLE_ANONYMOUS".equals(role)) {
			
			return "redirect:/login";
		}
		
		String redirect;
		
		switch (role) {
		
			case "ROLE_ADMIN" -> redirect = "redirect:/admin/main";
			case "ROLE_USER"  -> redirect = "redirect:/user/main";
			default 		  -> redirect = "redirect:/login";
		}
		
		return redirect;
	}
}
