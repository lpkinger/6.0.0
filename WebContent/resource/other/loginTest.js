function isChrome(){
	return /\bchrome\b/.test(navigator.userAgent.toLowerCase());
}
$(function() {
	//验证码动态显示
	var username = document.getElementById('username');
	var display =$('#checkcode').css('display');
	username.onblur = function(event){
		if($('#checkcode').css('display')=='none'){
			if(username.value!="" && username.value!=null){
				$.ajaxSetup({
			        async : false
			    });
				$.post(basePath + "common/countError.action",{
					em_code : $("#username").val(),
				},function(r){
					if(r.count>2){
						$("#checkcode").show();
					}
				})
			}
		}
	}
	username.onfocus = function(event){
		if($('#checkcode').css('display')=='none' && username_cookie!=null && username_cookie!=""){
			if(username.value!="" && username.value!=null){
				$.ajaxSetup({
			        async : false  
			    });
				$.post(basePath + "common/countError.action",{
					em_code : username_cookie
				},function(r){
					if(r.count>2){
						$("#checkcode").show();
					}
				})
			}
		}
	}
	//登入界面密码框大小写是否开启提示
	var inputPWD = document.getElementById('password');
	var capital = false;
	var first=1;
	var capitalTip = { 
	  elem:document.getElementById('capslocktpl'), 
	  toggle:function(s){ 
	  var sy = this.elem.style; 
	  var d = sy.display; 
	  if(s){ 
			  sy.display = s; 
		  }else{ 
			  sy.display = d =='none' ? '' : 'none'; 
			} 
		} 
	} 
	var detectCapsLock = function(event){
			var e = event||window.event; 
			var keyCode = e.keyCode||e.which; // 按键的keyCode 
			var isShift = e.shiftKey ||(keyCode == 16 ) || false ; // shift键是否按住 
			if ( 
				((keyCode >= 65 && keyCode <= 90 ) && !isShift) // Caps Lock 打开，且没有按住shift键 
				|| ((keyCode >= 97 && keyCode <= 122 ) && isShift)// Caps Lock 打开，且按住shift键 
			){
				capitalTip.toggle('block');capital=true
			 } else{
				 capitalTip.toggle('none');capital=false
			}
			first++;
	} 
	inputPWD.onkeypress = detectCapsLock; 
	inputPWD.onkeyup=function(event){
		if(first==1){
			return false;
		}else{
			var e = event||window.event; 
			if(e.keyCode == 20 /*&& capital*/){ 
				if(capital){
					capitalTip.toggle();
					capital=false;
				}else{
					capitalTip.toggle('block');
					capital=true;
				}
			}
		}
	
	}
	inputPWD.onblur=function(event){
		if(document.getElementById('capslocktpl').style.display){
			capitalTip.toggle();
		}
		first=2;
	}
	
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
	if(master_cookie1 != null && master_cookie1 != '') {
		$("#master1").val(master_cookie1);
	}
	if(master_cookie2 != null && master_cookie2 != '') {
		document.getElementById('master').innerHTML =master_cookie2;
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
var master_cookie1 = getCookie("master_name");
if (master_cookie1 == null || master_cookie1 == "null") {
	master_cookie1 = ""
}
var master_cookie2 = getCookie("master_fun");
if (master_cookie2 == null || master_cookie2 == "null") {
	master_cookie2 = ""
}
var _postflag = 0;
var _count = 0;
var rmbUser = getCookie('_rmb');
function login() {
	$.ajaxSetup({
        async : false  
    });
	var display =$('#checkcode').css('display')
	if(display!='none'){//验证码已经显示
		if ($("#validcode").val() == "") {
			$.showtip("请输入验证码!", 2500);
			refreshImg();
			$("#validcode").focus();
			return
		} else {
			$.post(basePath + "common/validCode.action", {
				code : $("#validcode").val()
			}, function(a) {
				if (a.success) {
					if ($("#username").val() == "") {
						$.showtip("请输入用户名!", 2500);
						refreshImg();
						$("#username").focus();
						return
					} else {
						if ($("#password").val() == "") {
							$.showtip("请输入密码!", 2500);
							refreshImg();
							$("#password").focus();
							return
						} else {
							$("#loading").show();
							$("#waitMsg").show();
							$("#loginBtn").text("登录中......");
							document.getElementById("loginBtn").disabled=true;
							var b = {};
							b.username = $("#username").val();
							b.password = encodeURIComponent($("#password").val());
							b.language = $(":radio:checked").val();
							b.sob = $("#master1").val();
							_postflag = 1;
							$.ajax({
								type : "POST",
								contentType : "application/json",
								url : basePath + "common/login.action?username="
										+ b.username + "&password=" + b.password
										+ "&language=" + b.language + "&sob="
										+ b.sob,
								success : function(c) {
									$("#loginBtn").text("登录");
									document.getElementById("loginBtn").disabled=false;
									_postflag = 0;
									if (c.success) {
										SetCookie("master_name", $("#master1").val());
										SetCookie("master_fun", $("#master").html());
										SetCookie("username", b.username);
										if (checked) {
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
											}else{
												$.showtip(c.reason, 6000);
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
						$.showtip("验证码错误!", 2500);
						refreshImg();
						$("#validcode").focus()
					}
				}
			})
		}
	}else{
		if ($("#username").val() == "") {
			$.showtip("请输入用户名!", 2500);
			refreshImg();
			$("#username").focus();
			return
		} else {
			if ($("#password").val() == "") {
				$.showtip("请输入密码!", 2500);
				refreshImg();
				$("#password").focus();
				return
			} else {
				$("#loading").show();
				$("#waitMsg").show();
				$("#loginBtn").text("登录中......");
				document.getElementById("loginBtn").disabled=true;
				var b = {};
				b.username = $("#username").val();
				b.password = encodeURIComponent($("#password").val());
				b.language = $(":radio:checked").val();
				b.sob = $("#master1").val();
				_postflag = 1;
				$.ajax({
					type : "POST",
					contentType : "application/json",
					url : basePath + "common/login.action?username="
							+ b.username + "&password=" + b.password
							+ "&language=" + b.language + "&sob="
							+ b.sob,
					success : function(c) {
						$("#loginBtn").text("登录");
						document.getElementById("loginBtn").disabled=false;
						_postflag = 0;
						if (c.success) {
							SetCookie("master_name", $("#master1").val());
							SetCookie("master_fun", $("#master").html());
							SetCookie("username", b.username);
							if (checked) {
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
								}else{
									$.showtip(c.reason, 6000);
								}
							} else {
								$.showtip(c.exceptionInfo, 6000)
							}
							$.post(basePath + "common/countError.action",{
								em_code : $("#username").val()
							},function(res){
								if(res.count>=3){
									$("#checkcode").show();
								}
							})
						}
					}
				})
		}
	}
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