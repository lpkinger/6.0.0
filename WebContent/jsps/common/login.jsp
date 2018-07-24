<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
Object employee = session.getAttribute("employee");
if(employee != null)
	response.sendRedirect(basePath);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title><spring:message code="login.title"></spring:message></title>
<link rel="icon" href="<%=basePath %>resource/images/icon_title.png" type="image/x-icon">
<meta name="keywords" content="ERP,企业供应链,办公自动化,人力资源管理,财务会计管理,工作流程管理 ">
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/css/login.css" />
<script type="text/javascript" src="<%=basePath %>resource/jquery/jquery-1.4.min.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/jquery/jquery.json-2.2.min.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/jquery/showtip.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/other/login.js"></script>
<script type="text/javascript">
	function refreshImg() {
		document.getElementById('validimg').src = '<%=basePath %>jsps/common/vcode.jsp?' + Math.random();//刷新验证码
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
	$(function(){
		var language = getCookie("language");
		$("#"+language).attr('checked', 'checked');
		if(language == 'zh_TW'){
			document.getElementById('loginBtn').src = '<%=basePath %>resource/images/tab7_zh_TW.jpg';	
		} else if(language == 'en_US'){
			document.getElementById('loginBtn').src = '<%=basePath %>resource/images/tab7_en_US.jpg';
		}
	});

</script>
</head>
	<body topmargin="0" leftmargin="0">
		<style type="text/css">
		.tool-box,.tool{height:30px;line-height:30px;}
		.tool-box{background-color:#fffeec;font-size:13px;}
		.tool{width:700px;margin:0 auto;list-style:none}
		.tool li{float:left;height:22px;line-height:22px;margin:4px 0;}
		.tool li.tl,.tool li.tc,.tool li.tr a{background:url(<%=basePath%>resource/images/ie_notice.jpg) no-repeat;}
		.tool li.tl{width:453px;color:#808080;padding-left:28px;}
		.tool li.tc{width:88px;background-position:0 -44px;text-align:center;}
		.tool li.tc a{display:block;color:#fffefb;}
		.tool li.tr{width:71px;color:#a9b5c5;padding-left:60px;}
		.tool li.tr span,.tool li.tr a{float:left;height:22px;}
		.tool li.tr span{width:58px;cursor:pointer}
		.tool li.tr a{width:13px;background-position:0 -22px;}
	    </style>
		<div class="tool-box" id="toolBox" style="display:none">
			<ul class="tool">
				<li class="tl"><em></em>您正在使用的浏览器访问速度比较缓慢，建议您安装Chrome浏览器！</li>
				<li class="tc"><a href="http://dlsw.baidu.com/sw-search-sp/soft/9d/14744/ChromeStandaloneSetup.1418195695.exe" target="_blank">立即下载</a></li>
				<li class="tr">
					<span class="close">不再提示</span>
					<a href="javascript:;" class="close"></a>
				</li>
			</ul>
		</div>
	
		<div align="right" style="padding-top: 5px;padding-right: 15px; position:relative;" class="topDiv">
		    <div style="display:inline-block;width:60px;text-align:center;">
				<span class="topSpan" onmouseover="showMenu('menu4');">手机版</span>
			</div>
			<div id="menu4" class="menuDiv" style="top:22px;right:209px; width:182px;">
				<img id="qrcode" src="<%=basePath %>/resource/images/encode.jpg" width="180px" height="180px">
			</div>
			<div style="display:inline-block;width:60px;text-align:center;">
				<span class="topSpan" onmouseover="showMenu('menu1');"><spring:message code="login.aboutUsoft"></spring:message></span>
			</div>
			<div id="menu1" class="menuDiv" style="top:22px;right:144px; width:55px;">
				<br/>
				<a href="http://www.usoftchina.com" target="_blank"><spring:message code="login.cmpHomePage"></spring:message>&nbsp;&nbsp;</a><br/><br/>
				<a href="http://www.usoftchina.com" target="_blank"><spring:message code="login.proIntroduce"></spring:message>&nbsp;&nbsp;</a><br/><br/>
			</div>
			<div style="display:inline-block;width:60px;text-align:center;">
				&nbsp;&nbsp;<span class="topSpan" onmouseover="showMenu('menu2');"><spring:message code="login.cuService"></spring:message></span>
			</div>
			<div id="menu2" class="menuDiv" style="top:22px;right:79px; width:55px;">
				<br/>
				<a href="http://www.usoftchina.com" target="_blank"><spring:message code="login.onLineService"></spring:message>&nbsp;&nbsp;</a><br/><br/>
				<a href="http://www.usoftchina.com" target="_blank"><spring:message code="login.telService"></spring:message>&nbsp;&nbsp;</a><br/><br/>
				<a href="http://www.usoftchina.com" target="_blank"><spring:message code="login.jsonUs"></spring:message>&nbsp;&nbsp;</a><br/><br/>
			</div>
			<div style="display:inline-block;width:60px;text-align:center;">
				&nbsp;&nbsp;<span class="topSpan" onmouseover="showMenu('menu3');"><spring:message code="login.help"></spring:message></span>
			</div>
			<div id="menu3" class="menuDiv" style="top:22px;right:14px; width:55px;">
				<br/>
				<a href="http://www.usoftchina.com" target="_blank"><spring:message code="login.onLineRefer"></spring:message>&nbsp;&nbsp;</a><br/><br/>
			</div>
		</div>
		<table border="0" width="100%" height="100%" cellspacing="0" cellpadding="0">
		  <tr>
		    <td align="center" valign="middle">
		    <table width="862" border="0" cellspacing="0" cellpadding="0">
		      <tr>
		        <td width="436" valign="top"><img src="<%=basePath %>resource/images/tab1.jpg" width="426" height="120" /></td>
		        <td width="436" valign="top"><img src="<%=basePath %>resource/images/tab222.png" width="436" height="120" /></td>
		      </tr>
		      <tr>
		        <td width="436" align="left" valign="top" background="<%=basePath %>resource/images/tab6.gif" class="td1" height="auto">
	 	<form action="" method="post" name=frm style="margin:0px;">
				<table width="320" border="0" cellspacing="0" cellpadding="0">
		          <tr>
		            <td height="30" align="left" valign="middle" class="td2"><spring:message code="login.username"></spring:message></td>
		            <!-- 设置tab切换的先后顺序 -->
		            <td height="30" align="left" valign="middle"><input tabindex="1" type="text" name="username" id="username" size="20" class="input" value="" onkeydown="keyDown(event)"></td>
		          	<td>
		          		<form:select path="masters" hidefocus="true" id="master">
						    <form:options items="${masters}" itemValue="ma_name" itemLabel="ma_function"/>
						</form:select>
		          	</td>
		          </tr>
		          <tr>
		            <td height="30" align="left" valign="middle" class="td2"><spring:message code="login.password"></spring:message></td>
		            <td height="30" align="left" valign="middle"><input tabindex="2" type="password" name="password" id="password" size="20" class="input" onkeydown="keyDown(event)"></td>
		          	<td class="td3"><input type="checkbox" name="RmbUser" id="RmbUser" class="check" checked="checked" hidefocus="true"><spring:message code="login.rememberPwd"></spring:message></td>
		          </tr>
		          <tr>
               	 	<td width="67px" align="left" valign="middle" class="td2"><div style="width:67px"><spring:message code="login.validcode"></spring:message></div></td>
                	<td align="left" valign="middle"><input tabindex="3" type="text" id="validcode" name="validcode" size="20" class="input" onkeydown="keyDown(event)"/></td>
                	<td width="30"><img id="validimg" src="<%=basePath %>jsps/common/vcode.jsp" onclick="document.getElementById('validimg')
                									.src='<%=basePath %>jsps/common/vcode.jsp?'+Math.random();" style="cursor: pointer;"></img></td>
              	</tr>
              	<tr>
              		<td height="30" class="td2"><spring:message code="login.language"></spring:message></td>
              		<td height="30"><input type="radio" name="language" id="zh_CN" value="zh_CN" checked="checked" onchange="languageSwitch();"/><span class="td2">简体</span>
              			<input type="radio" name="language" id="zh_TW" value="zh_CN" style="margin-left: 26px;" onchange="languageSwitch();"/><span class="td2">繁體</span></td>
		            <td height="30"><input type="radio" name="language" id="en_US" value="zh_CN" onchange="languageSwitch();"/><span class="td2">English</span></td>
              	</tr>
		        </table>
		          <table width="250" border="0" cellspacing="0" cellpadding="0">
		            <tr>
		            <td width="57" height="30" align="center" valign="middle"><img src="<%=basePath %>resource/images/loading.gif" id="loading"/><font id="waitMsg"></font></td>
		              <td width="57" height="30" align="left" valign="middle" > <img src="<%=basePath %>resource/images/tab7.gif" onclick="login();" style="cursor: pointer;" id="loginBtn"></img> </td>
		              <td width="20" height="30" align="center" valign="middle">
		              </td>
		              <td width="90" align="right" valign="middle" class="td3"><a href="<%=basePath %>jsps/common/_init.jsp" target='_blank'><spring:message code="login.registerUrl"></spring:message></a></td>
		            </tr>
		          </table>
		</form>
				  </td>
		        <td width="436" valign="top"><img src="<%=basePath %>resource/images/tab40.png" width="436" height="173" /></td>
		      </tr>
		      <tr>
		        <td width="426" valign="top"><img src="<%=basePath %>resource/images/tab4.gif" width="426" height="100" /></td>
		        <td width="436" valign="top"><img src="<%=basePath %>resource/images/tab5.jpg" width="436" height="100" border="0" usemap="#Map" /></td>
		      </tr>
		    </table>
		  </tr>
		</table>
		<map name="Map" id="Map"><area shape="rect" coords="44,65,226,80" href="http://www.usoftchina.com" target="_blank" /></map>
	</body>
</html>