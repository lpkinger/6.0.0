function isChrome(){
	return /\bchrome\b/.test(navigator.userAgent.toLowerCase());
}
$(function() {
	if(!isChrome()){
		$('#toolBox').css('display', 'block');
		$('#toolBox .close').click(function(){
			$('#toolBox').css('display', 'none');
		});
	}
	$("#loading").hide();
	$("#waitMsg").hide();
	$("#username").focus();
	$("#username").val(username_cookie);
	$("#password").val(password_cookie);
	if(master_cookie != null && master_cookie != '') {
		$("#master").val(master_cookie);
	}
	if(rmbUser != null && rmbUser == 0) {
		$("#RmbUser").attr("checked", false);
	};

	/*//获取最新的Android客户端下载地址	
	$.ajax({
        url: basePath + 'common/cross.action',
        type: 'GET',
        data: {
            path: 'http://218.17.158.219:8001/artifactory/libs-release-local/com/uas/android/pm/maven-metadata.xml'
        },
        dataType: 'xml',
        success: function(data) {
            if (data) {
            	// latest version
                var version = $(data).find("metadata").find("versioning").find("latest").text();
                $('#qrcode').attr('src', basePath + 'mobile/qr/encode.action?code=http://218.17.158.219:8090/jsps/try.html?ios=ios&andriod='+version);
                //$('#qrcode').attr('src', basePath + 'mobile/qr/encode.action?code=http://218.17.158.219:8001/artifactory/libs-release-local/com/uas/android/pm/' + version + '/pm-' + version + '.apk');
            }
        }
    });*/


	$("#validcode").bind("keyup", function() {
		if ($("#validcode").val().length == 4) {
			setTimeout(function() {
				if ($("#validcode").val().length == 4 && _postflag == 0) {
					_postflag = 1;
					$.post(basePath + "common/validCode.action", {
						code : $("#validcode").val()
					}, function(a) {
						_postflag = 0;
						if (!a.success) {
							$.showtip("验证码错误!", 3000);
							refreshImg();
							$("#validcode").val("");
							$("#validcode").focus()
						}
					})
				}
			}, 300)
		} else {
			if ($("#validcode").val().length > 4) {
				$("#validcode").val($("#validcode").val().substring(0, 4))
			}
		}
	})
});
function  getUrlParam(name){   
    var reg=new RegExp("(^|&)"+name+"=([^&]*)(&|$)");   
    var r=window.location.search.substr(1).match(reg);   
    if  (r!=null)   return decodeURI(r[2]); 
    return   null;   
}
var username_cookie = getCookie("username");
if (username_cookie == null || username_cookie == "null") {
	username_cookie = ""
}
var password_cookie = getCookie("password");
if (password_cookie == null || password_cookie == "null") {
	password_cookie = ""
}
var master_cookie = getCookie("master");
var _postflag = 0;
var _count = 0;
var rmbUser = getCookie('_rmb');
function login() {
	if ($("#validcode").val() == "") {
		$.showtip("请输入验证码!", 3000);
		refreshImg();
		$("#validcode").focus();
		return
	} else {
		$.post(basePath + "common/validCode.action", {
			code : $("#validcode").val()
		}, function(a) {
			if (a.success) {
				if ($("#username").val() == "") {
					$.showtip("请输入用户名!", 3000);
					refreshImg();
					$("#username").focus();
					return
				} else {
					if ($("#password").val() == "") {
						$.showtip("请输入密码!", 3000);
						refreshImg();
						$("#password").focus();
						return
					} else {
						$("#loading").show();
						$("#waitMsg").show();
						var b = {};
						b.username = $("#username").val();
						b.password = $("#password").val();
						b.language = $(":radio:checked").val();
						_postflag = 1;
						$.ajax({
							type : "POST",
							contentType : "application/json",
							url : basePath + "common/login.action?username="
									+ b.username + "&password=" + b.password
									+ "&language=" + b.language + "&sob="
									+ $("#master")[0].value,
							success : function(c) {
								$("#loading").hide();
								$("#waitMsg").hide();
								_postflag = 0;
								if (c.success) {
									SetCookie("master", $("#master")[0].value);
									SetCookie("username", b.username);
									if ($("#RmbUser").attr("checked")) {
										delCookie('_rmb');
										SetCookie("password", b.password)
									} else {
										SetCookie("_rmb", '0');
										SetCookie("password", "")
									}
									SetCookie("language", b.language);
									var path = basePath, mobile = getUrlParam("mobile");
									if(mobile != null) {
										path += "?mobile=0";
									}
									document.location.href = path;
								} else {
									if (c.reason) {
										if('未设置密码' == c.reason) {
											showPwdDialog();
										} else {
											$.showtip(c.reason, 6000);
											_count += 1;
											if(_count > 5) {
												$.showtip("错误5次以上,您的页面将会在3秒后关闭!", 6000);
												setTimeout(function(){
													window.opener = null;
													window.open('', '_self');
													window.close();
												}, 3000);
											} 			
										}
									} else {
										$.showtip(c.exceptionInfo, 6000)
									}
								}
							}
						})
					}
				}
			} else {
				if (a.ex) {
					$.showtip(a.ex, 8000)
				} else {
					$.showtip("验证码错误!", 3000);
					refreshImg();
					$("#validcode").focus()
				}
			}
		})
	}
}
function SetCookie(c, a) {
	var d = 30;
	var b = new Date();
	b.setTime(b.getTime() + d * 24 * 60 * 60 * 1000);
	document.cookie = c + "=" + escape(a) + ";expires=" + b.toGMTString()
}
function getCookie(b) {
	var a = document.cookie.match(new RegExp("(^| )" + b + "=([^;]*)(;|$)"));
	if (a != null) {
		return unescape(a[2])
	}
	return null
}
function delCookie(b) {
	var a = new Date();
	a.setTime(a.getTime() - 1);
	var c = getCookie(b);
	if (c != null) {
		document.cookie = b + "=" + c + ";expires=" + a.toGMTString()
	}
}
function keyDown(b) {
	var a = (navigator.appName == "Netscape") ? b.which : b.keyCode;
	if (a == 13) {
		login()
	}
};

function showPwdDialog() {
	// TODO
}