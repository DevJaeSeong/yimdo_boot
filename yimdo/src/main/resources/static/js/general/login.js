$(function() {
    
    initEventListener();
})

function initEventListener() {

    $("#loginBtn").on("click", () => { doLogin(); });
    $("#createAccountBtn").on("click", () => { location.href = "/createAccountPage.do"; });
    
    $("#loginId, #loginPasswd").keypress(function(event) {
    	
	    if (event.which === 13) {
	    	
	    	doLogin();
	    }
    });
}

function doLogin() {
	
	showLoadingOverlay();
	
	let id = $('input[name="loginId"]').val();
	let pw = $('input[name="loginPasswd"]').val();
	
	if (!id) {
		
		alert("아이디를 입력해주세요.");
		hideLoadingOverlay();
		return;
	}
	
	if (!pw) {
		
		alert("비밀번호를 입력해주세요.");
		hideLoadingOverlay();
		return;
	}

    // 새로운 form 엘리먼트 생성
    const formElement = document.createElement('form');
    formElement.setAttribute('method', 'POST');
    formElement.setAttribute('action', '/loginConfirm');

    // CSRF 토큰을 폼에 추가
    const csrfToken = $("meta[name='_csrf']").attr("content");
    const csrfInput = document.createElement('input');
    csrfInput.setAttribute('type', 'hidden');
    csrfInput.setAttribute('name', '_csrf');
    csrfInput.setAttribute('value', csrfToken);
    formElement.appendChild(csrfInput);

    // ID와 PW를 폼에 추가
    const idInput = document.createElement('input');
    idInput.setAttribute('type', 'hidden');
    idInput.setAttribute('name', 'id');
    idInput.setAttribute('value', id);
    formElement.appendChild(idInput);

    const pwInput = document.createElement('input');
    pwInput.setAttribute('type', 'hidden');
    pwInput.setAttribute('name', 'pw');
    pwInput.setAttribute('value', pw);
    formElement.appendChild(pwInput);

    // body에 form 추가 후 submit
    document.body.appendChild(formElement);
    
    formElement.submit();
}