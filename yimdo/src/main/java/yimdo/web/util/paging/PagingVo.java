package yimdo.web.util.paging;

import lombok.Data;

@Data
public class PagingVo {

	private String searchCondition = "";

	/** 검색Keyword */
	private String searchKeyword = "";

	/** 검색사용여부 */
	private String searchUseYn = "";

	/** 현재페이지 */
	private int pageIndex = 1;

	/** 페이지갯수 */
	private int pageUnit = 10;

	/** 페이지사이즈 */
	private int pageSize = 10;

	/** firstIndex */
	private int firstIndex = 1;

	/** lastIndex */
	private int lastIndex = 1;

	/** recordCountPerPage */
	private int recordCountPerPage = 10;
	
	/** 시, 도 */
	private String sido = "";
	
	/** 시, 군, 구 */
	private String sigungu = "";
	
	/** 임도코드 */
	private String yimdoCode = "";
	
	/** 차단기 정책코드 */
	private String breakerPolicyCode = "";
	
	/** 상태인자코드 */
	private String elementCode = "";
	
	/** 조회시작시각 */
	private String dateStar = "";
	
	/** 조회마감시각 */
	private String dateEnd = "";
	
	/** 차단기ID */
	private String breakerId = "";
	
	/** 사용자이름 */
	private String userNm = "";
	
	/** 상태인자코드 */
	private String purposeEntryCode = "";
}
