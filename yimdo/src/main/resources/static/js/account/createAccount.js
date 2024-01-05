$(function() {
    
    initEventListener();
    //setTestAccount();
})

function initEventListener() {

    $('#createAccountBtn').on('click', function() { doCreateAccount(); });
    $('#inputWrap input').on('input', function() { validateAccountInfo($(this).attr('id')); }); 
}

function setTestAccount() {
	
	$('#confrimedAccountInfoForm input[name="userId"]').val('smart1');
    $('#confrimedAccountInfoForm input[name="password"]').val('Smart!123');
    $('#confrimedAccountInfoForm input[name="passwordConfirm"]').val('Smart!123');
    $('#confrimedAccountInfoForm input[name="userNm"]').val('테스트A');
    $('#confrimedAccountInfoForm input[name="mbtlNum"]').val('010-1111-1111');
    $('#confrimedAccountInfoForm input[name="email"]').val('test@gmail.com');
    $('#confrimedAccountInfoForm input[name="carNumber"]').val('123가1234');
    $('#confrimedAccountInfoForm input[name="affiliation"]').val('테스트A');
}

function doCreateAccount() {

    showLoadingOverlay();

    let userId = $('#confrimedAccountInfoForm input[name="userId"]').val();
    let password = $('#confrimedAccountInfoForm input[name="password"]').val();
    let passwordConfirm = $('#confrimedAccountInfoForm input[name="passwordConfirm"]').val();
    let userNm = $('#confrimedAccountInfoForm input[name="userNm"]').val();
    let mbtlNum = $('#confrimedAccountInfoForm input[name="mbtlNum"]').val();
    let email = $('#confrimedAccountInfoForm input[name="email"]').val();
    let carNumber = $('#confrimedAccountInfoForm input[name="carNumber"]').val();
    let affiliation = $('#confrimedAccountInfoForm input[name="affiliation"]').val();

    if (userId == '' ||
        password == '' ||
        passwordConfirm == '' ||
        userNm == '' ||
        mbtlNum == '' ||
        email == '' ||
        carNumber == '' ||
        affiliation == '') {

        alert('모든 입력란을 조건에 맞게 입력해주세요.');
        hideLoadingOverlay();
        return;
    }

    let data = {
        'userId': userId,
        'password': password,
        'userNm': userNm,
        'mbtlNum': mbtlNum,
        'email': email,
        'carNumber': carNumber,
        'affiliation': affiliation
    }

	const csrfToken = $("meta[name='_csrf']").attr("content");

    fetch("/createAccountConfirm", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "X-CSRF-Token": csrfToken
        },
        body: JSON.stringify(data)
    })
    .then(response => {
        if (response.ok) return response.json();
        else throw new Error("네트워크 에러");
    })
    .then(data => {
        
        if (data.result == 'success') {

            alert('계정 생성에 성공하였습니다.');

        } else if (data.result == 'already_exists') {

            alert('이미 존제하는 계정입니다.');

        } else {

            alert('계정생성에 실패하였습니다. 다시시도해주세요.');
        }
    })
    .catch(error => {
        
        alert('서버 통신간 문제가 발생하였습니다. 잠시후 다시 시도해주세요.');
        console.error("fetch에러: ", error);
    })
    .finally(() => {

        hideLoadingOverlay();
    })
}

