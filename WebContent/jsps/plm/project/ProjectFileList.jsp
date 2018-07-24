<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link type="text/css" rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css"/>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ux/css/CheckHeader.css" />
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/CheckColumn.js"></script>
<script type="text/javascript" src="<%=basePath %>jsps/oa/doc/document.js"></script>

<style type="text/css">

.x-grid-tree-node-expanded .x-tree-icon-parent {
    background: url(../../../resource/ext/resources/themes/images/gray/tree/folder-open.gif)  no-repeat !important;;
}

.x-grid-row .x-grid-cell{
	background-color:white;
}

.toolbartext{
	font-size:15px;	
}

.toolbarcontent{
	font-size:15px;
	margin:0 30px 0 0px;
}

.x-form-file-wrap .x-btn button{
	background:#f7f7f7;
}

#file .x-btn-default-small-icon-text-left .x-btn-inner {
    height: 18px;
}

</style>

<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'plm.project.ProjectFileList'
    ],
    launch: function() {
    	Ext.create('erp.view.plm.project.ProjectFileList');//创建视图
    }
});

var formCondition = getUrlParam('formCondition');
formCondition = formCondition.replace(/IS/g,'=');
var prj_id = formCondition.substring(formCondition.indexOf('=') + 1);
formCondition = formCondition.replace(/prj_id/g,'pd_prjid');
var _noc = getUrlParam('_noc')==null?0:_noc;
var canRead = getUrlParam('canRead');
canRead = canRead==null?0:canRead;
var en_admin = '<%=session.getAttribute("en_admin")%>';

function viewMore(){
	Ext.getCmp('prjFileListTreeGrid').viewMore();
}

function downFile(id,folderId){
	Ext.getCmp('prjFileListTreeGrid').downFile(id,folderId);
}

</script>
</head>
<body>
</body>
</html>