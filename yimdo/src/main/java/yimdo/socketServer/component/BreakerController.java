package yimdo.socketServer.component;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import yimdo.socketServer.mapper.BreakerControllerMapper;
import yimdo.socketServer.vo.BreakerControllerVo;
import yimdo.socketServer.vo.BreakerHistoryVo;

/**
 * 차단기를 제어를 수행하는 클래스.
 */
@Component
@AllArgsConstructor
@Slf4j
public class BreakerController {

	private SocketServerUtil socketServerUtil;
	private SocketServerSender socketServerSender;
	private BreakerControllerMapper breakerControllerMapper;

	/**
	 * 차단기 상태변경요청을 전송하고 그 이력을 DB에 저장.
	 * 
	 * @param breakerControllerVo 차단기 상태변경 요청에 사용되는 vo
	 * @see BreakerControllerVo
	 */
	public void breakerRequest(BreakerControllerVo breakerControllerVo) {
		log.debug("차단기 조작 요청 진행 => {}", breakerControllerVo);
		
		String breakerId = breakerControllerVo.getBreakerId();
		String breakerPolicyCode = breakerControllerVo.getBreakerPolicyCode();
		String breakerStatus;
		
		try {
			
			breakerStatus = breakerControllerMapper.selectBreakerStatusById(breakerId);
			
		} catch (Exception e) {
			
			log.debug("DB에서 차단기 상태 조회하던중 에러 발생.");
			e.printStackTrace();
			return;
		}
		
		if (!socketServerUtil.validateBreakerStatus(breakerId, breakerStatus, breakerPolicyCode)) 
			return;
		
		BreakerHistoryVo breakerHistoryVo = new BreakerHistoryVo();
		breakerHistoryVo.setBreakerId(breakerId);
		breakerHistoryVo.setBreakerPolicyCode(breakerPolicyCode);
		breakerHistoryVo.setElementCode(breakerControllerVo.getElementCode());
		breakerHistoryVo.setModifier(breakerControllerVo.getModifier());
		breakerHistoryVo.setModifyDetail(breakerControllerVo.getModifyDetail());
		breakerHistoryVo.setSystemControl(breakerControllerVo.getSystemControl());
		
		try {
			
			breakerControllerMapper.updateBreakerPolicyDetail(breakerHistoryVo);
			breakerControllerMapper.insertBreakerHistory(breakerHistoryVo);
			
		} catch (Exception e) {

			log.error("DB 에러");
			e.printStackTrace();
		}
		
		socketServerSender.sendRequestToBreaker(breakerId, breakerPolicyCode, breakerHistoryVo.getBreakerHistoryId());
	}
}
