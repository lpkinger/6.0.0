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
<link rel="stylesheet"
	href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css"
	type="text/css"></link>
 <link rel="stylesheet" href="<%=basePath %>resource/css/main.css"
	type="text/css"></link>
	<link rel="stylesheet" href="auto.css"
	type="text/css"></link> 
<style type="text/css"> 
.x-html-editor-wrap textarea {
	background-color: #FAFAFA;
}
</style>
<script type="text/javascript"
	src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/ux/layout/component/form/ItemSelector.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/ux/layout/component/form/MultiSelect.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/ux/form/MultiSelect.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">	
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'ma.jprocess.AutoJprocess'
    ],
    launch: function() {
    	Ext.create('erp.view.ma.jprocess.AutoJprocess');//创建视图
    }
});
var caller ='AutoProcess!Luanch';
var datalistId=getUrlParam('datalistId');
var CodeCaller="";
var title="";
var params=null;
var type=getUrlParam('type');
if(datalistId && datalistId!='NaN'){
	var datalist = parent.Ext.getCmp(datalistId);
	var datalistStore = datalist.currentRecord;
	CodeCaller=datalistStore.data['ap_caller'] !=undefined ?datalistStore.data['ap_caller']:datalistStore.data['pt_caller'];
	title=datalistStore.data['pt_name']!=undefined ?datalistStore.data['pt_name']:datalistStore.data['ap_name'];
}
if(!CodeCaller) CodeCaller=getUrlParam('caller');
if(type!=1){
    params={
		caller:caller,
		condition:'',
		_noc:1
	};
}
var formCondition = '';
var gridCondition = '';
</script>
</head>
<body>
</body>
</html>