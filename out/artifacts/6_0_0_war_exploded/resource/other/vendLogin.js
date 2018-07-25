function keyDown(b) {
	var a = (navigator.appName == "Netscape") ? b.which : b.keyCode;
	if (a == 13) {
		login()
	}
};

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
function showVendLoginDiv(isSessionOut){
	if(!Ext.getCmp('login')){
		var title = '重&nbsp;新&nbsp;登&nbsp;录';
		if(isSessionOut){
			title = '会话过期,请重新登录';
		}
		Ext.create('erp.view.vendbarcode.main.vendReLogin', {
			isSessionOut: isSessionOut,
			title: title,
			id: 'login'
		});
	} else {
		Ext.getCmp('login').show();
		Ext.getCmp('login').refresh();
	}
}
function delCookie(b) {
	var a = new Date();
	a.setTime(a.getTime() - 1);
	var c = getCookie(b);
	if (c != null) {
		document.cookie = b + "=" + c + ";expires=" + a.toGMTString()
	}
}
var checked=true;
/* $(function(){
	var $x = $(window).height()
	var $h = $x*0.68;
	$('.container').height($h);
}) */
function getMa(){
	$.ajax({
		type : "GET",
		url : basePath+"vendbarcode/getAllMasters.action",
		success : function(c) {
			if (c.success) {
				var rmbUser = getCookie('_rmb');
				if(rmbUser != null && rmbUser == 0) {
					$("#RmbUser").attr("checked", false);
				}else{
					$("#username").val(getCookie("username"));
					$("#password").val(getCookie("password"));
				}
				var json=eval(c)["masters"];
	            var list="";
				$.each(json, function (index, item) {  
	                var Name = json[index].ma_name;
	                var func = json[index].ma_function;
	                if(index==0){
						$("#master1").val(Name);
						document.getElementById('master').innerHTML = func;
					}
	                $("#box").append("<li role='"+"presentation"+"'><a  role='"+"menuitem"+"' tabindex='"+"-1"+"' align=left value='"+Name+"' style='"+"cursor: pointer;margin:0px 32px"+"' onclick='changeMaster(this)'>"+func+"</a></li>");
	            });
				if(getCookie("master_fun")!=null){
					document.getElementById('master').innerHTML = getCookie("master_fun");
					$("#master1").val(getCookie("master_name"));
				}
			} else {
			}
		}
	})
}
function checkboxOnclick(checkbox){
	if (checkbox.checked == true){
		checked=true;
	}else{
		checked=false;
	} 
	console.log(checkbox.checked);
}
function changeMaster(f){
	$("#master1").val(f.getAttribute('value'));
	document.getElementById('master').innerHTML = f.innerHTML;
}
var basePath = (function() {
	var fullPath = window.document.location.href;
	var path = window.document.location.pathname;
	var subpos = fullPath.indexOf('//');
	var subpath = subpos > -1 ? fullPath.substring(0, subpos + 2) : '';
	if (subpos > -1)
		fullPath = fullPath.substring(subpos + 2);
	var pos = fullPath.indexOf(path);
	return subpath + fullPath.substring(0, pos) + path.substring(0, path.substr(1).indexOf('/') + 1) + '/';
})();
function showMenu(id){
	document.getElementById(id).style.visibility = 'visible';
	$("#"+id).show(100);
	initDiv(id);
}
function initDiv(id){
	var setime=null;
	 $("#" + id).hover(function(){
		 window.clearTimeout(setime); 
		 $(this).slideDown(200);
	     	return false;       
	 },function(){
	    	$tis= $(this);
	   		setime= setTimeout(function(){
	     		$tis.slideUp();
	     	},200);  
	    	return false; 
	 });
}
function languageSwitch(){
	SetCookie('language', $(":radio:checked").val());
	window.location.href = '<%=basePath %>';
}
function login() 
{
	$.ajaxSetup({
        async : false  
    });
	if ($("#username").val() == "") 
	{
		$.showtip("请输入用户名!", 2500);
		$("#username").focus();
		return
	} else 
	{
		if ($("#password").val() == "") {
			$.showtip("请输入密码!", 2500);
			$("#password").focus();
			return
		} else 
		{
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
				type : "GET",
				contentType : "application/json",
				url : basePath + "vendbarcode/login.action?username="
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
						SetCookie("ve_code", c.ve_code);
						SetCookie("em_code", c.em_code);
						SetCookie("em_name", c.em_name);
						if ($("#RmbUser").is(':checked')) {
							delCookie('_rmb');
							SetCookie("password", b.password)
						} else {
							SetCookie("_rmb", '0');
							SetCookie("password", "")
						}
						SetCookie("language", b.language);
						var path = basePath;
						document.location.href = path+"vendbarcode/loginSuc.action";
					} else {
						if (c.reason) {
							$.showtip(c.reason, 6000);
					    }
					}
				}
			})
		}
  	}
}
$(function(){
	var language = getCookie("language");
	$("#"+language).attr('checked', 'checked');
	if(language == 'zh_TW'){
		document.getElementById('loginBtn').src = '<%=basePath %>resource/images/tab7_zh_TW.jpg';	
	} else if(language == 'en_US'){
		document.getElementById('loginBtn').src = '<%=basePath %>resource/images/tab7_en_US.jpg';
	}
}); 
