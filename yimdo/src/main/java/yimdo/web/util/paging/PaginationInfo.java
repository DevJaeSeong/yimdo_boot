package yimdo.web.util.paging;

import lombok.Data;

/**
 * 페이징 처리를 위한 데이터가 담기는 객체.
 * 페이징 처리에 필요한 데이터를 Required Fields, Not Required Fields 로 나누었다.
 * <p>    
 * <b>Required Fields</b><br>
 * : 사용자가 입력해야 하는 필드값이다. <br>
 * currentPageNo : 현재 페이지 번호. <br>
 * recordCountPerPage : 한 페이지당 게시되는 게시물 건 수. <br>
 * pageSize : 페이지 리스트에 게시되는 페이지 건수. <br>
 * totalRecordCount : 전체 게시물 건 수.
 * </p>
 * <p>
 * <b>Not Required Fields</b><br>
 * : 사용자가 입력한 Required Fields 값을 바탕으로 계산하여 정해지는 값이다. <br>
 * totalPageCount: 페이지 개수. <br>
 * firstPageNoOnPageList : 페이지 리스트의 첫 페이지 번호. <br>
 * lastPageNoOnPageList : 페이지 리스트의 마지막 페이지 번호. <br>
 * firstRecordIndex : 페이징 SQL의 조건절에 사용되는 시작 rownum. <br>
 * lastRecordIndex : 페이징 SQL의 조건절에 사용되는 마지막 rownum.
 * </p>
 */
@Data
public class PaginationInfo {

	// Required Fields
	private int currentPageNo;
	private int recordCountPerPage;
	private int pageSize;
	private int totalRecordCount;

	// Not Required Fields
	private int totalPageCount;
	private int firstPageNoOnPageList;
	private int lastPageNoOnPageList;
	private int firstRecordIndex;
	private int lastRecordIndex;

	public int getTotalPageCount() {
		totalPageCount = ((getTotalRecordCount() - 1) / getRecordCountPerPage()) + 1;
		return totalPageCount;
	}

	public int getFirstPageNo() {
		return 1;
	}

	public int getLastPageNo() {
		return getTotalPageCount();
	}

	public int getFirstPageNoOnPageList() {
		firstPageNoOnPageList = ((getCurrentPageNo() - 1) / getPageSize()) * getPageSize() + 1;
		return firstPageNoOnPageList;
	}

	public int getLastPageNoOnPageList() {
		lastPageNoOnPageList = getFirstPageNoOnPageList() + getPageSize() - 1;
		if (lastPageNoOnPageList > getTotalPageCount()) {
			lastPageNoOnPageList = getTotalPageCount();
		}
		return lastPageNoOnPageList;
	}

	public int getFirstRecordIndex() {
		firstRecordIndex = (getCurrentPageNo() - 1) * getRecordCountPerPage();
		return firstRecordIndex;
	}

	public int getLastRecordIndex() {
		lastRecordIndex = getCurrentPageNo() * getRecordCountPerPage();
		return lastRecordIndex;
	}
}
