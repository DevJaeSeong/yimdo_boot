package yimdo.web.common.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import yimdo.web.common.service.CommonDataService;
import yimdo.web.common.vo.SidoVo;
import yimdo.web.common.vo.SigunguVo;
import yimdo.web.util.paging.PagingVo;

@RestController
@RequestMapping("/common")
@AllArgsConstructor
public class CommonDataController {
	
	private CommonDataService commonDataService;
	
	/**
	 * 시, 도 테이블을 리턴
	 */
	@GetMapping("/getSido")
	public Object getSido() {
		
		List<SidoVo> sidoVos = commonDataService.getSido();
		
		return sidoVos;
	}
	
	/**
	 * 시, 군, 구 테이블을 리턴
	 */
	@GetMapping("/getSigungu")
	public Object getSigungu(@RequestParam("sido") String sido) {
		
		List<SigunguVo> sigunguVos = commonDataService.getSigungu(sido);
		
		return sigunguVos;
	}
	
	/**
	 * 임도 테이블을 리턴
	 */
	@GetMapping("/getYimdoList")
	public Object getYimdoList(@ModelAttribute PagingVo pagingVo) {
		
		Map<String, Object> data = commonDataService.getYimdoData(pagingVo);
		
		return data;
	}
	
	/**
	 * 차단기 테이블을 리턴
	 */
	@GetMapping("/getBreakerList")
	public Object getBreakerList(@ModelAttribute PagingVo pagingVo) {
		
		Map<String, Object> data = commonDataService.getBreakerData(pagingVo);
		
		return data;
	}
	
	/**
	 * 상태변경인자 테이블 리턴
	 */
	@GetMapping("/getElementList")
	public Object getElemetList(@ModelAttribute PagingVo pagingVo) {
		
		Map<String, Object> data = commonDataService.getElementData(pagingVo);
		
		return data;
	}
}
