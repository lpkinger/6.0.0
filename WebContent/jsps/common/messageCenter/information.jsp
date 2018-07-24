<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
	String url = request.getRequestURL().toString();
	String basePath = url.substring(0, url.length()
			- request.getRequestURI().length())
			+ request.getContextPath() + "/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=basePath%>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ux/css/CheckHeader.css" /> 
<link rel="stylesheet" href="<%=basePath %>jsps/common/messageCenter/css/centerform.css"  type="text/css"></link>
<script type="text/javascript" src="<%=basePath%>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/GridHeaderFilters.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/CheckColumn.js"></script>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link> 
<style type="text/css">
.readbutton{
	background: #3384FF !important;
	font-weight:bold !important;
	text-align: center;
	color: white !important;	
}
.x-window-body-default{
	background-color: white;
}
.x-btn-default-small .x-btn-inner .x-btn-default-toolbar-small {
	color: white !important;
}
.x-btn-default-toolbar-small .x-btn-inner{
	color: white !important;
}
.search{
	margin-left: 5px;
}
.x-toolbar-footer {
    background: #fff;
    margin-top: 0px;
}
</style>
<script type="text/javascript">
	Ext.Loader.setConfig({
		enabled : true
	});//开启动态加载
	Ext.application({
		name : 'erp',//为应用程序起一个名字,相当于命名空间
		appFolder : basePath + 'app',//app文件夹所在路径
		controllers : [//声明所用到的控制层
		'common.messageCenter.Information' ],
		launch : function() {
			Ext.create('erp.view.common.messageCenter.Information');//创建视图
		}
	});	
	var height = window.innerHeight;
	var width = window.innerWidth;
	if(Ext.isIE){
		height = screen.height*0.73;
		width = screen.width*0.73;
	}
	var pageSize = parseInt(height*0.7/23); 
	var emname = '<%=session.getAttribute("em_name")%>';
	var emuu = '<%=session.getAttribute("em_uu")%>';
	var emcode='<%=session.getAttribute("em_code")%>';
	function openTmpUrl(parent,url, newMaster ,winId){
		var mywindows = Ext.getCmp('mywindows');
		var informationgrid = Ext.getCmp('informationgrid');
		var toMaster=informationgrid.currentmaster
		if(parent){
			openUrl(url);
		}else{
			if(toMaster){
				openMessageUrl(url,'',winId,toMaster)
			}else{
				openUrl(url);
			}
		}
		if(mywindows){
			mywindows.close();
		}
	 	/* if(informationgrid.readStatusData){
			informationgrid.updateReadstatus(informationgrid.readStatusData);
		} */
	} 
	function openTmpMessageUrl(url,appurl,panelId,newMaster){
		var mywindows = Ext.getCmp('mywindows');
		var informationgrid = Ext.getCmp('informationgrid');
		var toMaster=informationgrid.currentmaster
		if(toMaster){
			openMessageUrl(url,appurl,panelId,toMaster)
		}else{
			openUrl(url);
		}
		if(mywindows){
			mywindows.close();
		}
		if(informationgrid.readStatusData){
			informationgrid.updateReadstatus(informationgrid.readStatusData);
		}
	}
		
</script>
</head>
<body>


</body>

</html>