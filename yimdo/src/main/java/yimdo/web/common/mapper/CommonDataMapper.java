package yimdo.web.common.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import yimdo.web.common.vo.BreakerInfoVo;
import yimdo.web.common.vo.ModifyElementVo;
import yimdo.web.common.vo.SidoVo;
import yimdo.web.common.vo.SigunguVo;
import yimdo.web.common.vo.YimdoInfoVo;
import yimdo.web.util.paging.PagingVo;

@Mapper
public interface CommonDataMapper {

	public List<SidoVo> getSido() throws Exception;
	public List<SigunguVo> getSigungu(String sido) throws Exception;
	
	public List<YimdoInfoVo> getYimdoList(PagingVo pagingVo) throws Exception;
	public int getYimdoListTotalCount(PagingVo pagingVo) throws Exception;
	
	public BreakerInfoVo getBreakerById(String breakerId) throws Exception;
	public List<BreakerInfoVo> getAllBreakerList() throws Exception;
	public List<BreakerInfoVo> getBreakerList(PagingVo pagingVo) throws Exception;
	public int getBreakerListTotalCount(PagingVo pagingVo) throws Exception;
	
	public List<ModifyElementVo> getUsedModifyElementList() throws Exception;
	public List<ModifyElementVo> getModifyElementList(PagingVo pagingVo) throws Exception;
	public int getModifyElementTotalCnt(PagingVo pagingVo) throws Exception;
}
