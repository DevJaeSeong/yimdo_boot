package yimdo.web.util.paging;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import yimdo.serverConfig.server.ServerConfig;

/**
 * 페이징 처리에 필요한 요소를 처리해주는 
 */
@Component("pagingMaker")
public class PagingMaker {
	
	/**
	 * 페이징처리에 필요한 PagingVo, PaginationInfo의 값을 설정후 Map에 담아 반환.
	 * @param pagingVo 사용자의 get요청에 담긴 query문 내용을 담은 객체
	 * @param totalCount 해당 데이터가 DB에 저장된 총 갯수
	 * @return new HashMap("paginationInfo": {@link PaginationInfo}, "pagingVo": {@link PagingVo})
	 */
	public Map<String, Object> pagingMake(PagingVo pagingVo, int totalCount) {
		
		pagingVo.setPageUnit(ServerConfig.PAGE_UNIT);
		pagingVo.setPageSize(ServerConfig.PAGE_SIZE);
		
		PaginationInfo paginationInfo = new PaginationInfo();
		paginationInfo.setCurrentPageNo(pagingVo.getPageIndex()); 		//현재 페이지 번호
		paginationInfo.setRecordCountPerPage(pagingVo.getPageUnit()); 	//한 페이지에 게시되는 게시물 건수
		paginationInfo.setPageSize(pagingVo.getPageSize()); 			//페이징 리스트의 사이즈
		paginationInfo.setTotalRecordCount(totalCount);

		pagingVo.setFirstIndex(paginationInfo.getFirstRecordIndex());
		pagingVo.setLastIndex(paginationInfo.getLastRecordIndex());
		pagingVo.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("paginationInfo", paginationInfo);
		map.put("pagingVo", pagingVo);
		
		return map;
	}
}
