<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css"/>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript">
var bodystr=window.opener.document.body.innerHTML;
Ext.onReady(function(){
	var headerstr='',bottomstr='',basePath='<%=basePath %>';
	document.title=getUrlParam('title');
	Ext.Ajax.request({
		url :basePath+ 'common/getPrintSet.action',
		method : 'get',
		sync:'false',
		callback : function(options,success,response){
			var res = new Ext.decode(response.responseText);
			if(res.success){
				headerstr=res.data.header;
				bottomstr=res.data.bottom;
				var div_header=document.getElementById('headerdiv');
				var logo=document.getElementById('logo');
				var div_headertext=document.getElementById('textdiv');
				var div_body=document.getElementById('bodydiv');
				var div_bottom=document.getElementById('bottomdiv');
				div_body.innerHTML=bodystr;
				div_headertext.innerHTML=headerstr;
				div_bottom.innerHTML=bottomstr;
				if(res.data.img){
					imgid=res.data.img.split(";")[0];
					logo.src=basePath + 'common/downloadbyId.action?id='+imgid;
				}
				var w=document.getElementById('form').style.width;
				div_header.style.width=w;
				div_bottom.style.width=w;
				setTimeout(function(){
		              window.print();
		              window.close();
				},1000);
			}
		}
	});
});
window.onunload =function(){
	window.opener.location.reload();     
}
function  getUrlParam(name){   
    var reg=new RegExp("(^|&)"+name+"=([^&]*)(&|$)");   
    var r=window.location.search.substr(1).match(reg);   
    if  (r!=null)   return decodeURI(r[2]); 
    return   null;   
} 
function print_f(){
	/* setTimeout(function(){
              window.print();
              window.close();
		},1000); */ 
};
</script>
</head>
<body>
<div id='headerdiv' style="width:auto;">
<span  id='imgdiv' style="display:inline-block;">
<img id='logo' src="" /><!-- style="width:80px;height:50px;vertical-middle;" -->
</span>
<span id='textdiv'  style="display:inline-block"  align="center"></span>
</div>
<div id='bodydiv'></div>
<br />
<div id='bottomdiv' style="width:auto;height:auto" align='center'></div>
<br />
</body>
</html>