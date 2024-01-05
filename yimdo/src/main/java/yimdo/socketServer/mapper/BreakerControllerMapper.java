package yimdo.socketServer.mapper;

import org.apache.ibatis.annotations.Mapper;

import yimdo.socketServer.vo.BreakerHistoryVo;

@Mapper
public interface BreakerControllerMapper {

	String selectBreakerStatusById(String breakerId) throws Exception;
	void updateBreakerPolicyDetail(BreakerHistoryVo breakerHistoryVo) throws Exception;
	void insertBreakerHistory(BreakerHistoryVo breakerHistoryVo) throws Exception;

}
