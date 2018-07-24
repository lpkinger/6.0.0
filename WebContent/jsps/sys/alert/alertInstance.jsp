<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
request.setCharacterEncoding("utf-8");
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<link rel="stylesheet" href="<%=basePath %>jsps/common/datalistFilter/datalistFilter.css" type="text/css"></link>
<style>
.clearconditionsp {
    margin-left: 480px !important;
    border: 0px;
    background-color: #F1F1F1 !important;
    background-image: none;
    color: #428bca !important;
    /* margin-right: 0px; */
}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/RowExpander.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/layout/component/form/ItemSelector.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/layout/component/form/MultiSelect.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/form/MultiSelect.js"></script>

<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'sys.alert.AlertInstance'
    ],
    launch: function() {
    	Ext.create('erp.view.sys.alert.AlertInsViewport');//创建视图
    }
});

var caller = 'AlertInstance';
var formCondition = getUrlParam('formCondition');//从url解析参数
var gridCondition = '';
var aii_id;
if(formCondition) {
	formCondition = (formCondition+'').replace(/IS/g,"=");
	var arr = formCondition.split('=');
	for(var i=0;i<arr.length;i++) {
		v = arr[i].replace(/ /g, '');
		if(v == 'aii_id') {
			aii_id = arr[i+1];
		}
	}
}

</script>
</head>
<body >
</body>
</html>