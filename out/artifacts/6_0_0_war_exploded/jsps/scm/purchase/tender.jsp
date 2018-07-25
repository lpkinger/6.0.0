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
<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<style type="text/css">

    #release{
		width: 60px !important;
	}
	
	#release-btnEl{
		width: 54px !important;
	}
	
	#release-btnInnerEl{
		width: 54px !important;
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
        'scm.purchase.Tender'
    ],
    launch: function() {
    	Ext.create('erp.view.scm.purchase.Tender');//创建视图
    }
});
var caller = 'Tender';
var formCondition = getUrlParam('formCondition');
var emname = '<%=session.getAttribute("em_name")%>';
if(formCondition){
	var id = formCondition.substr(formCondition.indexOf('idIS')+4).replace(/[' "]/g,"");
}

function getEmptyData(detno){
	var datas = new Array();
	if(!detno){
		detno = 1;
	}
	for(var i=0;i<10;i++){
		var o = new Object();
		o.index=detno+i;
		o.id=null;
		o.qty=null;
		//o.unit='PCS';
		datas.push(o);
	}
	return datas;
}
</script>
</head>
<body>
</body>
</html>