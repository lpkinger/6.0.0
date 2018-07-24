<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="<%=basePath%>resource/ext/4.2/resources/ext-theme-gray/ext-theme-gray-all.css"
	rel="stylesheet" type="text/css" />
 <link href="<%=basePath%>resource/gnt/2.9/sch-gantt-all.css"
	rel="stylesheet" type="text/css" />
 <link href="<%=basePath%>resource/gnt/advanced/advanced.css"
	rel="stylesheet" type="text/css" />
 <link href="<%=basePath%>resource/gnt/2.9/gantt.css"
	rel="stylesheet" type="text/css" />  
<style type="text/css">
 .x-btn-gray {
	background: #d5d5d5;
	background: -moz-linear-gradient(top, #fff 0, #efefef 38%, #d5d5d5 88%);
	background: -webkit-gradient(linear, left top, left bottom, color-stop(0%, #fff),
		color-stop(38%, #efefef), color-stop(88%, #d5d5d5));
	filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#fff',
		endColorstr='#d5d5d5', GradientType=0);
	border-color: #bfbfbf;
	border-radius: 2px;
	vertical-align: bottom;
	text-align: center;
}

.x-btn-gray:hover {
	opacity: .75;
	-ms-filter: "alpha(opacity=75)";
	filter: alpha(opacity = 75);
}
.x-toolbar-default {
  border:none !important;
}

.x-button-icon-save {
    padding-top: 20px;
	background-image: url('<%=basePath%>/resource/images/drink.png')
}
.x-button-icon-submit {
    padding-top: 20px;
	background-image: url('<%=basePath%>/resource/images/basket_put.png')
}
.x-button-icon-check {
    padding-top: 20px;
    background-image: url('<%=basePath%>/resource/images/hourglass.png')
}
.x-button-icon-modify {
    padding-top: 20px;
	background-image: url('<%=basePath%>/resource/images/basket_put.png')
}
.x-button-icon-delete {
    padding-top: 20px;
	background-image:   url('<%=basePath%>/resource/images/basket_remove.png')
}
.x-button-icon-close {
    padding-top: 20px;
	background-image: url('<%=basePath%>/resource/images/icon/trash.png')
}

.x-button-icon-change {
    padding-top: 20px;
    background-image: url('<%=basePath%>/resource/images/arrow_branch.png')
}
</style>	
<script src="<%=basePath%>resource/ext/4.2/ext-all.js"
	type="text/javascript"></script>
<script src="<%=basePath%>resource/gnt/2.9/gnt-all-debug.js"
	type="text/javascript"></script>	
	<script type="text/javascript" src="<%=basePath %>resource/ux/grid/GridHeaderFilters.js">
</script>
<script src="<%=basePath%>app/util/BaseUtil.js" type="text/javascript"></script>
<script src="<%=basePath%>resource/ext/ext-lang-zh_CN.js"
	type="text/javascript"></script>
<script src="<%=basePath%>resource/i18n/i18n.js" type="text/javascript"></script>
<script type="text/javascript">	
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'plm.task.ProjectGanttTask'
    ],
    launch: function() {
    	Ext.create('erp.view.plm.task.ProjectGanttTask');//创建视图
    }
});
var caller = 'ProjectMainTask!Gantt';
var formCondition = getUrlParam('formCondition');
var gridCondition = '';
var datalistId=getUrlParam('datalistId');
var prjplanid=getUrlParam('prjplanid');
var pt_id = getUrlParam('pt_id');
if(!formCondition){
	formCondition = 'pt_idIS' + pt_id;
	if(location.href.indexOf('?')>0){
		location.href= location.href + '&formCondition=' + formCondition;
	}else{
		location.href= location.href + '?formCondition=' + formCondition;
	}
}

if(formCondition&&(prjplanid=='undefined'||!prjplanid)){
	var ptid = formCondition.substring(formCondition.indexOf('pt_idIS')+7);
	Ext.Ajax.request({
		url:basePath + 'common/getFieldData.action',
		method:'post',
		params:{
			field:'pt_prjid',
			caller:'projectmaintask',
			condition:'pt_id=' + ptid
		},
		callback:function(options,success,response){
			var res = Ext.decode(response.responseText);
			if(res.success){
				location.href = location.href + '&prjplanid=' + res.data;
			}else if(res.exceptionInfo){
				showError(res.exceptionInfo);
			}
		}
	});
}
 
</script>
</head>
<body >
</body>
</html>