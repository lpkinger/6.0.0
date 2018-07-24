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
<script type="text/javascript" src="<%=basePath %>resource/lodopActivex/LodopsFuncs.js"></script> 
<script type="text/javascript" src="<%=basePath %>resource/zebraBrowserPrint/BrowserPrint-1.0.4.min.js"></script> 
<script type="text/javascript" src="<%=basePath %>resource/zebraBrowserPrint/zebraPrint.js"></script>
<object  id="LODOP_OB" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA" width=0 height=0> 
<embed id="LODOP_EM" type="application/x-print-lodop" width=0 height=0></embed>
</object>
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'vendbarcode.BarAcceptNotify.SaveBarcode'
    ],
    launch: function() {
        Ext.create('erp.view.vendbarcode.barAcceptNotify.saveBarcode.Viewport');
    }
});
	var caller = 'vendSaveBarcode'
	var formCondition = getUrlParam('formCondition');
	var gridCondition = getUrlParam('gridCondition');
	var key=getUrlParam('key');
	var inoutno=getUrlParam('inoutno');
	var lps_barcaller = '';
	var lps_obcaller = '';
	var wc = window.parent.caller ;
	var dpi ='';
	var printType='';
</script>
</head>
<body>
</body>
</html>