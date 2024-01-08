package yimdo.web.admin.breakerHistory.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import yimdo.socketServer.vo.BreakerHistoryVo;
import yimdo.web.admin.breakerHistory.mapper.BreakerHistoryMapper;
import yimdo.web.util.paging.PagingVo;

@Service
@AllArgsConstructor
@Slf4j
public class BreakerHistoryService {

	private final BreakerHistoryMapper breakerHistoryMapper;

	public Map<String, Object> getBreakerHistoryList(PagingVo pagingVo) {
		
		Map<String, Object> data = new HashMap<>();
		
		try {
			
			int totalCount = breakerHistoryMapper.getBreakerHistoryListCount(pagingVo);
			pagingVo.setPaginationInfo(totalCount);
			
			List<BreakerHistoryVo> breakerHistoryVos = breakerHistoryMapper.getBreakerHistoryList(pagingVo);
			
			data.put("breakerHistoryVos", breakerHistoryVos);
			data.put("paginationInfo", pagingVo.getPaginationInfo());
			
		} catch (Exception e) {
			
			log.error("DB 에러.");
			e.printStackTrace();
			data = null;
		}
		
		return data;
	}
}
