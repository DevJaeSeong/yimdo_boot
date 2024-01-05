package yimdo.web.admin.breakerStatus.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import yimdo.socketServer.component.BreakerController;
import yimdo.socketServer.vo.BreakerControllerVo;
import yimdo.web.admin.breakerStatus.mapper.BreakerStatusMapper;
import yimdo.web.admin.breakerStatus.vo.BreakerStatusVo;

@Service
@AllArgsConstructor
@Slf4j
public class BreakerStatusService {

	private BreakerStatusMapper breakerStatusMapper;
	private BreakerController breakerController;
	
	public Map<String, Integer> getBreakerListEachStatusCount(Map<String, String> msgMap) {
		
		Map<String, Integer> statusCounts = null;
		
		try {
			
			statusCounts = breakerStatusMapper.getBreakerListEachStatusCount(msgMap);
			
		} catch (Exception e) {
			
			log.error("DB 에러");
			e.printStackTrace();
		}
		
		return statusCounts;
	}

	public void updateBreakerStatus(BreakerStatusVo breakerStatusVo) {
		
		Map<String, Object> map = new HashMap<>();
		
		List<String> selectedBreakers = breakerStatusVo.getSelectedBreakers();
		String modifier = breakerStatusVo.getModifier();
		String modifyDetail = breakerStatusVo.getModifyDetail();
		String elementCode = breakerStatusVo.getSelectedElement();
		String policyCode = breakerStatusVo.getSelectedPolicy();
		String systemControl = breakerStatusVo.getSystemControl();
		
		for (String breakerId : selectedBreakers) {
			
    		BreakerControllerVo breakerControllerVo = new BreakerControllerVo(breakerId, 
    																		  policyCode, 
    																		  modifier, 
    																		  elementCode, 
																			  modifyDetail);
    		
    		breakerControllerVo.setSystemControl(systemControl);
    		
    		breakerController.breakerRequest(breakerControllerVo);
			map.put(breakerId, "success");
		}
	}
}
