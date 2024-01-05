$(function() {
	
	fetch_getSido();
	fetch_getYimdoList();
})

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

function fetch_getYimdoList(pageNo) {
	
    $("#breakerSearchInput").val("");
    fetch_getBreakerList();

	if (pageNo <= 0 || pageNo == null) pageNo = 1;
	
	let sido = $('#sido').val();
	if (sido == undefined) sido = "";
	let sigungu = $('#sigungu').val();
	if (sigungu == undefined) sigungu = "";
	let searchKeyword = $('#yimdoSearchInput').val();
	
	let queryString = "pageIndex=" + pageNo + 
					  "&searchKeyword=" + searchKeyword +
					  "&sido=" + sido +
					  "&sigungu=" + sigungu;
	
	fetch("/common/getYimdoList?" + queryString)
	.then(response => {
		if (response.ok) return response.json();
		else throw new Error("네트워크 에러");
	})
	.then(data => {
		
		let yimdoInfoVos = data.yimdoInfoVos;
		let paginationInfo = data.paginationInfo;
		let prev = (paginationInfo.firstPageNoOnPageList == 1 ? 1 : (paginationInfo.currentPageNo - paginationInfo.pageSize < 1 ? 1 : paginationInfo.currentPageNo - paginationInfo.pageSize));
		let next = (paginationInfo.lastPageNoOnPageList == paginationInfo.lastPageNo ? paginationInfo.lastPageNo : (paginationInfo.currentPageNo + paginationInfo.pageSize > paginationInfo.lastPageNo ? paginationInfo.lastPageNo : paginationInfo.currentPageNo + paginationInfo.pageSize));
		
		$('#yimdoTotalCount').html("<span class='data_num'>총 <strong>" + paginationInfo.totalRecordCount + "</strong>건의 게시물이 있습니다. <em>(" + paginationInfo.totalPageCount + "페이지)</em></span>");
		
		$('#yimdoList').empty();
		yimdoInfoVos.forEach(yimdoInfoVo => {
			
			$('#yimdoList').append('<tr><td><a style="cursor: pointer;" onclick="set_selectedYimdo(' + yimdoInfoVo.yimdoCode + '); return false;">' + yimdoInfoVo.yimdoName + '</a></td><td>' + yimdoInfoVo.yimdoAddress + '</td> <td>' + yimdoInfoVo.yimdoDistance + '</td><td>' + yimdoInfoVo.yimdoSidoName + '</td><td>' + yimdoInfoVo.yimdoGunguName + '</td><td>' + yimdoInfoVo.yimdoDetail + '</td></tr>');
		})
		
		$("#yimdoListPaging").empty();
		
		$("#yimdoListPaging").append('<button type=\"button\" class=\"btn_page_first\" title=\"맨앞 페이지\" onclick=\"fetch_getYimdoList(1);\">맨 처음</button>');
		$("#yimdoListPaging").append('<button type=\"button\" class=\"btn_page_prev\" title=\"이전 페이지\" onclick=\"fetch_getYimdoList(' + prev + ');\">이전</button>');

		for (let i = paginationInfo.firstPageNoOnPageList; i <= paginationInfo.lastPageNoOnPageList; i++) {
			
		  if (i == paginationInfo.currentPageNo) {
			  
			 $("#yimdoListPaging").append('<a class="ml2 mr2 current" href=\"#\" title=\"현재 ' + i + '페이지\" >' + i + '</a>');
			 
		  } else {
			  
			 $("#yimdoListPaging").append('<a class="ml2 mr2" href=\"#\" title=\"' + i + '페이지\" onclick=\"fetch_getYimdoList(' + i + ');\">' + i + '</a>');
		  }
		}
		
		$("#yimdoListPaging").append('<button type=\"button\" class=\"btn_page_next\" title=\"다음 페이지\" onclick=\"fetch_getYimdoList(' + next + ');\">다음</button>');
		$("#yimdoListPaging").append('<button type=\"button\" class=\"btn_page_last\" title=\"맨 마지막\" onclick=\"fetch_getYimdoList(' + paginationInfo.lastPageNo + ');\">맨 마지막</button>');
	})
	.catch(error => {
		
		console.error("fetch에러: ", error);
	})
	
}

