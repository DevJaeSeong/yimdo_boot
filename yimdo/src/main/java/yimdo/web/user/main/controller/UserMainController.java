package yimdo.web.user.main.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import yimdo.serverConfig.security.vo.UserDetailVo;

@Controller
@RequestMapping("/user")
public class UserMainController {

	@GetMapping("/main")
	public String main(Model model, Authentication authentication) {
		
		UserDetailVo userDetailVo = (UserDetailVo) authentication.getDetails();
		model.addAttribute("userNm", userDetailVo.getUserNm());
		
		return "user/main";
	}
}
