package yimdo.web.admin.breakerHistory.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.AllArgsConstructor;
import yimdo.web.admin.breakerHistory.service.BreakerHistoryService;
import yimdo.web.util.paging.PagingVo;

@Controller
@RequestMapping("/admin/breakerHistory")
@AllArgsConstructor
public class BreakerHistoryController {

	private final BreakerHistoryService breakerHistoryService;
	
	@GetMapping("/")
	public String breakerHistory() {
		
		return "admin/breakerHistory";
	}
	
	@GetMapping("/getBreakerHistoryList")
	@ResponseBody
	public Object getBreakerHistoryList(@ModelAttribute PagingVo pagingVo, Model model) {
		
		Map<String, Object> data = breakerHistoryService.getBreakerHistoryList(pagingVo);
		
		return data != null ? data : new ResponseEntity<>("조회된 데이터가 없음.", HttpStatus.BAD_REQUEST);
	}
}