function validateAccountInfo(inputId) {

    switch (inputId) {

        case 'userId': {

                $('#userId').val($('#userId').val().replace(/[^a-zA-Z0-9]/g, ''));

                let userId = $('#userId').val();
                let idRegex = RegExp(/^[a-zA-Z0-9]{4,16}$/);

                if (idRegex.test(userId)) {

                    $('#userIdError').text('');
                    $('#confrimedAccountInfoForm input[name="userId"]').val(userId);

                } else {
                    
                    $('#userIdError').text('영문, 숫자로만 구성되고 길이가 4~16 사이의 값으로 입력해주세요.');
                    $('#confrimedAccountInfoForm input[name="userId"]').val('');
                }
            }
            break;

        case 'password': {

                let password = $('#password').val();
                let passwordRegex = RegExp(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*#?&])[a-zA-Z\d@$!%*#?&]{8,16}$/);
                
                if (passwordRegex.test(password)) {

                    $('#passwordError').text('');
                    $('#confrimedAccountInfoForm input[name="password"]').val(password);

                } else {
                    
                    $('#passwordError').text('영어 대소문자, 숫자, 특수기호가 최소 각각 1회씩 들어가있고 길이가 8~16자 이내로 입력해주세요.');
                    $('#confrimedAccountInfoForm input[name="password"]').val('');
                }
            }
            break;
        
        case 'passwordConfirm': {

                let password = $('#password').val();
                let passwordConfirm = $('#passwordConfirm').val();

                if (password == passwordConfirm) {

                    $('#passwordConfirmError').text('');
                    $('#confrimedAccountInfoForm input[name="passwordConfirm"]').val(passwordConfirm);

                } else {
                    
                    $('#passwordConfirmError').text('입력한 비밀번호와 일치하지않습니다.');
                    $('#confrimedAccountInfoForm input[name="passwordConfirm"]').val('');
                }
            }
            break;

        case 'userNm': {

                $('#userNm').val($('#userNm').val().replace(/[^a-zA-Zㄱ-ㅎ가-힣]/g, ''));

                let userNm = $('#userNm').val();
                let userNmRegex = RegExp(/^[가-힣a-zA-Z]{2,}$/);

                if (userNmRegex.test(userNm)) {

                    $('#userNmError').text('');
                    $('#confrimedAccountInfoForm input[name="userNm"]').val(userNm);

                } else {
                    
                    $('#userNmError').text('이름은 길이가 2~20자 이내의 한글, 영문으로만 가능합니다.');
                    $('#confrimedAccountInfoForm input[name="userNm"]').val('');
                }
            }
            break; 

        case 'mbtlNum': {

                let value = $('#mbtlNum').val().replace(/[^0-9]/g, '');
                let formattedValue = '';

                if (value.length > 3) {
                    
                    formattedValue += value.substring(0, 3) + '-';
                    
                    if (value.length > 7) {
                        
                        formattedValue += value.substring(3, 7) + '-';
                        formattedValue += value.substring(7, 11);
                        
                    } else {
                        
                        formattedValue += value.substring(3, value.length);
                    }
                    
                } else {
                    
                    formattedValue = value;
                }
                
                $('#mbtlNum').val(formattedValue);
            
                let mbtlNum = $('#mbtlNum').val();
                let mbtlNumRegex = RegExp(/^\d{3}-\d{3,4}-\d{4}$/);

                if (mbtlNumRegex.test(mbtlNum)) {

                    $('#mbtlNumError').text('');
                    $('#confrimedAccountInfoForm input[name="mbtlNum"]').val(mbtlNum);

                } else {
                    
                    $('#mbtlNumError').text('휴대폰 번호양식에 맞게 입력해주세요.');
                    $('#confrimedAccountInfoForm input[name="mbtlNum"]').val('');
                }
            }
            break; 

        case 'email': {

                let email = $('#email').val();
                let emailRegex = RegExp(/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,100}$/);

                if (emailRegex.test(email)) {

                    $('#emailError').text('');
                    $('#confrimedAccountInfoForm input[name="email"]').val(email);

                } else {
                    
                    $('#emailError').text('이메일 형식에 맞게 입력해주세요.');
                    $('#confrimedAccountInfoForm input[name="email"]').val('');
                }
            }
            break; 

        case 'carNumber': {

                let carNumber = $('#carNumber').val();
                let carNumberRegex = RegExp(/^(?=.*[가-힣])(?=.*[0-9])[가-힣0-9]{1,50}$/);

                if (carNumberRegex.test(carNumber)) {

                    $('#carNumberError').text('');
                    $('#confrimedAccountInfoForm input[name="carNumber"]').val(carNumber);

                } else {
                    
                    $('#carNumberError').text('자동차번호를 정확하게 입력해주세요.');
                    $('#confrimedAccountInfoForm input[name="carNumber"]').val('');
                }
            }
            break;

        case 'affiliation': {

                $('#affiliation').val($('#affiliation').val().replace(/[^a-zA-Zㄱ-ㅎ가-힣]/g, ''));

                let affiliation = $('#affiliation').val();
                let affiliationRegex = RegExp(/^[가-힣a-zA-Z]{1,50}$/);

                if (affiliationRegex.test(affiliation)) {

                    $('#affiliationError').text('');
                    $('#confrimedAccountInfoForm input[name="affiliation"]').val(affiliation);

                } else {
                    
                    $('#affiliationError').text('50자 이내 한글, 영어로만 입력해주세요.');
                    $('#confrimedAccountInfoForm input[name="affiliation"]').val('');
                }
            }
            break; 

        default:
            break;
    }
}