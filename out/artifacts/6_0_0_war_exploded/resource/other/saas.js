(function($) {
	$("#s_username").focus();
	if (typeof $.cookie == 'undefined') {
		$.cookie = {};
		$.cookie.set = function(c, a, d) {
			d = d || 30;
			var b = new Date();
			b.setTime(b.getTime() + d * 24 * 60 * 60 * 1000);
			document.cookie = c + "=" + escape(a) + ";expires=" + b.toGMTString()
		};
		$.cookie.get = function(b) {
			var a = document.cookie.match(new RegExp("(^| )" + b + "=([^;]*)(;|$)"));
			if (a != null) {
				return unescape(a[2])
			}
			return null
		};
		$.cookie.del = function(b) {
			$.cookie.set(b, 1, -1);
		};
	}
	if (typeof $.location == 'undefined') {
		$.location = function(name) {
			var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
			var r = window.location.search.substr(1).match(reg);
			if (r != null)
				return decodeURI(r[2]);
			return null;
		};
	}
	var _username = $.cookie.get('s_username'), _password = $.cookie.get('s_password'), _remember = $.cookie
			.get('s_remember');
	if (_username && _username != '')
		$('#s_username').val(_username);
	$('#s_password').val(_password);
	if (_remember != null && _remember == 0) {
		$('#remember').attr('checked', false);
	}
	window.basePath = (function() {
		var fullPath = window.document.location.href;
		var path = window.document.location.pathname;
		var subpos = fullPath.indexOf('//');
		var subpath = subpos > -1 ? fullPath.substring(0, subpos + 2) : '';
		if (subpos > -1)
			fullPath = fullPath.substring(subpos + 2);
		var pos = fullPath.indexOf(path);
		return subpath + fullPath.substring(0, pos) + path.substring(0, path.substr(1).indexOf('/') + 1) + "/";
	})();
	(function() {
		if(typeof basePath == 'undefined')
			return;
		$.ajax({
			type : "GET",
			contentType : "application/json",
			url : basePath + "common/saas/master.action?basePath=" + basePath,
			success : function(c) {
				setLoading(false);
				if(c && c.ma_name) {
					if(!c.enable)
						window.location.href = basePath + "common/saas/disable.action";
					else {
						window._init = c.init;
						window.sob = c.ma_name;
						$('#en-info h1').text(c.ma_function);
						// 演示模式，自动登录
						if('guest' == c.type && c.tempName) {
							$.cookie.set("s_username_" + c.ma_name, c.tempName);
							$('#login-wrap').addClass('hide');
							$('#guest-wrap').addClass('slidein');
							var page = $.location("_page");
							if(!page || page != 'logout') {
								login(c.tempName, '1');
							}
						}
					}
				} else
					window.location.href = basePath + "common/saas/error.action";
			}
		});
	})();
	$('#resetPwdLink').click(redirectResetPwd);
	$(window).keydown(function(event){
		event.stopPropagation();
		// 回车登录
		if (event.keyCode == 13) {
			login();
		}
	});
})(jQuery);
function login(username, password) {
	username = username || $("#s_username").val();
	password = password || $("#s_password").val();
	if (!username) {
		$.showtip("请输入用户名!", 3000);
		$("#s_username").focus();
	} else if (!password) {
		$.showtip("请输入密码!", 3000);
		$("#s_password").focus();
	} else {
		setLoading(true, '登录中...');
	}
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : basePath + "common/login.action?username=" + username + "&password=" + password + "&sob=" + sob,
		success : function(c) {
			setLoading(false);
			if (c.success) {
				$.cookie.set("s_username", username);
				if ($("#remember").attr("checked")) {
					$.cookie.del('s_remember');
					$.cookie.set("s_password", password);
				} else {
					$.cookie.set("s_remember", '0');
					$.cookie.del("s_password");
				}
				var path = basePath, mobile = $.location("mobile");
				/*if(!window._init) {
					path += 'system/init.action';
				}*/
				if (mobile != null) {
					path += "?mobile=0";
				}
				document.location.href = path;
			} else {
				if (c.reason) {
					if('未设置密码' == c.reason) {
						showPwdDialog();
					} else {
						$.showtip(c.reason, 6000);						
					}
				} else {
					$.showtip(c.exceptionInfo, 6000);
				}
			}
		}
	});
}
function keyDown(b) {
	var a = (navigator.appName == "Netscape") ? b.which : b.keyCode;
	if (a == 13) {
		login()
	}
}
// loading
function setLoading(isLoading, loadingText) {
	$('.loading-container').css('display', isLoading ? 'block' : 'none');
	$('.loading-back').css('display', isLoading ? 'block' : 'none');
	if(isLoading) {
		$('.loading-container').text(loadingText);
	}
};

function showPwdDialog() {
	var dialog = $('#pwdDialog'), modal = dialog.find('.modal'), back = dialog.find('.modal-backdrop');
	dialog.show();
	back.addClass('in');
	modal.addClass('in').find('.btn-default').click(function(){
		modal.removeClass('in');
		back.removeClass('in');
		dialog.hide();
	});
	modal.find('.btn-primary').click(redirectResetPwd);
}

function redirectResetPwd() {
	var retUrl = encodeURIComponent(window.location.href);
	window.location.href = 'https://sso.ubtob.com/reset/forgetPasswordValidationAccount?appId=saas&returnURL=' + retUrl;
}