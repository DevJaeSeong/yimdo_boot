$(function () {
	
	init();
	fetch_getSido();
	fetch_getBreakerHistoryList();
})

function init() {

    $('table tr').mouseover(function () {
        $(this).css("backgroundColor", "#faf8f1");
    });

    $('table tr').mouseout(function () {
        $(this).css("backgroundColor", "#fff");
    });
	
	$('#elementListViewBtn').bind('click', function (e) {
		
	    e.preventDefault();
	    $('#popup_2').bPopup();
	});	
}

function fetch_getSido() {
	
	fetch("/common/getSido")
	.then(response => {
		if (response.ok) return response.json();
		else throw new Error("네트워크 에러");
	})
	.then(sidoVos => {
		
		$('#sido').empty();
		$('#sido').append('<option value="">시/도 선택</option>');
		
		sidoVos.forEach(sidoVo => {
			
			$('#sido').append('<option value="' + sidoVo.sidoCode + '">' + sidoVo.sidoName + '</option>');
		})
	})
	.catch(error => {
		
		console.error("fetch에러: ", error);
	})
	
}

function fetch_getSigungu() {
	
	let sido = $('#sido').val();
	
	fetch("/common/getSigungu?sido=" + sido)
	.then(response => {
		if (response.ok) return response.json();
		else throw new Error("네트워크 에러");
	})
	.then(sigunguVos => {
		
		$('#sigungu').empty();
		$('#sigungu').append('<option value="">시/군/구 선택</option>');
		
		sigunguVos.forEach(sigunguVo => {
			
			$('#sigungu').append('<option value="' + sigunguVo.sigunguCode + '">' + sigunguVo.sigunguName + '</option>');
		})
	})
	.catch(error => {
		
		console.error("fetch에러: ", error);
	})
	
}

function fetch_getBreakerHistoryList(pageNo) {
	
	if (pageNo <= 0 || pageNo == null) pageNo = 1;
	
    let sido = $('#sido').val();
    let sigungu = $('#sigungu').val();
	let searchKeyword = $('#breakerSearchInput').val();
	let breakerPolicyCode = $(".policyRadio").filter(':checked:first').val();
	let elementCode = $("#selectedElement").val();
	let dateStar = $("#dateStar").val();
	let dateEnd = $("#dateEnd").val();
	
	
    if (sido == undefined) sido = "";
    if (sigungu == undefined) sigungu = "";
	if (breakerPolicyCode == undefined) breakerPolicyCode = "";
    if (dateEnd != "") {
        
        if (dateEnd < dateStar) {
    
            alert("마감일이 시작일보다 빠릅니다. 기간을 다시 설정해주세요.");
            return;
        }
    }
	
	let queryString = "pageIndex=" + pageNo + 
					  "&sido=" + sido + 
					  "&sigungu=" + sigungu + 
					  "&breakerPolicyCode=" + breakerPolicyCode + 
					  "&elementCode=" + elementCode + 
					  "&dateStar=" + dateStar + 
					  "&dateEnd=" + dateEnd + 
					  "&searchKeyword=" + searchKeyword;	
	
	fetch("/admin/breakerHistory/getBreakerHistoryList?" + queryString)
	.then(response => {
		if (response.ok) return response.json();
		else throw new Error("네트워크 에러");
	})
	.then(data => {
		
		let breakerHistoryVos = data.breakerHistoryVos;
		let paginationInfo = data.paginationInfo;
		let prev = (paginationInfo.firstPageNoOnPageList == 1 ? 1 : (paginationInfo.currentPageNo - paginationInfo.pageSize < 1 ? 1 : paginationInfo.currentPageNo - paginationInfo.pageSize));
		let next = (paginationInfo.lastPageNoOnPageList == paginationInfo.lastPageNo ? paginationInfo.lastPageNo : (paginationInfo.currentPageNo + paginationInfo.pageSize > paginationInfo.lastPageNo ? paginationInfo.lastPageNo : paginationInfo.currentPageNo + paginationInfo.pageSize));
		
		$('#breakerTotalCount').html("<span class='data_num'>총 <strong>" + paginationInfo.totalRecordCount + "</strong>건의 게시물이 있습니다. <em>(" + paginationInfo.totalPageCount + "페이지)</em></span>");
		
		$('#breakerList').empty();
		breakerHistoryVos.forEach(breakerHistoryVo => {
			
			let breakerPolicyDeco;
			
			switch (breakerHistoryVo.breakerPolicyCode) {
			
			  case "1001":
				  breakerPolicyDeco = "status_value01";
			    break;
			    
			  case "1002":
				  breakerPolicyDeco = "status_value05";
			    break;
			  
			  case "2001":
				  breakerPolicyDeco = "status_value02";
			    break;
			    
			  case "2002":
				  breakerPolicyDeco = "status_value04";
			    break;
			  
			  case "3001":
				  breakerPolicyDeco = "status_value03";
			    break;
			  
			  default:
				  breakerPolicyDeco = "status_value03";
			    break;
			};
			
			$('#breakerList').append('<tr><td>' + breakerHistoryVo.yimdoName + '</td><td>' + breakerHistoryVo.breakerId + '</td><td><span class="' + breakerPolicyDeco + '">' + breakerHistoryVo.breakerPolicyName + '</span></td><td>' + breakerHistoryVo.modifyReqDate + '</td><td>' + breakerHistoryVo.modifyResDate + '</td><td>' + breakerHistoryVo.modifier + '</td><td>' + breakerHistoryVo.elementName + '</td><td class="tbtdL">' + breakerHistoryVo.modifyDetail + '</td></tr>');
		})
		
		$("#breakerListPaging").empty();
		
		$("#breakerListPaging").append('<button type=\"button\" class=\"btn_page_first\" title=\"맨앞 페이지\" onclick=\"fetch_getBreakerHistoryList(1); return false;\">맨 처음</button>');
		$("#breakerListPaging").append('<button type=\"button\" class=\"btn_page_prev\" title=\"이전 페이지\" onclick=\"fetch_getBreakerHistoryList(' + prev + '); return false;\">이전</button>');

		for (let i = paginationInfo.firstPageNoOnPageList; i <= paginationInfo.lastPageNoOnPageList; i++) {
			
		  if (i == paginationInfo.currentPageNo) {
			  
			 $("#breakerListPaging").append('<a class="ml2 mr2 current" href=\"#\" id="breakerListCurrentPage" data-breakerlistcurrentpage="' + i + '" title=\"현재 ' + i + '페이지\" >' + i + '</a>');
			 
		  } else {
			  
			 $("#breakerListPaging").append('<a class="ml2 mr2" href=\"#\" title=\"' + i + '페이지\" onclick=\"fetch_getBreakerHistoryList(' + i + '); return false;\">' + i + '</a>');
		  }
		}
		
		$("#breakerListPaging").append('<button type=\"button\" class=\"btn_page_next\" title=\"다음 페이지\" onclick=\"fetch_getBreakerHistoryList(' + next + '); return false;\">다음</button>');
		$("#breakerListPaging").append('<button type=\"button\" class=\"btn_page_last\" title=\"맨 마지막\" onclick=\"fetch_getBreakerHistoryList(' + paginationInfo.lastPageNo + '); return false;\">맨 마지막</button>');
	})
	.catch(error => {
		
		console.error("fetch에러: ", error);
	})
}

