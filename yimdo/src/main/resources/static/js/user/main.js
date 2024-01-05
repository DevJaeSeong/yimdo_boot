function logout() {
	
	const csrfToken = $("meta[name='_csrf']").attr("content");
	
	const form = $('<form>', {
	
        'action': '/logoutConfirm'
        , 'method': 'POST'
        , 'style': 'display: none;'
	});
	
	form.append('<input type="hidden" name="_csrf" value="' + csrfToken + '">');
	$('body').append(form);
	form.submit();
}