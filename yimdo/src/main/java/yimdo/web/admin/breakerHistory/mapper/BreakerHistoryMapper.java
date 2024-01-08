package yimdo.web.admin.breakerHistory.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import yimdo.socketServer.vo.BreakerHistoryVo;
import yimdo.web.util.paging.PagingVo;

@Mapper
public interface BreakerHistoryMapper {

	int getBreakerHistoryListCount(PagingVo pagingVo) throws Exception;
	List<BreakerHistoryVo> getBreakerHistoryList(PagingVo pagingVo) throws Exception;
}