function set_selectedYimdo(yimdoCode) {
	
	$('#selectedYimdo').val(yimdoCode);
    $('#breakerSearchOption').val('');
    $('#breakerSearchInput').val('');
	fetch_getBreakerList();
}

function searchBreakerList() {

    let searchCondition = $('#breakerSearchOption').val();
    if (searchCondition == "" || searchCondition == null) {

        alert('검색 항목을 선택해주세요');
        return;

    } else {

        fetch_getBreakerList();
    }
}

function fetch_getBreakerList(pageNo) {

	fetch_getBreakerListEachStatusCount();
	
	if (pageNo <= 0 || pageNo == null) pageNo = 1;
    let sido = $('#sido').val(); if (sido == undefined) sido = "";
    let sigungu = $('#sigungu').val(); if (sigungu == undefined) sigungu = "";

	let yimdoCode = $('#selectedYimdo').val();
    let searchCondition = $('#breakerSearchOption').val();
	let searchKeyword = $('#breakerSearchInput').val();
	let queryString = "pageIndex=" + pageNo + 
					  "&yimdoCode=" + yimdoCode +
					  "&sido=" + sido +
					  "&sigungu=" + sigungu +
					  "&searchCondition=" + searchCondition +
					  "&searchKeyword=" + searchKeyword;
	
	fetch("/common/getBreakerList?" + queryString)
	.then(response => {
		if (response.ok) return response.json();
		else throw new Error("네트워크 에러");
	})
	.then(data => {
		
		let breakerInfoVos = data.breakerInfoVos;
		let paginationInfo = data.paginationInfo;
		let prev = (paginationInfo.firstPageNoOnPageList == 1 ? 1 : (paginationInfo.currentPageNo - paginationInfo.pageSize < 1 ? 1 : paginationInfo.currentPageNo - paginationInfo.pageSize));
		let next = (paginationInfo.lastPageNoOnPageList == paginationInfo.lastPageNo ? paginationInfo.lastPageNo : (paginationInfo.currentPageNo + paginationInfo.pageSize > paginationInfo.lastPageNo ? paginationInfo.lastPageNo : paginationInfo.currentPageNo + paginationInfo.pageSize));
		
		$('#breakerTotalCount').html("<span class='data_num'>총 <strong>" + paginationInfo.totalRecordCount + "</strong>건의 게시물이 있습니다. <em>(" + paginationInfo.totalPageCount + "페이지)</em></span>");
		
		$('#breakerList').empty();
		breakerInfoVos.forEach(breakerInfoVo => {
			
			let breakerStatusDeco;
            let breakerPolicyDeco;
			
			switch (breakerInfoVo.breakerPolicyCode) {
			
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

			switch (breakerInfoVo.breakerStatusCode) {

			  case "01":
				breakerStatusDeco = "status_value01";
			    break;
			    
			  case "02":
				  breakerStatusDeco = "status_value05";
			    break;
			  
			  case "03":
				  breakerStatusDeco = "status_value02";
			    break;
			    
			  case "04":
				  breakerStatusDeco = "status_value04";
			    break;
			  
			  case "05":
				  breakerStatusDeco = "status_value03";
			    break;
			  
			  default:
				  breakerStatusDeco = "status_value03";
			    break;
			}
			
            if (breakerPolicyDeco == breakerStatusDeco) {

                $('#breakerList').append('<tr><td><ul class="checkboL"><li><div class="checkboX"><input class="breakerCheckBox" type="checkbox" value="' + breakerInfoVo.breakerId + '" id="' + breakerInfoVo.breakerId + '" name="' + breakerInfoVo.breakerId + '"><label for="' + breakerInfoVo.breakerId + '"></label></div></li></ul></td><td>' + breakerInfoVo.breakerId + '</td><td>' + breakerInfoVo.yimdoName + '</td><td><span class="' + breakerPolicyDeco + '">' + breakerInfoVo.breakerPolicyName + '</td><td><span class="' + breakerStatusDeco + '">' + breakerInfoVo.breakerStatusName + '</span></td><td>' + breakerInfoVo.modifier + '</td><td>' + breakerInfoVo.modifyDate + '</td><td>' + breakerInfoVo.elementName + '</span></td><td></td></tr>');
            
            } else {

                $('#breakerList').append('<tr><td><ul class="checkboL"><li><div class="checkboX"><input class="breakerCheckBox" type="checkbox" value="' + breakerInfoVo.breakerId + '" id="' + breakerInfoVo.breakerId + '" name="' + breakerInfoVo.breakerId + '"><label for="' + breakerInfoVo.breakerId + '"></label></div></li></ul></td><td>' + breakerInfoVo.breakerId + '</td><td>' + breakerInfoVo.yimdoName + '</td><td><span class="' + breakerPolicyDeco + '">' + breakerInfoVo.breakerPolicyName + '</td><td><span class="' + breakerStatusDeco + '">' + breakerInfoVo.breakerStatusName + '</span></td><td>' + breakerInfoVo.modifier + '</td><td>' + breakerInfoVo.modifyDate + '</td><td>' + breakerInfoVo.elementName + '</span></td><td>차단기 정책과 상태가 일치하지 않습니다.</td></tr>');
            }

		})
		
		$("#breakerListPaging").empty();
		
		$("#breakerListPaging").append('<button type=\"button\" class=\"btn_page_first\" title=\"맨앞 페이지\" onclick=\"fetch_getBreakerList(1); return false;\">맨 처음</button>');
		$("#breakerListPaging").append('<button type=\"button\" class=\"btn_page_prev\" title=\"이전 페이지\" onclick=\"fetch_getBreakerList(' + prev + '); return false;\">이전</button>');

		for (let i = paginationInfo.firstPageNoOnPageList; i <= paginationInfo.lastPageNoOnPageList; i++) {
			
		  if (i == paginationInfo.currentPageNo) {
			  
			 $("#breakerListPaging").append('<a class="ml2 mr2 current" href=\"#\" title=\"현재 ' + i + '페이지\" >' + i + '</a>');
			 
		  } else {
			  
			 $("#breakerListPaging").append('<a class="ml2 mr2" href=\"#\" title=\"' + i + '페이지\" onclick=\"fetch_getBreakerList(' + i + '); return false;\">' + i + '</a>');
		  }
		}
		
		$("#breakerListPaging").append('<button type=\"button\" class=\"btn_page_next\" title=\"다음 페이지\" onclick=\"fetch_getBreakerList(' + next + '); return false;\">다음</button>');
		$("#breakerListPaging").append('<button type=\"button\" class=\"btn_page_last\" title=\"맨 마지막\" onclick=\"fetch_getBreakerList(' + paginationInfo.lastPageNo + '); return false;\">맨 마지막</button>');
		
	})
	.catch(error => {
		
		console.error("fetch에러: ", error);
	})
}

function fetch_getBreakerListEachStatusCount() {
	
    let sido = $('#sido').val();
    if (sido == undefined) sido = "";
    let sigungu = $('#sigungu').val();
    if (sigungu == undefined) sigungu = "";
	let yimdoCode = $('#selectedYimdo').val();
	let searchKeyword = $('#breakerSearchInput').val()
	let queryString = "yimdoCode=" + yimdoCode +
                      "&sido=" + sido +
                      "&sigungu=" + sigungu +
					  "&searchKeyword=" + searchKeyword;
	
	fetch("/admin/breakerStatus/getBreakerListEachStatusCount?" + queryString)
	.then(response => {
		if (response.ok) return response.json();
		else throw new Error("네트워크 에러");
	})
	.then(statusCounts => {

		if (statusCounts == null) {
			
			breakerReset();
			return;
		}
        
        $("#breakerCount_01").text(statusCounts["01"] + "건");
        $("#breakerCount_02").text(statusCounts["02"] + "건");
        $("#breakerCount_03").text(statusCounts["03"] + "건");
        $("#breakerCount_04").text(statusCounts["04"] + "건");
        $("#breakerCount_05").text(statusCounts["05"] + "건");
	})
	.catch(error => {
		
		breakerReset();
		console.error("fetch에러: ", error);
	})
}

function breakerReset() {
	
	$('#breakerTotalCount').html("<span class='data_num'>총 <strong> 0</strong>건의 게시물이 있습니다. <em>(1페이지)</em></span>");
	$("#breakerCount_01").text("0건");
	$("#breakerCount_02").text("0건");
	$("#breakerCount_03").text("0건");
	$("#breakerCount_04").text("0건");
	$("#breakerCount_05").text("0건");
	$('#breakerList').empty();
	$("#breakerListPaging").empty();
}

function checkAll() {
	
	if ($("#allCheckBox").prop('checked')) {
		
	  $('.breakerCheckBox').prop('checked', true);
	  
	} else {
		
	  $('.breakerCheckBox').prop('checked', false);
	}
}

function fetch_getElementList(pageNo) {
	
	if (pageNo <= 0 || pageNo == null) pageNo = 1;
	
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
			
			$("#pop_elementList").append('<tr><td style="text-align: center;">' + modifyElementVo.elementName + '</td><td class="ac"><input type="button" class="btn_addsq" onclick="selectElement(\'' + modifyElementVo.elementCode + '\', \'' + modifyElementVo.elementName + '\'); return false;" style="width: 65px; cursor: pointer;" value="선택"></td></tr>');
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

function fetch_updateBreakerStatus() {
	
	let selectedBreakers = [];
	let selectedBoxes = $(".breakerCheckBox").filter(':checked');
	if (selectedBoxes.length <= 0) {
		
		alert("변경할 차단기를 선택해주세요.");
		return;
	}
	selectedBoxes.each(function() {
		
		selectedBreakers.push($(this).attr("id"));
	})
	
	let selectedElement = $("#selectedElement").val();
	if (selectedElement == "" || selectedElement == null) {
		
		alert("상태변경인자를 선택해주세요.");
		return;
	}
	
	let modifier = "관리자";
	let modifyDetail = $("#modifyDetail").val();
	let selectedPolicy = $(".policyRadio").filter(':checked:first').val();
	let systemControl = 'n';

	if (selectedPolicy == "1001" || selectedPolicy == "1002") {
		
		systemControl = 'y'
		//modifyDetail = "관리자에 의한 강제 정책 해제.";
	}
	
	let msg = {
		"selectedBreakers": selectedBreakers,
		"selectedElement": selectedElement,
		"selectedPolicy": selectedPolicy,
		"modifyDetail": modifyDetail,
        "modifier": modifier,
		"systemControl": systemControl
	}

	fetch("/admin/breakerStatus/updateBreakerStatus", {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
			"${_csrf.headerName}": "${_csrf.token}"
		},
		body: JSON.stringify(msg)
	})
	.then(response => {
		if (response.ok) return response.json();
		else throw new Error("네트워크 에러");
	})
	.then(data => {
		
		let breakerListCurrentPage = $("#breakerListCurrentPage").data("breakerlistcurrentpage");
		fetch_getBreakerList(breakerListCurrentPage);

        alert("적용되었습니다.");
        console.log(data);
	})
	.catch(error => {
		
		console.error("fetch에러: ", error);
	})
}