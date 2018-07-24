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
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/GridHeaderFilters.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/data/PagingMemoryProxy.js"></script>
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'common.MultiDbfind'
    ],
    launch: function() {
        Ext.create('erp.view.common.multiDbfind.Viewport');
    }
});
	var key = getUrlParam('key');
	var keyValue = getUrlParam('keyValue');
	var caller = '';
	var keyField = '';
	var condition = key + " like '%" + keyValue + "%'";
	var dbfind = getUrlParam('dbfind');
	var which = 'form';
	var triggerId = getUrlParam('trigger');
	var trigger = parent.Ext.getCmp(triggerId);
	var dbCondition=getUrlParam('dbCondition');
	var dbBaseCondition=getUrlParam('dbBaseCondition');
	var dbGridCondition=getUrlParam('dbGridCondition');
	if(!trigger.ownerCt|| trigger.column){//如果是grid的dbfind
		which = 'grid';
		dbfind=decodeURIComponent(dbfind);
		caller = dbfind.split('|')[0];
		keyField = dbfind.split('|')[1];
		condition = keyField + " like '%" + keyValue + "%'";
		if(dbGridCondition&&dbGridCondition!=null){
			condition=condition+" AND "+ decodeURIComponent(dbGridCondition).replace(/\s{1}IS\s{1}/g, '=');
		}
	} else {
		caller = getUrlParam('caller');
	}
	if(dbCondition&&dbCondition != null){
		condition=condition + " AND " + decodeURIComponent(dbCondition).replace(/\s{1}IS\s{1}/g, '=');
	}
	if(dbBaseCondition&&dbBaseCondition != null){
		condition=condition + " AND " + decodeURIComponent(dbBaseCondition).replace(/\s{1}IS\s{1}/g, '=');
	}
	var page = 1;
	var value = 0;
	var total = 0;
	var count = 0;
	var dataCount = 0;
	var msg = '';
	var height = window.innerHeight;
	var pageSize = parseInt(height*0.7/23);
	var dbfinds = [];
</script>
</head>
<body >
</body>
</html>