<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0,maximum-scale=1.0,user-scalable=no" />
<title>消息详情</title>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<script type="text/javascript" src="<%=basePath%>resource/jquery/jquery-1.11.3.min.js"></script>  	
<base href="<%=basePath%>jsps/mobile/" />
<style type="text/css">

.context{
	/* text-align: center; */
	position:absolute;  
	top: 35%;
	margin:0 auto;
	width:100%;
    height:50%;
    font-size: 15px;   
}
 #ctli{
    /* 这些都不重要 */
    float: left;width: 100%;background: #ffffff;height: 100%;margin-left: -10%;
    padding:0;
    list-style-type:none;
    /* 重点 */
    text-align: center; 
}
li{
	  list-style-type:none;
}
#attach {
  	margin-left:-16px;
	padding-left: 15px;
}
/* 重点 */
p {display: inline-block;text-align: left;}

</style>

</head>
<body id="body" style="background-color:white; background-image: url('images/messagebg.png');">		
	<div class='context'>
		<ul>
	    	<li id="ctli"><p class="ct"></p></li>
		</ul>
	</div>
	<div id="title" style="border-left-width:10px;border-right-width:10px;">
		<div style="margin-bottom:5px;color: gray;text-align: center;"><span style="font-size:18px;color:#2F95DD;font-weight: bold;" class="title"></span></div>
	</div>
	<div id="nonect" style="height: auto; margin-top: 10px">
		<span class="notecontent" style="font-size: 14px;"></span>
	</div>
	<div>
		<ul id="attach"></ul>
	</div>
	<div style="border-width: 1px;border-color: gray;border-style: dashed;"></div>
	<div style="font-size:14px;margin-top:5px; margin-bottom:5px;color: gray;">审批人:<span style="margin-left: 25px;color: black;" class="approver"></span></div>
	<div style="font-size:14px;margin-bottom:5px;color: gray;">审批时间:<span style="margin-left: 7px;color: black;" class="apptime"></span></div>
	
</body>
<script> 
	var basePath="<%=basePath%>";
 	var CONTEXT= '<%=request.getAttribute("context")%>'; 
 	var mfrom= '<%=request.getAttribute("MFROM")%>';
 	var NO_TITLE= '<%=request.getAttribute("NO_TITLE")%>';
 	var NO_APPTIME= '<%=request.getAttribute("NO_APPTIME")%>';
 	var NO_APPROVER= '<%=request.getAttribute("NO_APPROVER")%>';
 	var NO_CONTENT= '<%=request.getAttribute("NO_CONTENT")%>';
	var attachwithnames='<%=request.getAttribute("attachwithnames")%>';
	if(mfrom=='note'){
		$('.title').html(NO_TITLE);
		$('.apptime').html(NO_APPTIME);
		$('.approver').html(NO_APPROVER);
		$('.notecontent').html(NO_CONTENT);
		$('.context').hide();
		if(attachwithnames.length>2){
				var  attach=attachwithnames.substring(1,attachwithnames.length-1).split(",");
				for(var a in attach){
						var attachname=attach[a].split("#")[1];
						var attachid=$.trim(attach[a].split("#")[0]);
						var url=basePath+'common/downloadbyId.action?id='+attachid;
						$('#attach').append('<li style="font-size:14px;"><a href='+url+'>'+attachname+'</a><li>');
					}
			}
	}else {
		$('.apptime').html(NO_APPTIME);
		$('.approver').html(NO_APPROVER);
		$('#body').css("background-image","none");
		$('#title').hide();
		$('#nonect').hide();
		if(CONTEXT == "" || CONTEXT == undefined || CONTEXT == null||CONTEXT=='null'){		
			CONTEXT='<div style="font-weight:normal;font-size:14px;color:black;">不存在消息详情</div>';
		}
		$('.ct').html(CONTEXT);
	}
	
	//移动端监听alert方法，跳到APP原生界面
	var openUrl = function(url,master,winId){
		alert("url:" + url + " master:" + master);
	}

</script>

</html>