function fetch_getElementList(pageNo) {
	
	if (pageNo <= 0 || pageNo == null) pageNo = 1;
	
	$('#elementSearch').val("")
	let searchKeyword = $('#elementSearch').val();
	let queryString = "pageIndex=" + pageNo + "&searchKeyword=" + searchKeyword;
	
	fetch("/common/getElementList?" + queryString)
	.then(response => {
		if (response.ok) return response.json();
		else throw new Error("네트워크 에러");
	})
	.then(data => {
		
		let modifyElementVos = data.modifyElementVos;
		let paginationInfo = data.paginationInfo;
		let prev = (paginationInfo.firstPageNoOnPageList == 1 ? 1 : (paginationInfo.currentPageNo - paginationInfo.pageSize < 1 ? 1 : paginationInfo.currentPageNo - paginationInfo.pageSize));
		let next = (paginationInfo.lastPageNoOnPageList == paginationInfo.lastPageNo ? paginationInfo.lastPageNo : (paginationInfo.currentPageNo + paginationInfo.pageSize > paginationInfo.lastPageNo ? paginationInfo.lastPageNo : paginationInfo.currentPageNo + paginationInfo.pageSize));
		
		$('#element_totalCnt').html("총 <strong>" + paginationInfo.totalRecordCount + "</strong>건의 게시물이 있습니다. <em>(" + paginationInfo.totalPageCount + "페이지)</em>");
		
		$("#pop_elementList").empty();
		modifyElementVos.forEach(modifyElementVo => {
			
			$("#pop_elementList").append('<tr><td style="text-align: center;">' + modifyElementVo.elementName + '</td><td class="ac"><input type="button" class="btn_addsq" onclick="selectElement(' + modifyElementVo.elementCode + ', \'' + modifyElementVo.elementName + '\'); return false;" style="width: 65px; cursor: pointer;" value="선택"></td></tr>');
		})
		
		$("#pop_pageBtn").empty();
		$("#pop_pageBtn").append('<button type=\"button\" class=\"btn_page_first\" title=\"맨앞 페이지\" onclick=\"fetch_getElementList(1); return false;\">맨 처음</button>');
		$("#pop_pageBtn").append('<button type=\"button\" class=\"btn_page_prev\" title=\"이전 페이지\" onclick=\"fetch_getElementList(' + prev + '); return false;\">이전</button>');
		
		for (let i = paginationInfo.firstPageNoOnPageList; i <= paginationInfo.lastPageNoOnPageList; i++) {
			
		  if (i == paginationInfo.currentPageNo) {
			  
			 $("#pop_pageBtn").append('<a class="ml2 mr2 current" href=\"#\" title=\"현재 ' + i + '페이지\" >' + i + '</a>');
			 
		  } else {
			  
			 $("#pop_pageBtn").append('<a class="ml2 mr2" href=\"#\" title=\"' + i + '페이지\" onclick=\"fetch_getElementList(' + i + '); return false;\">' + i + '</a>');
		  }
		}
		
		$("#pop_pageBtn").append('<button type=\"button\" class=\"btn_page_next\" title=\"다음 페이지\" onclick=\"fetch_getElementList(' + next + '); return false;\">다음</button>');
		$("#pop_pageBtn").append('<button type=\"button\" class=\"btn_page_last\" title=\"맨 마지막\" onclick=\"fetch_getElementList(' + paginationInfo.lastPageNo + '); return false;\">맨 마지막</button>');
	})
	.catch(error => {
		
		console.error("fetch에러: ", error);
	})
}

function selectElement(elementCode, elementName) {
	
	$('#selectedElement').val(elementCode);
	$('#selectedElementName').val(elementName);
	$('#popup_2').bPopup().close();
}

function reset() {
	
    $('#sido').val("");
    $('#sigungu').empty();
    $('#sigungu').append('<option value="">시/군/구 선택</option>');
	$('#breakerSearchInput').val("");
	$(".policyRadio").prop("checked", false);
	$("#selectedElement").val("");
    $("#dateStar").val("");
	$("#dateEnd").val("");
	
	fetch_getBreakerHistoryList();
}