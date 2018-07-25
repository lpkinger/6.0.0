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
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ux/css/CheckHeader.css" />
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/CheckColumn.js"></script>
<style>
	.x-column-header{
		text-align: center;
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
        'scm.purchase.TenderEstimate'
    ],
    launch: function() {
    	Ext.create('erp.view.scm.purchase.TenderEstimate');//创建视图
    }
});
var caller = 'TenderEstimate';
var formCondition = getUrlParam('formCondition');
var gridCondition = getUrlParam('gridCondition');
var id = formCondition.substr(formCondition.indexOf('idIS')+4).replace(/[' "]/g,"");

function showTrigger(gridId,val,name,x,y){//明细行文本框
	val = unescape(val);
	var store = Ext.getCmp(gridId).store;
	var record = store.getAt(x);
	Ext.MessageBox.minPromptWidth = 600;
    Ext.MessageBox.defaultTextHeight = 200;
    Ext.MessageBox.style= 'background:#e0e0e0;';
    Ext.MessageBox.prompt("详细内容", '',
    function(btn, text) { 
        if (btn == 'ok') {
            if (name&&record) {
                record.set(name, text);
            }
        }
    },
    this, true, //表示文本框为多行文本框    
    val);
}

</script>
</head>
<body>
</body>
</html>