package yimdo.web.admin.breakerStatus.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BreakerStatusMapper {

	Map<String, Integer> getBreakerListEachStatusCount(Map<String, String> msgMap) throws Exception;

}
