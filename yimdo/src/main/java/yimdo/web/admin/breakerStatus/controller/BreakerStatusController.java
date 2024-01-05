package yimdo.web.admin.breakerStatus.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.AllArgsConstructor;
import yimdo.web.admin.breakerStatus.service.BreakerStatusService;
import yimdo.web.admin.breakerStatus.vo.BreakerStatusVo;

@Controller
@RequestMapping("/admin/breakerStatus")
@AllArgsConstructor
public class BreakerStatusController {
	
	private BreakerStatusService breakerStatusService;
	
	@GetMapping("/")
	public String breakerStatus(Model model) {
		
		model.addAttribute("menuNum", 1);
		
		return "admin/breakerStatus";
	}
	
	/**
	 * 선택한 임도의 차단기들의 각각상태 갯수를 리턴
	 */
	@GetMapping("/getBreakerListEachStatusCount")
	@ResponseBody
	public Object getBreakerListEachStatusCount(@RequestParam Map<String, String> msgMap) {
		
		Map<String, Integer> statusCounts = breakerStatusService.getBreakerListEachStatusCount(msgMap);
		
		return statusCounts;
	}
	
	@PostMapping("/updateBreakerStatus")
	@ResponseBody
	public Map<String, Object> updateBreakerStatus(@RequestBody BreakerStatusVo breakerStatusVo) {
		
		Map<String, Object> result = new HashMap<>();
		
		try {
			
			breakerStatusService.updateBreakerStatus(breakerStatusVo);
			result.put("result", "success");
			
		} catch (Exception e) {
			
			result.put("result", "fail");
			e.printStackTrace();
		}
		
		return result;
	}
}
